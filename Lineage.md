# Introduction
Deze repo automatiseert het aanmaken en verwijderen van OpenMetadata dashboards en lineage vanuit PowerBI datasets. De flow is YAML-driven: SQL-query's halen rapportmetadata op uit Databricks, YAMLs leggen de mapping vast tussen PowerBI datasets en platinum tables. Het doel is dat mappings centraal beheerd worden en herbruikbaar blijven over omgevingen (DEV/TEST/PROD).

Belangrijkste onderdelen:
- **Automatische lineage pipeline** via `detect_changes_and_apply_lineage` notebook
- YAML-mappings voor PowerBI → Platinum koppeling
- Delete notebook voor cleanup

---

# Automatische Lineage Pipeline

## Overzicht
De hoofdnotebook [detect_changes_and_apply_lineage](detect_changes_and_apply_lineage) detecteert automatisch nieuwe PowerBI REST API data en past lineage toe voor alle gemapte datasets. Ongemapte datasets worden weggeschreven naar `unmapped_datasets.csv` voor manuele review.

### Wat doet de notebook?

| Stap | Cel | Functie |
|------|-----|---------|
| 1 | Change Detection | Vergelijkt `MAX(RawDataStartDatum)` met state file. Stopt als geen nieuwe data. |
| 2 | CSV Export | Exporteert alle PowerBI datasets naar CSV |
| 3 | YAML Diff | Vergelijkt datasets met YAML-mappings, schrijft `unmapped_datasets.csv` |
| 4 | Rapporten Export | Haalt rapporten op via SQL met `RawDataIsCurrent = true` filter |
| 5 | Lineage Toepassen | Maakt/updatet dashboards + lineage edges in OpenMetadata (parallel) |
| 6 | State Bijwerken | Slaat verwerkte datum op voor volgende run |

### Flow diagram
```
+-----------------------------------------------------------------------------+
|                    detect_changes_and_apply_lineage                         |
+-----------------------------------------------------------------------------+
|                                                                             |
|  +--------------+    Geen nieuwe data?    +-----------------+               |
|  | Stap 1:      | ----------------------->| EXIT (no_changes)|              |
|  | Change       |                         +-----------------+               |
|  | Detection    |                                                           |
|  +------+-------+                                                           |
|         | Nieuwe data!                                                      |
|         v                                                                   |
|  +--------------+                                                           |
|  | Stap 2-4:    |--> All_PowerBI_Datasets.csv                               |
|  | CSV Export   |--> unmapped_datasets.csv (voor manuele review)            |
|  | + YAML Diff  |--> Rapporten_mapped_datasets.csv                          |
|  +------+-------+                                                           |
|         |                                                                   |
|         v                                                                   |
|  +--------------+    +-----------------------------------------+            |
|  | Stap 5:      |    | Per rapport:                            |            |
|  | Lineage      |--->|  1. Categoriseer: nieuw / update / skip  |            |
|  | Toepassen    |    |  2. Resolve source tables via YAML       |            |
|  | (parallel)   |    |  3. Create/update dashboard + lineage    |            |
|  +------+-------+    +-----------------------------------------+            |
|         |                                                                   |
|         v                                                                   |
|  +--------------+                                                           |
|  | Stap 6:      |--> .last_processed_date (voor volgende run)               |
|  | State Update |--> EXIT (completed + samenvatting)                        |
|  +--------------+                                                           |
|                                                                             |
+-----------------------------------------------------------------------------+
```

## Belangrijke logica

### SCD Type 2 brontabellen
De tabellen `powerbirestapi_report` en `powerbirestapi_dataset` bevatten historische snapshots (SCD Type 2) met `RawDataStartDatum`/`RawDataEindDatum`. De kolom `RawDataIsCurrent = true` identificeert de actuele rij. De SQL-query filtert hier direct op, waardoor er zonder post-hoc deduplicatie ~1165 unieke rapporten overblijven.

Belangrijke observaties uit de data:
- **DatasetID** wijzigt nooit over snapshots heen (0 van 414 rapporten)
- **WebUrl** wijzigt nooit
- **RapportNaam** wijzigt bij 14 rapporten (hernoemingen in Power BI)

### Identificatiestrategie (name / displayName)
Elk dashboard in OpenMetadata wordt aangemaakt met:
- `name` = `RapportID` (stabiele UUID, wijzigt nooit) → unieke identificatie
- `displayName` = `RapportNaam` (leesbaar, kan wijzigen) → weergave in de UI

Dit voorkomt naamconflicten: twee rapporten met dezelfde naam maar een ander `RapportID` worden correct onderscheiden. Matching bij pre-fetch gebeurt direct op `dashboard.name == RapportID` — zonder URL-regex of fragiele string-matching.

### Drie verwerkingspaden (create / update / skip)
Na pre-fetch van alle bestaande dashboards wordt elk rapport gecategoriseerd:
- **Create**: `RapportID` bestaat nog niet in OpenMetadata → dashboard aanmaken + lineage
- **Update**: `RapportID` bestaat al maar `displayName` (= `RapportNaam`) is gewijzigd → dashboard bijwerken
- **Skip**: `RapportID` bestaat al met identieke `displayName` → niets doen

De update-logica is nodig omdat rapporten over tijd hernoemd kunnen worden terwijl het `RapportID` stabiel blijft.

### Mapbaarheidsfilter
Een dashboard wordt **alleen** aangemaakt als de dataset mappeerbaar is:
- `target_tables` is niet leeg → **mappeerbaar** (directe tabel-mapping)
- `target_datasets` is niet leeg EN resolveert naar tables via Platinum YAML → **mappeerbaar**
- `target_datasets: []` (leeg) → **NIET mappeerbaar**, wordt overgeslagen

### Prioriteitslogica (target_tables vs target_datasets)
```yaml
# Voorbeeld: beide aanwezig
PBIDataset: Epplus betalingen
  target_datasets:
    - r_epplus           # GENEGEERD (want target_tables is niet leeg)
  target_tables:
    - r_epplus.epp_betalingen  # GEBRUIKT
```

Regels:
1. Als `target_tables` niet leeg is → gebruik **alleen** die specifieke tabel(len)
2. Alleen als `target_tables` leeg is → resolve via `target_datasets` + Platinum YAML

## Performance optimalisaties
De notebook is geoptimaliseerd voor grote aantallen rapporten:

| Optimalisatie | Effect |
|---------------|--------|
| `RawDataIsCurrent = true` filter | Direct ~1165 unieke rijen, geen post-hoc deduplicatie |
| Mapbaarheidsfilter | 1165 → ~464 relevante rapporten |
| Pre-fetch + categorisatie | Enkel nieuwe/gewijzigde rapporten verwerkt |
| Parallelle verwerking (ThreadPoolExecutor) | 10 threads |
| Connection pool sizing | Voorkomt connection thrashing |
| Source table caching | 1 lookup per unieke FQN, niet per rapport |
| Logging naar file | Geen stdout overhead, real-time monitoring |

## Logging
Alle output gaat naar een logfile in `Datasets/lineage_run_YYYYMMDD_HHMMSS.log`. Bevat:
- Start configuratie (workers, pool size, aantal rapporten)
- Per rapport: dashboard status + lineage status
- Elke 50 rapporten: voortgangsregel met totalen
- Eindresultaat: samenvatting van alle counters

---

# YAML Mappings Bijwerken

## Wanneer?
Na elke run verschijnen nieuwe, ongemapte datasets in `Datasets/unmapped_datasets.csv`. Deze moeten manueel worden toegevoegd aan `PowerBI_Dataset_Mappings.yml`.

## Hoe?
1. Open `Datasets/unmapped_datasets.csv`
2. Voor elke dataset, bepaal de juiste mapping:
   - Directe tabel: voeg toe met `target_tables`
   - Via platinum dataset: voeg toe met `target_datasets`
   - Niet te mappen: voeg toe met `target_datasets: []` (wordt dan permanent overgeslagen)
3. Voeg de mapping toe aan `PowerBI_Dataset_Mappings.yml`

## Voorbeeld
```yaml
datasets:
  # Directe tabel-mapping (prioriteit)
  - PBIDataset: Epplus betalingen
    target_datasets:
      - r_epplus
    target_tables:
      - r_epplus.epp_betalingen

  # Via platinum dataset
  - PBIDataset: VLAIO Steunster
    target_datasets:
      - steunster
    target_tables: []

  # Bewust niet mappen (wordt overgeslagen)
  - PBIDataset: Test Dataset
    target_datasets: []
    target_tables: []
```

## Na YAML-update: herverwerking forceren
De change detection (stap 1) controleert of er **nieuwe data in de silver tabel** is. Als je enkel de YAML hebt bijgewerkt maar er geen nieuwe REST API data is binnengekomen, wordt de notebook niet uitgevoerd.

**Oplossing:** Verwijder het state-bestand om een herrun te forceren:
```bash
rm Datasets/.last_processed_date
```

Bij de volgende run worden dan alle nieuw gemapte rapporten verwerkt.

---

# Productie Setup

## Job Configuratie
Maak een scheduled Databricks Job aan:

| Setting | Waarde |
|---------|--------|
| Notebook | `detect_changes_and_apply_lineage` |
| Cluster | Cluster met toegang tot silver tables + OpenMetadata |
| Schedule | Wekelijks (bijv. elke maandag 07:00 CET) |
| Timezone | Europe/Brussels |
| Alerts | Optioneel: e-mail bij failure |

### Cron expressie
```
0 0 7 ? * MON *
```
(Elke maandag om 07:00)

## Dynamische padresolutie
Het basispad (`BASE`) wordt dynamisch afgeleid uit de notebook-locatie:
```python
notebook_path = dbutils.notebook.entry_point.getDbutils().notebook().getContext().notebookPath().get()
BASE = '/Workspace' + os.path.dirname(notebook_path)
```
Hierdoor werkt de notebook voor elke gebruiker, ongeacht de workspace-locatie, zolang de mapstructuur behouden blijft.

## Workflow bij nieuwe data
1. **Automatisch:** Job draait wekelijks
2. **Automatisch:** Change detection checkt of er nieuwe data is
3. **Automatisch:** Gemapte rapporten krijgen dashboards + lineage; hernoemde rapporten worden bijgewerkt
4. **Manueel:** Review `unmapped_datasets.csv`, update YAML indien nodig
5. **Manueel:** Verwijder state-bestand, trigger job opnieuw (of wacht tot volgende scheduled run)

## Omgevingsvariabelen (.env)

Maak een `.env` bestand in de root van de repository. **Alle** omgevingsspecifieke waarden staan hierin:

```
# Verplicht
JWT_SECRET=your_openmetadata_jwt
OM_DATABASE_SERVICE=lz02-d-dbw-ccdata-main
OM_DATABASE_SCHEMA=platinum_dev

# Optioneel (hebben DEV-defaults in de notebook als ze ontbreken)
OM_HOST=https://omd.data-services-dev.vlaio.be/api
DASHBOARD_SERVICE=VLAIO_POWERBI_TEST
```

| Variabele | Beschrijving | Verplicht? |
|-----------|-------------|------------|
| `JWT_SECRET` | JWT-token voor OpenMetadata authenticatie | Ja |
| `OM_DATABASE_SERVICE` | Database service naam in OpenMetadata (bv. `lz02-d-dbw-ccdata-main`) | Ja |
| `OM_DATABASE_SCHEMA` | Database schema in OpenMetadata (bv. `platinum_dev`) | Ja |
| `OM_HOST` | OpenMetadata API URL | Nee (default: DEV) |
| `DASHBOARD_SERVICE` | OpenMetadata dashboard service naam | Nee (default: `VLAIO_POWERBI_TEST`) |

## Notebook-variabelen (cel 3)

Naast de `.env` staan er twee variabelen **in de notebook zelf** (cel 3, "Configuratie") die de SQL-tabelnamen bepalen:

```python
SILVER_DATASET_TABLE = 'ccdata_dev.silver.powerbirestapi_dataset'
SILVER_REPORT_TABLE = 'ccdata_dev.silver.powerbirestapi_report'
```

Deze staan bewust niet in `.env` omdat ze een Databricks SQL-context vereisen en niet per gebruiker maar per omgeving wijzigen.

## Overzicht DEV → PROD aanpassingen

| Waar | Variabele | DEV waarde | PROD waarde |
|------|-----------|------------|-------------|
| `.env` | `OM_HOST` | `https://omd.data-services-dev.vlaio.be/api` | `https://omd.data-services.vlaio.be/api` |
| `.env` | `DASHBOARD_SERVICE` | `VLAIO_POWERBI_TEST` | `VLAIO_POWERBI` |
| `.env` | `OM_DATABASE_SERVICE` | `lz02-d-dbw-ccdata-main` | `lz02-p-dbw-ccdata-main` |
| `.env` | `OM_DATABASE_SCHEMA` | `platinum_dev` | `platinum_prod` |
| Notebook cel 3 | `SILVER_DATASET_TABLE` | `ccdata_dev.silver.powerbirestapi_dataset` | `ccdata_prod.silver.powerbirestapi_dataset` |
| Notebook cel 3 | `SILVER_REPORT_TABLE` | `ccdata_dev.silver.powerbirestapi_report` | `ccdata_prod.silver.powerbirestapi_report` |

---

# Cleanup
Notebook: [Delete_OpenMetadata_lineage](Delete_OpenMetadata_lineage)

Verwijdert alle dashboards en bijbehorende lineage edges uit OpenMetadata.

## Gebruik
1. Run cel 1-2 (imports + connectie)
2. Run cel 3 (cleanup) — verwijdert alle dashboards parallel

## Performance
- Parallel met 15 threads
- Logging naar `Datasets/cleanup_run_YYYYMMDD_HHMMSS.log`

---

# Handmatige Notebooks (Legacy)

## Data Exports
Notebook: [Platinum_en_PowerBI_Datasets](Archief/Platinum_en_PowerBI_Datasets)

Deze notebook is voor handmatige exports en wordt niet meer gebruikt in de automatische flow (de exports zijn geïntegreerd in `detect_changes_and_apply_lineage`).

## Create Lineage (Legacy)
Notebook: [Create_OpenMetadata_lineage](Archief/Create_OpenMetadata_lineage)

Originele notebook voor het aanmaken van dashboards + lineage. Vervangen door `detect_changes_and_apply_lineage` die dezelfde logica bevat maar met:
- Change detection
- Automatische CSV exports
- YAML diff + unmapped tracking
- Update-logica voor hernoemde rapporten
- Performance optimalisaties

---

# Getting Started

## Vereisten
- Databricks workspace met toegang tot:
  - `ccdata_dev.silver.powerbirestapi_dataset`
  - `ccdata_dev.silver.powerbirestapi_report`
- OpenMetadata API toegang (JWT token)
- Python packages (als cluster library): `openmetadata-ingestion`, `pyyaml`, `python-dotenv`

## .env configuratie
Maak een `.env` bestand in de root van de repository (zie sectie "Omgevingsvariabelen" voor alle opties):
```
JWT_SECRET=your_openmetadata_jwt
OM_DATABASE_SERVICE=lz02-d-dbw-ccdata-main
OM_DATABASE_SCHEMA=platinum_dev
OM_HOST=https://omd.data-services-dev.vlaio.be/api
DASHBOARD_SERVICE=VLAIO_POWERBI_TEST
```

## Eerste run
1. Zorg dat `.env` correct is geconfigureerd
2. Open `detect_changes_and_apply_lineage` notebook
3. Run alle cellen (of Run All)
4. Controleer `Datasets/unmapped_datasets.csv` voor datasets die nog gemapped moeten worden
5. Update `PowerBI_Dataset_Mappings.yml` indien nodig

---

# Bestandsstructuur
```
Yu-Lan-Batchelor/
├── .env                              # Omgevingsvariabelen (NIET in git)
├── .gitignore
├── README.md                         # Deze documentatie
├── detect_changes_and_apply_lineage  # Hoofdnotebook (automatische pipeline)
├── Delete_OpenMetadata_lineage       # Cleanup notebook
├── Mappings/                         # Alle mapping-gerelateerde bestanden
│   ├── PowerBI_Dataset_Mappings.yml      # PowerBI → target mapping (MANUEEL BEHEERD)
│   ├── Platinum_Dataset_Mappings.yml     # Platinum dataset → tables mapping
│   ├── build_mapping.py                  # Script: match PowerBI naar Platinum
│   ├── fill_powerbi_yaml_from_mappings.py # Script: vul YAML aan vanuit CSV
│   ├── PowerBI_to_Platinum_mapping_confirmed.csv
│   └── PowerBI_to_Platinum_mapping_review.csv
├── Datasets/                         # Gegenereerde output (CSVs, logs, state)
│   ├── .last_processed_date          # State file voor change detection
│   ├── All_PowerBI_Datasets.csv      # Alle PowerBI datasets
│   ├── All_Platinum_Datasets.csv     # Alle Platinum datasets
│   ├── Rapporten_mapped_datasets.csv # Rapporten voor gemapte datasets
│   ├── unmapped_datasets.csv         # Datasets die nog gemapped moeten worden
│   └── lineage_run_*.log             # Run logs
└── Archief/                          # Legacy notebooks en oude scripts
    ├── Create_OpenMetadata_lineage   # Vervangen door detect_changes_and_apply_lineage
    ├── Platinum_en_PowerBI_Datasets  # Exports nu geïntegreerd in hoofdnotebook
    └── ...                           # Oude helper scripts
```

---

# Troubleshooting

## Notebook stopt bij "Geen nieuwe data gedetecteerd"
**Oorzaak:** De `MAX(RawDataStartDatum)` is niet veranderd sinds de laatste run.

**Oplossing:** Verwijder het state-bestand:
```bash
rm Datasets/.last_processed_date
```

## "JWT_SECRET ontbreekt" of "Expired token!"
**Oorzaak:** Het JWT-token in `.env` is verlopen of niet ingevuld.

**Oplossing:** Genereer een nieuw token in OpenMetadata (Settings → Bots → Generate Token), update `JWT_SECRET` in `.env`, en herstart de kernel zodat de client opnieuw wordt opgebouwd.

## Connection pool warnings in de log
**Oorzaak:** `MAX_WORKERS` is groter dan de connection pool size.

**Oplossing:** Zorg dat `POOL_SIZE >= MAX_WORKERS + 5` (al geconfigureerd in de notebook).

## Veel "already_exists" in de resultaten
**Oorzaak:** Dashboards bestaan al in OpenMetadata (door eerdere run).

**Oplossing:** Dit is normaal gedrag. De notebook slaat bestaande dashboards over en voegt alleen lineage toe indien nodig.

## Rapporten worden niet verwerkt ondanks YAML-update
**Oorzaak:** Change detection ziet geen nieuwe data in de silver tabel.

**Oplossing:** Verwijder het state-bestand (zie hierboven).

## 403 Authorization fout op Azure storage
**Oorzaak:** SAS-token van het cluster is verlopen.

**Oplossing:** Herstart het cluster via Compute → cluster → Restart.