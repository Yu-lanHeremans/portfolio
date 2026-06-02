package domein;

import dto.SpelerDTO;
import utils.Gebouw;
import utils.Kleur;
import java.util.*;
import java.util.stream.Collectors;

/**
 * De DomeinController vormt de brug tussen de GUI en de domeinlogica van het spel.
 * Deze klasse beheert de spelers, het spelverloop, de rondes en interacties zoals dobbelsteenworpen en plaatsingen.
 */
public class DomeinController {
    private final SpelerRepository spelerRepository;
    private Spel huidigSpel;
    private Ronde huidigeRonde;
    Random random = new Random();
    
    /** Initialiseert een nieuwe DomeinController en maakt een spelerrepository aan. */
    public DomeinController() {
        spelerRepository = new SpelerRepository();        
    }

    /**
     * Registreert een nieuwe speler met de gegeven gebruikersnaam en geboortejaar.
     *
     * @param gebruikersnaam de naam van de speler
     * @param geboortejaar het geboortejaar van de speler
     */
    public void registreerSpeler(String gebruikersnaam, int geboortejaar) {
        Speler nieuweSpeler = new Speler(gebruikersnaam, geboortejaar);
        spelerRepository.voegToe(nieuweSpeler);
    }

    /**
     * Geeft een lijst van alle geregistreerde spelers in DTO-formaat.
     *
     * @return lijst van SpelerDTO's
     */
    public List<SpelerDTO> geefAlleSpelers() {
        return spelerRepository.geefAlleSpelers().stream()
            .map(speler -> new SpelerDTO(speler.getGebruikersnaam(), speler.getGeboortejaar(), null, 0))
            .collect(Collectors.toList());
    }

    /**
     * Geeft de beschikbare kleuren terug voor spelers.
     *
     * @return lijst van beschikbare kleuren
     */
    public List<Kleur> getBeschikbareKleuren() {
        return Arrays.asList(Kleur.values());
    }

    /**
     * Start een nieuw spel met de gegeven spelers en hun gekozen kleuren.
     *
     * @param spelersKleuren een map van SpelerDTO naar hun gekozen Kleur
     */
    public void startNieuwSpel(Map<SpelerDTO, Kleur> spelersKleuren) {
        if (spelersKleuren == null || spelersKleuren.isEmpty()) {
            throw new IllegalArgumentException("Er moeten spelers en kleuren gekozen worden om het spel te starten.");
        }

        Map<Speler, Kleur> spelersKleurenMap = new HashMap<>();
        for (Map.Entry<SpelerDTO, Kleur> entry : spelersKleuren.entrySet()) {
            SpelerDTO spelerDTO = entry.getKey();
            Speler speler = new Speler(spelerDTO.gebruikersnaam(), spelerDTO.geboortejaar());
            spelersKleurenMap.put(speler, entry.getValue());
        }

        huidigSpel = new Spel(new ArrayList<>(spelersKleurenMap.keySet()), spelersKleurenMap);
    }

    /** Start een nieuwe ronde binnen het huidige spel. */
    public void startRonde() {
        huidigSpel.startNieuweRonde();
        this.huidigeRonde = huidigSpel.getHuidigeRonde();
    }

    /**
     * Geeft het nummer van de huidige ronde.
     *
     * @return ronde nummer
     */
    public int geefRondeNummer() {
        return huidigeRonde.getNummer();
    }

    /** Gooit de dobbelstenen voor de huidige ronde. */
    public void gooiDobbelstenen() {
        huidigeRonde.gooiDobbelstenen();
    }

    /**
     * Geeft een stringrepresentatie van de dobbelsteenworp.
     *
     * @return tekstuele weergave van de worp
     */
    public String waardenWorp() {
        return huidigeRonde.toonWorp();
    }

    /**
     * Bepaalt de rijindex voor het gekozen gebouw op basis van het aantal keer dat het voorvalt in de worp.
     *
     * @param gekozenGebouw het gekozen gebouw
     * @return rij-index voor plaatsing
     */
    public int zetsteenRij(Gebouw gekozenGebouw) {
        int aantalGebouwInDobbelstenen = 0;
        for (Dobbelsteen d : huidigSpel.getHuidigeRonde().getDobbelstenen()) {
            if (d.getWaarde() == gekozenGebouw) {
                aantalGebouwInDobbelstenen++;
            }
        }
        return aantalGebouwInDobbelstenen - 1;
    }

    /**
     * Toont de worp als genummerde lijst.
     *
     * @return genummerde weergave van de worp
     */
    public String toonWorp() {
        StringBuilder sb = new StringBuilder("Huidige worp:\n");
        String[] dobbelstenen = huidigeRonde.toonWorp().split(" ");
        for (int i = 0; i < dobbelstenen.length; i++) {
            sb.append((i + 1)).append(": ").append(dobbelstenen[i]).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Herschudt specifieke dobbelstenen op basis van hun indices.
     *
     * @param nrs lijst van te herschudden dobbelsteenindices
     */
    public void hersmijtDobbelstenen(List<Integer> nrs) {
        huidigeRonde.hersmijtDobbelstenen(nrs);
    }

    /**
     * Geeft de huidige speler in DTO-formaat.
     *
     * @return de huidige speler als SpelerDTO
     */
    public SpelerDTO geefHuidigeSpeler() {
        Speler huidigeSpeler = huidigeRonde.getHuidigeSpeler();
        Kleur kleur = huidigSpel.getSpelersKleuren().get(huidigeSpeler);
        Integer zetStenen = huidigSpel.getAantalZetstenen();
        return new SpelerDTO(huidigeSpeler.getGebruikersnaam(), huidigeSpeler.getGeboortejaar(), kleur, zetStenen);
    }

    /**
     * Geeft de spelinformatie als string.
     *
     * @return beschrijving van het spel
     */
    public String getSpelInfo() {
        if (huidigSpel == null) {
            throw new IllegalStateException("Er is nog geen spel gestart.");
        }
        return huidigSpel.toonSpelInfo();
    }

    /**
     * Geeft een lijst van spelers die deelnemen aan het spel.
     *
     * @return lijst van SpelerDTO's
     */
    public List<SpelerDTO> geefSpelersVoorSpel() {
        if (huidigSpel == null) {
            throw new IllegalStateException("Er is nog geen spel gestart.");
        }
        List<SpelerDTO> spelerDTOs = new ArrayList<>();
        for (Speler speler : huidigSpel.getSpelers()) {
            Kleur kleur = huidigSpel.getSpelersKleuren().get(speler);
            Integer zetStenen = huidigSpel.getAantalZetstenen();
            spelerDTOs.add(new SpelerDTO(speler.getGebruikersnaam(), speler.getGeboortejaar(), kleur, zetStenen));
        }
        return spelerDTOs;
    }

    /**
     * Geeft de startspeler van het spel.
     *
     * @return SpelerDTO van de startspeler
     */
    public SpelerDTO geefStartSpeler() {
        if (huidigSpel == null) {
            throw new IllegalStateException("Er is nog geen spel gestart.");
        }
        Speler startSpeler = huidigSpel.getStartSpeler();
        Kleur kleur = huidigSpel.getSpelersKleuren().get(startSpeler);
        Integer zetStenen = huidigSpel.getAantalZetstenen();
        return new SpelerDTO(startSpeler.getGebruikersnaam(), startSpeler.getGeboortejaar(), kleur, zetStenen);
    }

    /**
     * Geeft de lijst van beschikbare bonusfiches.
     *
     * @return lijst van bonusfiches
     */
    public List<Integer> geefBonusfiches() {
        return huidigSpel.getBonusFiches();
    }

    /** Vult het fichesgebied op het spelbord. */
    public void vulFichesGebied() {
        huidigSpel.getSpelbord().vulFichesgebied();
    }

    /**
     * Geeft het fichesgebied van het spelbord.
     *
     * @return array van fiches
     */
    public String[] geefFichesGebied() {
        return huidigSpel.getSpelbord().getFichesGebied();
    }

    /**
     * Toont de huidige staat van het spelbord.
     *
     * @return tekstuele weergave van het spelbord
     */
    public String toonBord() {
        return huidigSpel.getSpelbord().toString();
    }

    /**
     * Geeft het aantal worpen dat de huidige speler heeft gedaan.
     *
     * @return aantal worpen
     */
    public int huidigeAantalWorpen() {
        Speler huidigeSpeler = huidigeRonde.getHuidigeSpeler();
        return huidigeRonde.getSpelerWorpen().get(huidigeSpeler);
    }

    /**
     * Geeft een map van unieke gebouwen in de huidige worp.
     *
     * @return map van index naar gebouw
     */
    public Map<Integer, Gebouw> getUniekeGebouwenUitWorp() {
        String worp = huidigeRonde.toonWorp();
        Set<Gebouw> uniekeGebouwen = new HashSet<>();
        String[] delen = worp.replace("Huidige worp: ", "").split(" ");
        for (String deel : delen) {
            try {
                Gebouw gebouw = Gebouw.valueOf(deel.toUpperCase());
                uniekeGebouwen.add(gebouw);
            } catch (IllegalArgumentException e) {
                System.out.println("Ongeldig gebouw gevonden: " + deel);
            }
        }
        Map<Integer, Gebouw> gebouwMap = new HashMap<>();
        int index = 1;
        for (Gebouw gebouw : uniekeGebouwen) {
            gebouwMap.put(index++, gebouw);
        }
        return gebouwMap;
    }

    /**
     * Plaatst een zetsteen voor de opgegeven speler en gebouw.
     *
     * @param huidigeSpeler de speler die aan zet is
     * @param gekozenGebouw het gebouw waarop gezet wordt
     */
    public void plaatsZetsteen(SpelerDTO huidigeSpeler, Gebouw gekozenGebouw) {
        huidigSpel.plaatsZetsteen(huidigeSpeler, gekozenGebouw);
    }

    /**
     * Beëindigt het huidige spel en slaat de resultaten op.
     *
     * @return eindresultaat als string
     */
    public String eindigSpel() {
        String res = this.huidigSpel.eindigSpel();
        List<Speler> spelers = this.huidigSpel.getSpelers();
        for (Speler speler : spelers) {
            this.spelerRepository.update(speler);
        }
        return res;
    }

    /**
     * Geeft het aantal resterende zetstenen in het spel.
     *
     * @return aantal zetstenen
     */
    public int aantalZetstenen() {
        return huidigSpel.getAantalZetstenen();
    }

    /**
     * Geeft het gebied met gebouwtegels en punten.
     *
     * @return 2D-array van GebouwTegels
     */
    public GebouwTegel[][] geefGebouwPuntenGebied() {
        return huidigSpel.getSpelbord().getGebouwpuntenGebied();
    }

   
	
	public DobbelVak[][][] geefDobbelGebied() {
		return huidigSpel.getSpelbord().getDobbelGebied();
	}

    /**
     * Geeft de kleuren van een specifieke tegel.
     *
     * @param col kolomindex
     * @param row rijindex
     * @return lijst van kleuren op de tegel
     */
    public List<Kleur> geefGebouwTegelKleur(int col, int row) {
        GebouwTegel[][] tegel = huidigSpel.getSpelbord().getGebouwpuntenGebied();
        return tegel[col][row].getKleuren();
    }

    /** Zet de volgende speler aan de beurt in de huidige ronde. */
    public void volgendeAanBeurt() {
        this.huidigeRonde.volgendeSpeler();
    }

    /**
     * Geeft het aantal spelers in de huidige ronde.
     *
     * @return aantal spelers
     */
    public int aantalSpelers() {
        return this.huidigeRonde.getAantalSpelers();
    }

    /** Zet het aantal worpen per speler terug naar nul. */
    public void resetSpelerWorpen() {
        this.huidigeRonde.resetSpelerWorpen();
    }

    /**
     * Geeft de score van een speler op basis van spelinformatie.
     *
     * @param huidigeSpeler de speler waarvan de score gevraagd wordt
     * @return score van de speler
     */
    public int geefSpelerScore(SpelerDTO huidigeSpeler) {
        return this.huidigSpel.geefSpelerScore(huidigeSpeler);
    }

    /** Bepaalt beloningen voor de spelers aan het einde van het spel. */
    public void bepaalBeloningen() {
        this.huidigSpel.bepaalBeloningen();
    }

    /** Evalueert het puntengebied met gebouwtegels voor eindscore. */
    public void evalueerGebouwPuntengebied() {
        huidigSpel.evalueerGebouwPuntengebied();
    }
}
