# dq_framework2 - Data Quality test framework

Dit project automatiseert het aanmaken, synchroniseren en uitvoeren van OpenMetadata Data Quality testcases op basis van een declaratieve rule catalog.

## Wat dit framework doet

1. Leest scopes (databases en schemas) uit de rule catalog.
2. Haalt tabellen en kolommen op via OpenMetadata.
3. Bouwt testcases vanuit regels en parameters.
4. Upsert testcases en (optioneel) verwijdert stale tests.
5. Triggert ingestion pipelines indien nodig.
6. Schrijft resultaten en signatures naar outputbestanden.

## Belangrijkste bestanden

- Orchestratie: [src/dq_framework2/main.py](src/dq_framework2/main.py)
- Regelcatalogus: [src/dq_framework2/rule_catalog.yaml](src/dq_framework2/rule_catalog.yaml)
- Catalog loader: [src/dq_framework2/rule_catalog.py](src/dq_framework2/rule_catalog.py)
- Rule engine: [src/dq_framework2/rule_engine.py](src/dq_framework2/rule_engine.py)
- Builders: [src/dq_framework2/rule_builders.py](src/dq_framework2/rule_builders.py)
- OpenMetadata API en pipelines: [src/dq_framework2/services.py](src/dq_framework2/services.py)
- Configuratie: [src/dq_framework2/settings.py](src/dq_framework2/settings.py)
- Hulpfuncties: [src/dq_framework2/helpers.py](src/dq_framework2/helpers.py)
- Rapportering (coverage + dashboard): [src/dq_framework2/Rapportering](src/dq_framework2/Rapportering)
- Documentatie: [src/Documentatie](src/Documentatie)

## Rapportering

De map [src/dq_framework2/Rapportering](src/dq_framework2/Rapportering) bevat de coverage-module en het Data Quality Dashboard:

- **Coverage runner** (`coverage_runner.py`, `coverage_core.py`): berekent test-dekking op kolom- en tabelniveau en schrijft Delta snapshots.
- **Data Quality Dashboard**: Databricks Lakeview dashboard met overzicht van testresultaten, pass rates en coverage.
- **Testdata ophalen**: notebook dat de DQ-resultaten uit OpenMetadata inleest naar Delta tabellen.

Zie ook: [Coverage rapportering](#coverage-rapportering) hieronder.

## Databricks DQX

De map [Databricks_DQX](Databricks_DQX) bevat notebooks met een introductie en handleiding voor het Databricks Data Quality Expectations (DQX) framework. Dit is een alternatieve/complementaire aanpak voor datakwaliteit binnen Databricks, los van OpenMetadata. De notebooks behandelen:

- Introductie en setup van DQX
- Regels definiëren in code
- Volledige handleiding
- Bulk regelgeneratie

## Archief

De map [Archief](Archief) bevat oude scripts en API calls die kunnen dienen als simpele voorbeelden om met de OpenMetadata API te werken. Dit is referentiemateriaal en niet actief in gebruik.

## Documentatie

Gedetailleerde documentatie staat in [src/Documentatie](src/Documentatie):

- [DQ_FRAMEWORK2_OVERVIEW.md](src/Documentatie/DQ_FRAMEWORK2_OVERVIEW.md) — Volledige workflow stap-voor-stap met diagram en deep dive per testtype.
- [GEBUIKSHANLEIDING.md](src/Documentatie/GEBUIKSHANLEIDING.md) — Praktische gids voor het schrijven en beheren van tests.
- [DQ_FRAMEWORK2_CHEATSHEET.md](src/Documentatie/DQ_FRAMEWORK2_CHEATSHEET.md) — Snelle referentie voor testtypes, placeholders en commando's.

## Snelstart (lokaal of Databricks)

Installeer enkel de dependencies die nodig zijn om OpenMetadata te gebruiken:

```bash
pip install python-dotenv PyYAML openmetadata-ingestion==1.12.6
```

### Databricks setup

Installeer dezelfde packages op je Databricks cluster:

1. Ga naar **Compute**.
2. Klik daar op je cluster.
3. Ga naar **Libraries**.
4. Kies **Install New**.
5. Voeg deze PyPI packages toe:
	- `python-dotenv`
	- `PyYAML`
	- `openmetadata-ingestion==1.12.6`

Let op: de versie van `openmetadata-ingestion` moet gelijk zijn aan de huidige
OpenMetadata versie in jouw omgeving. Gebruik dus dezelfde major/minor versie
als de OpenMetadata server om compatibiliteitsproblemen te vermijden.

Maak een `.env` in de projectroot met minstens:

```env
JWT_SECRET=...
OPENMETADATA_HOST=https://your-openmetadata-host/api
OPENMETADATA_VERIFY_SSL=true
```

Start daarna de orchestratie:

```bash
"C:/Program Files/Python313/python.exe" src/dq_framework2/main.py
```

## Configuratie via environment variables

De volledige configuratie staat in [src/dq_framework2/settings.py](src/dq_framework2/settings.py). Hieronder de belangrijkste env vars:

OpenMetadata:
- `JWT_SECRET`: bearer token secret.
- `OPENMETADATA_HOST`: base URL inclusief `/api`.
- `CA_BUNDLE_PATH`: optionele CA bundle path.
- `OPENMETADATA_VERIFY_SSL`: forceer SSL verificatie (`true`/`false`).

Parallelisme en timeouts:
- `DQ_PARALLEL_TABLE_WORKERS`: aantal tabel workers (default 6).
- `DQ_PARALLEL_DATASET_WORKERS`: aantal dataset workers (default 4).
- `DQ_DATASET_POOL_TIMEOUT_SECONDS`: dataset timeout (default 600).
- `DQ_TABLE_POOL_TIMEOUT_SECONDS`: tabel timeout (default 3600).

Sync gedrag:
- `DQ_SYNC_MODE`: verwijder stale testcases als `true` (default `true`).
- `DQ_HARD_DELETE_STALE_TESTS`: hard delete in plaats van soft delete (default `true`).
- `DQ_MIGRATE_LEGACY_PIPELINES`: migreer legacy pipeline namen (default `true`).

Pipeline trigger window:
- `DQ_RECENT_SUCCESS_WINDOW_HOURS`: skip trigger bij recente success (default 24).

Output en logging:
- `DQ2_OUTPUT_DIR`: basis outputmap (default `Output` onder `src/dq_framework2`).
- `DQ2_SIGNATURE_FILE`: signatures output.
- `DQ2_RESULT_FILE`: result output.
- `DQ2_LOG_FILE`: logbestand.

Retry gedrag:
- `DQ_TRIGGER_RETRY_ATTEMPTS`: aantal trigger retries (default 3).
- `DQ_TRIGGER_RETRY_DELAY_SECONDS`: delay tussen retries (default 5).

## Tests toevoegen (korte gids)

Waar je een regel zet:
1. **Globaal**: `generic_rules`.
2. **Schema**: `databases.<database>.<schema>`.
3. **Tabel**: `databases.<database>.<schema>.table_rules.<table>`.

Vuistregels:
- Gebruik `table_tests` voor regels op tabelniveau.
- Gebruik `required_not_null_columns` voor verplichte kolommen.
- Gebruik `column_rules` voor expliciete kolommen.
- Gebruik `column_pattern_rules` voor herbruikbare patronen.
- Gebruik `sets` voor gedeelde waardelijsten.

Meer details, voorbeelden en placeholders vind je in:
- [src/Documentatie/GEBUIKSHANLEIDING.md](src/Documentatie/GEBUIKSHANLEIDING.md)
- [src/Documentatie/DQ_FRAMEWORK2_OVERVIEW.md](src/Documentatie/DQ_FRAMEWORK2_OVERVIEW.md)
- [src/Documentatie](src/Documentatie)

## Outputbestanden

Standaard worden deze bestanden geschreven onder `src/dq_framework2/Output`:
1. `dq_testcase_signatures_framework2.json`
2. `dq_results_framework2.txt`
3. `dq_framework2.log`

## Coverage rapportering

De coverage module berekent kolom- en tabelcoverage en schrijft Delta snapshots.
De entrypoint staat in [src/dq_framework2/Rapportering/coverage_runner.py](src/dq_framework2/Rapportering/coverage_runner.py).

**Belangrijk:** De coverage is uitsluitend gebaseerd op tests die door dit framework worden geproduceerd (via `rule_catalog.yaml`). Tests die handmatig via de OpenMetadata UI zijn aangemaakt, worden **niet** meegenomen in de coverageberekening. Het doel is inzicht te geven in wat het framework afdekt, niet de totale testdekking over alle bronnen.

**Waarom niet op basis van OpenMetadata data?** Een alternatieve aanpak zou zijn om de coverage te berekenen aan de hand van de testcases die daadwerkelijk op OpenMetadata staan. Dit is in principe mogelijk met dezelfde data die de OVERVIEW-stap gebruikt. Het probleem is echter dat kolomcoverage dan lastig exact te bepalen is: SQL-gebaseerde tests (`custom_sql`, `start_before_end`, etc.) opereren op tabelniveau en zijn niet aan één specifieke kolom gebonden. Door de coverage te berekenen vanuit de rule catalog + builders compile-flow weet het framework precies welke kolommen worden geraakt, omdat het zelf de tests opbouwt en de kolomcontext kent.

Voorbeeld (in een notebook of script met Spark):

```python
from dq_framework2.Rapportering.coverage_runner import run_coverage_and_write_tables

run_coverage_and_write_tables()
```

## Troubleshooting FAQ

### Test wordt niet aangemaakt

**Symptoom:** Een regel staat in `rule_catalog.yaml`, maar de test verschijnt niet in OpenMetadata.

**Mogelijke oorzaken:**
1. **Kolom bestaat niet op de tabel.** Kolom-level tests en table tests met `required_columns` worden overgeslagen als de kolom ontbreekt. Check de kolomnamen in OpenMetadata (hoofdlettergevoelig).
2. **Tabel valt buiten scope.** Alleen tabellen in schema's die expliciet onder `databases.<db>.<schema>` staan worden verwerkt.
3. **Pattern matcht niet.** Bij `column_pattern_rules` wordt `fnmatch` (glob) gebruikt — `*Email` matcht `BehandelaarEMail` maar niet `email_adres`.
4. **Guard-conditie niet voldaan.** Tests als `start_before_end` en `no_overlapping_periods` vereisen specifieke kolommen (BK/PK). Controleer de log voor "skipped" meldingen.

**Diagnose:** Zoek in `dq_framework2.log` naar de tabelnaam — overgeslagen tests worden expliciet gelogd.

### Pipeline trigger faalt

**Symptoom:** Tests worden aangemaakt maar de pipeline start niet.

**Mogelijke oorzaken:**
1. **Pipeline naam te lang.** Kubernetes limiet is 63 tekens. Het framework kort namen automatisch in, maar bij zeer lange schema+tabelnamen kan dit falen. Check de log op "pipeline name too long".
2. **OpenMetadata API timeout.** Verhoog `DQ_TRIGGER_RETRY_ATTEMPTS` en `DQ_TRIGGER_RETRY_DELAY_SECONDS`.
3. **Pipeline niet gekoppeld aan TestSuite.** Gebruik `DQ_MIGRATE_LEGACY_PIPELINES=true` om oude pipelines te migreren.

**Diagnose:** De trigger response wordt gelogd. Zoek op "trigger" in het logbestand.

### Onverwachte stale deletes

**Symptoom:** Tests verdwijnen uit OpenMetadata na een run.

**Mogelijke oorzaken:**
1. **Testnaam gewijzigd.** Het framework identificeert tests op naam. Als je `name` in YAML wijzigt, wordt de oude test als "stale" gezien en verwijderd, en een nieuwe aangemaakt.
2. **Regex escaping veranderd.** Subtiele wijzigingen in regex patronen veranderen de signature, waardoor sync denkt dat de test nieuw is.
3. **Schema uit scope gehaald.** Als je een schema verwijdert uit `databases`, worden bijbehorende tests als stale gemarkeerd.

**Oplossing:** Zet `DQ_SYNC_MODE=false` om stale deletes tijdelijk uit te schakelen terwijl je wijzigingen test.

### Signature mismatch / framework runt steeds opnieuw

**Symptoom:** Het framework upsert en triggert elke run, ook als er niets veranderd is.

**Mogelijke oorzaken:**
1. **Signature file verwijderd of corrupt.** Het bestand `dq_testcase_signatures_framework2.json` moet bewaard blijven tussen runs.
2. **Niet-deterministische waarden.** Als een regel dynamische waarden bevat (bv. `CURRENT_DATE` in een parameter), verandert de signature elke keer.
3. **Env var gewijzigd.** Sommige settings beïnvloeden de testobjecten indirect.

**Oplossing:** Controleer of de signature file stabiel is. Bij een schone start: verwijder het bestand en laat het framework alles opnieuw opbouwen.

### Coverage cijfers kloppen niet met OpenMetadata

**Symptoom:** Het dashboard toont een ander aantal tests dan de OpenMetadata UI.

**Mogelijke oorzaken:**
1. **UI-tests niet meegenomen.** De coverage telt alleen framework-gegenereerde tests (uit `rule_catalog.yaml`). Handmatig aangemaakte tests in de UI worden niet meegeteld.
2. **Table-level tests missen `table_name`.** Dit was een bekende bug in de view (gefixed: `entity_fqn_parts >= 4`). Controleer of de view `v_dq_test_case_result_history` up-to-date is.
3. **Tests uit andere databases/services.** De ingest haalt alle testcases op, inclusief tests op andere services (bv. MSDEA). Filter in het dashboard op het juiste schema.

### Import- of connectiefouten

**Symptoom:** `ModuleNotFoundError` of `ConnectionError` bij het starten.

**Mogelijke oorzaken:**
1. **`openmetadata-ingestion` niet geïnstalleerd of verkeerde versie.** De versie moet exact overeenkomen met de OpenMetadata server versie.
2. **`.env` bestand ontbreekt of pad verkeerd.** Het framework verwacht `.env` in de projectroot.
3. **SSL certificaat probleem.** Zet `OPENMETADATA_VERIFY_SSL=false` voor debug, of configureer `CA_BUNDLE_PATH` correct.
4. **Op Databricks: packages niet op cluster geïnstalleerd.** Installeer via Compute > Libraries (zie Snelstart).

## API referenties

OpenMetadata documentatie (v1.11.x):
- https://docs.open-metadata.org/v1.11.x/api-reference
- https://docs.open-metadata.org/v1.11.x/api-reference/data-quality/test-cases
- https://docs.open-metadata.org/v1.11.x/api-reference/data-quality/test-definitions
- https://docs.open-metadata.org/v1.11.x/api-reference/data-quality/test-suites

Lokale OpenAPI specificatie:
- [omd_openapi/openapi.json](omd_openapi/openapi.json)

Belangrijkste endpointgroepen:
- `/v1/dataQuality/testCases`
- `/v1/dataQuality/testSuites`
- `/v1/dataQuality/testDefinitions`
- `/v1/tables` en `/v1/tables/name/{fqn}`
- `/v1/services/ingestionPipelines`
- `/v1/services/ingestionPipelines/deploy/{id}`
- `/v1/services/ingestionPipelines/trigger/{id}`

## Contribute

Bijdragen zijn welkom. Richtlijnen:
1. Pas regels aan in `rule_catalog.yaml` waar mogelijk.
2. Voeg nieuwe testtypes toe in `rule_builders.py` als YAML niet volstaat.
3. Houd testnamen stabiel om onverwachte stale deletes te vermijden.
4. Update documentatie in [src/Documentatie](src/Documentatie) bij functionele wijzigingen.
