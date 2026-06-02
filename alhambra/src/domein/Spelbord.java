package domein;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import utils.Gebouw;
import utils.Kleur;

/**
 * Deze klasse stekt het speelbord voor van Alhambra, waarin het gebied voor 
 * gebouwen, fiches en dobbelstenen wordt beheerd en ook de spelers, hun scores, 
 * en de voortgang van het spel.
 */
public class Spelbord {
    
    private GebouwTegel[][] gebouwpuntenGebied;
    private String[] fichesGebied;
    private DobbelVak[][][] dobbelGebied;
    private List<Gebouw> gebouwen;
    private List<Integer> bonusFiches;
    private int ronde;
    private Random random;
    private List<Speler> spelers;
    private Map<Speler, Boolean> startFiche;
    private Map<Speler, Kleur> spelersKleuren;
    private Map<Speler, Integer> spelerScores;
    private Map<Integer, Map<Integer, List<Integer>>> puntenMapPerRonde;

    /**
     * Constructor voor het initiëren van een Spelbord.
     * @param bonusFiches De lijst van bonus fiches die beschikbaar zijn op het bord.
     * @param spelers De lijst van spelers die deelnemen aan het spel.
     * @param spelersKleuren De kleuren toegewezen aan elke speler.
     */
    public Spelbord(List<Integer> bonusFiches, List<Speler> spelers, Map<Speler, Kleur> spelersKleuren) {
        this.random = new Random();
        this.ronde = 0;
        setSpelers(spelers);
        setSpelersKleuren(spelersKleuren);
        initSpelerScores();
        initGebouwpuntenGebied();
        setFichesGebied();
        setDobbelGebied();
        setGebouwen();
        setBonusFiches(bonusFiches);
        initStartFiche();
        initPuntenMapPerRonde();
    }

    /**
     * Initialiseert de speler scores bij de start van het spel.
     */
    private void initSpelerScores() {
        this.spelerScores = new HashMap<>();
        for(Speler speler : spelers) {
            this.spelerScores.put(speler, 0);
        }
    }

    /**
     * Verhoogt de score van een speler met een bepaald aantal punten.
     * @param speler De speler waarvan de score verhoogd moet worden.
     * @param punten Het aantal punten om toe te voegen aan de score van de speler.
     */
    private void setSpelerScore(Speler speler, int punten) {
        int score = this.spelerScores.get(speler);
        this.spelerScores.put(speler, score + punten);
    }

    /**
     * Haalt de scores van alle spelers op.
     * @return Een map van spelers naar hun huidige score.
     */
    public Map<Speler, Integer> getSpelerscores() {
        return this.spelerScores;
    }

    /**
     * Stelt de kleuren van de spelers in.
     * @param spelersKleuren Een map van spelers naar hun toegewezen kleur.
     */
    private void setSpelersKleuren(Map<Speler, Kleur> spelersKleuren) {
        this.spelersKleuren = spelersKleuren;
    }

    /**
     * Initialiseert het start fiche voor elke speler (allemaal op false).
     */
    private void initStartFiche() {
        this.startFiche = new HashMap<Speler, Boolean>();

        for (Speler speler : spelers) {
            startFiche.put(speler, false);
        }
    }

    /**
     * Haalt de start fiches op voor alle spelers.
     * @return Een map van spelers naar hun start fiche status.
     */
    public Map<Speler, Boolean> getStartFiche() {
        return this.startFiche;
    }

    /**
     * Wijst een start fiche toe aan de opgegeven speler en zet andere spelers op false.
     * @param startSpeler De speler die het start fiche krijgt.
     */
    public void geefStartFiche(Speler startSpeler) {
        for (Speler speler : spelers) {
            if (speler.equals(startSpeler)) {
                startFiche.put(startSpeler, true);
            } else {
                startFiche.put(speler, false);
            }
        }
    }

    /**
     * Stelt de lijst van spelers in.
     * @param spelers De lijst van spelers.
     */
    private void setSpelers(List<Speler> spelers) {
        this.spelers = spelers;
    }

    /**
     * Initialiseert het gebouwpunten gebied met een matrix van GebouwTegel-objecten.
     */
    private void initGebouwpuntenGebied() {
        this.gebouwpuntenGebied = new GebouwTegel[6][12];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 12; j++) {
                gebouwpuntenGebied[i][j] = new GebouwTegel();
            }
        }
    }

    /**
     * Haalt het gebouwpunten gebied op.
     * @return Het gebouwpunten gebied als een matrix van GebouwTegel-objecten.
     */
    public GebouwTegel[][] getGebouwpuntenGebied() {
        return gebouwpuntenGebied;
    }

    /**
     * Voegt een kleur toe aan een specifieke tegel in het gebouwpunten gebied.
     * @param table De rij van de tegel.
     * @param punten Het punt van de tegel.
     * @param kleur De kleur die toegevoegd moet worden.
     * @throws IllegalArgumentException Wanneer de indexen ongeldig zijn.
     */
    public void voegKleurToeAanTegel(int table, int punten, Kleur kleur) {
        if (table < 0 || table >= 6 || punten < 0 || punten >= 12) {
            throw new IllegalArgumentException("Ongeldige index voor GebouwTegel.");
        }        
        int vorigePositie = -1;
        for (int row = 0; row < 12; row++) {
            if (gebouwpuntenGebied[table][row].heeftKleur(kleur)) {
                vorigePositie = row;
                gebouwpuntenGebied[table][row].verwijderKleur(kleur);
                break;
            }
        }        
        int nieuwePositie = (vorigePositie == -1) ? punten - 1 : Math.min(11, vorigePositie + punten);        
        gebouwpuntenGebied[table][nieuwePositie].voegKleurToe(kleur);
    }

    /**
     * Stelt het fiches gebied in met lege waarden.
     */
    public void setFichesGebied() {
        this.fichesGebied = new String[6];
        Arrays.fill(this.fichesGebied, "");
    }

    /**
     * Haalt de lijst van bonus fiches op.
     * @return De lijst van bonus fiches.
     */
    public List<Integer> getBonusFiches() {
        return bonusFiches;
    }

    /**
     * Stelt de lijst van bonus fiches in.
     * @param bonusFiches De lijst van bonus fiches.
     */
    public void setBonusFiches(List<Integer> bonusFiches) {
        this.bonusFiches = bonusFiches;
    }

    /**
     * Vult het fiches gebied met fiches, inclusief het start fiche.
     */
    public void vulFichesgebied() {
        Arrays.fill(fichesGebied, "");
        int startFichePositie = -1;

        if (ronde == 1 || ronde == 2) {
            startFichePositie = random.nextInt(6); 
            fichesGebied[startFichePositie] = "STRT";
        }

        for (int i = 0; i < fichesGebied.length; i++) {
            if (startFichePositie != i) {
                if (!bonusFiches.isEmpty()) {
                    fichesGebied[i] = String.valueOf(bonusFiches.remove(0));
                }
            }
        }
    }

    /**
     * Haalt de huidige ronde op.
     * @return De huidige ronde.
     */
    public int getRonde() {
        return ronde;
    }

    /**
     * Verhoogt de ronde met 1 en vult het fiches gebied opnieuw.
     */
    public void volgendeRonde() {
        this.ronde += 1;
        vulFichesgebied();
    }

    /**
     * Initialiseert het dobbel gebied met een matrix van DobbelVak-objecten.
     */
    public void setDobbelGebied() {
        this.dobbelGebied = new DobbelVak[6][8][3];
        for (int table = 0; table < 6; table++) { 
            for (int i = 0; i < 8; i++) {
                int nummer = i + 1;  
                for (int j = 0; j < 3; j++) {
                    dobbelGebied[table][i][j] = new DobbelVak(nummer);
                }
            }
        }
    }

    /**
     * Haalt het dobbel gebied op.
     * @return Het dobbel gebied als een matrix van DobbelVak-objecten.
     */
    public DobbelVak[][][] getDobbelGebied() {
        return this.dobbelGebied;
    }
    /**
     * Leegt het dobbelgebied door alle vakken in het gebied de kleur null te geven.
     */
    public void leegDobbelgebied() {
        for (int table = 0; table < 6; table++) { 
            for (int rij = 0; rij < 8; rij++) {
                for (int kolom = 0; kolom < 3; kolom++) {
                    dobbelGebied[table][rij][kolom].setKleur(null); 
                }
            }
        }
    }

    /**
     * Plaatst een zetsteen in het dobbelgebied op de opgegeven locatie. Als de positie al bezet is, 
     * wordt geprobeerd een zetsteen in de volgende lege positie te plaatsen. Als er geen lege posities 
     * meer zijn in de rij, wordt de zetsteen in de volgende rij geplaatst.
     * 
     * @param table Het indexnummer van de tafel (0-5).
     * @param rij Het indexnummer van de rij (0-7).
     * @param kolom Het indexnummer van de kolom (0-2).
     * @param kleur De kleur van de zetsteen die geplaatst moet worden.
     */
    public void plaatsZetsteen(int table, int rij, int kolom, Kleur kleur) {
        if (table < 0 || table >= 6 || rij < 0 || rij >= 8 || kolom < 0 || kolom >= 3) {
            return; 
        }
        if (dobbelGebied[table][rij][kolom].getKleur() != null) {
            for (int j = kolom + 1; j < 3; j++) {
                if (dobbelGebied[table][rij][j].getKleur() == null) {
                    dobbelGebied[table][rij][j].setKleur(kleur);
                    return; 
                }
            }
            if (rij + 1 < 8) {
                plaatsZetsteen(table, rij - 1, 0, kleur); 
            }
            return; 
        } 
        dobbelGebied[table][rij][kolom].setKleur(kleur);
    }

    /**
     * Geeft de twee hoogste zetstenen (de kleuren) in een opgegeven kolom terug.
     * 
     * @param index Het indexnummer van de kolom (0-5).
     * @return Een lijst van de kleuren van de twee hoogste zetstenen.
     * @throws IllegalArgumentException Als de opgegeven index buiten het bereik ligt.
     */
    public List<Kleur> geefHoogsteZetstenen(int index) {
        List<Kleur> hoogsteKleuren = new ArrayList<>();

        if (index < 0 || index >= 6) {
            throw new IllegalArgumentException("Ongeldige kolomindex.");
        }
        for (int rij = 7; rij >= 0; rij--) { 
            for (int kolom = 0; kolom < 3; kolom++) {
                Kleur kleur = dobbelGebied[index][rij][kolom].getKleur();

                if (kleur != null) {
                    hoogsteKleuren.add(kleur);
                    if (hoogsteKleuren.size() == 2) {
                        return hoogsteKleuren; 
                    }
                }
            }
        }

        return hoogsteKleuren;
    }

    /**
     * Bepaalt de beloningen op basis van de hoogste zetstenen in elke kolom en kent scores toe aan de spelers.
     * Als de tweede zetsteen de bonus 'STRT' heeft, wordt een startfiche aan de speler gegeven.
     * 
     * @see #geefStartFiche(Speler)
     */
    public void bepaalBeloningen() {
        for (int kolom = 0; kolom < 6; kolom++) {
            List<Kleur> hoogsteKleuren = geefHoogsteZetstenen(kolom);
            if (!hoogsteKleuren.isEmpty()) {
                Kleur hoogsteSteen = hoogsteKleuren.get(0);
                int gebouwpunten1 = 2;
                verwerkgebouwPunten(hoogsteSteen, gebouwpunten1, kolom);
            }
            if (hoogsteKleuren.size() > 1) {
                Kleur tweedeSteen = hoogsteKleuren.get(1); 
                int gebouwpunten2 = 1;
                verwerkgebouwPunten(tweedeSteen, gebouwpunten2, kolom);
                String bonus = getBonusFiche(kolom);
                Speler speler = vindSpelerMetKleur(tweedeSteen);
                if(bonus == "STRT") {
                    geefStartFiche(speler);
                } else {
                    int score;
                    try {
                        score = Integer.parseInt(bonus.trim());
                    } catch (NumberFormatException e) {
                        score = new Random().nextInt(3) + 1;
                    }                    
                    this.setSpelerScore(speler, score);
                }
            }
        }
    }

    /**
     * Verwerkt de gebouwpunten voor de opgegeven kleur en kolom.
     * 
     * @param steen De kleur van de zetsteen.
     * @param gebouwpunten Het aantal gebouwpunten dat wordt toegekend.
     * @param kolom Het indexnummer van de kolom waar de zetsteen is geplaatst.
     */
    private void verwerkgebouwPunten(Kleur steen, int gebouwpunten, int kolom) {
        this.voegKleurToeAanTegel(kolom, gebouwpunten, steen);
    }

    /**
     * Verkrijgt de bonusfiche voor een bepaalde kolom.
     * 
     * @param kolom Het indexnummer van de kolom.
     * @return De bonusfiche voor de kolom.
     */
    private String getBonusFiche(int kolom) {
        return this.fichesGebied[kolom];
    }

    /**
     * Stelt de lijst van gebouwen in, gebruikmakend van de waarden uit de enum Gebouw.
     */
    public void setGebouwen() {
        this.gebouwen = Arrays.asList(Gebouw.values());
    }

    /**
     * Verkrijgt de fiches in het gebied.
     * 
     * @return Een array van de fiches in het gebied.
     */
    public String[] getFichesGebied() {
        return fichesGebied;
    }

    /**
     * Zoekt de speler die de opgegeven kleur bezit.
     * 
     * @param kleur De kleur die wordt gezocht.
     * @return De speler die de opgegeven kleur bezit, of null als er geen speler wordt gevonden.
     */
    private Speler vindSpelerMetKleur(Kleur kleur) {
        for (Map.Entry<Speler, Kleur> entry : spelersKleuren.entrySet()) {
            if (entry.getValue().equals(kleur)) {
                return entry.getKey(); 
            }
        }
        return null; 
    }

    /**
     * Geeft een stringrepresentatie van het bord, inclusief gebouwpunten, fiches en het dobbelgebied.
     * 
     * @return Een stringrepresentatie van het bord.
     */
    public String toString() {
        StringBuilder bord = new StringBuilder();

        bord.append("\n--- Gebouwenpunten ---\n");
        for (int row = 11; row >= 0; row--) {
            for (int table = 0; table < 6; table++) {
                bord.append(String.format("%-25s", gebouwpuntenGebied[table][row].toString()));
            }
            bord.append("\n\n"); 
        }

        bord.append("\n--- Fiches ---\n");
        for (int i = 0; i < fichesGebied.length; i++) {
            bord.append(String.format("%-25s", fichesGebied[i].isEmpty() ? "[ ]" : fichesGebied[i]));
        }
        bord.append("\n\n");

        bord.append("\n--- DobbelGebied ---\n");
        for (int row = 7; row >= 0; row--) { 
            for (int table = 0; table < 6; table++) {
                bord.append(String.format("%-25s", verkortArray(dobbelGebied[table][row])));
            }
            bord.append("\n\n"); 
        }

        bord.append("\n--- Gebouwen ---\n");
        for (Gebouw gebouw : gebouwen) {
            bord.append(String.format("%-25s", gebouw.name()));
        }

        return bord.toString();
    }

    /**
     * Resetteert het spelbord door het dobbelgebied leeg te maken.
     */
    public void resetSpelBord() {
        this.leegDobbelgebied();
    }

    /**
     * Initialiseert de punten per ronde map, die de puntenscores voor elke kolom en ronde bijhoudt.
     */
    private void initPuntenMapPerRonde() {
        this.puntenMapPerRonde = new HashMap<>();

        Map<Integer, List<Integer>> kolom0 = new HashMap<>();
        kolom0.put(1, new ArrayList<>(Arrays.asList(1)));  
        kolom0.put(2, new ArrayList<>(Arrays.asList(8, 16))); 
        kolom0.put(3, new ArrayList<>(Arrays.asList(16, 8, 1))); 
        puntenMapPerRonde.put(0, kolom0); 

        Map<Integer, List<Integer>> kolom1 = new HashMap<>();
        kolom1.put(1, new ArrayList<>(Arrays.asList(2)));  
        kolom1.put(2, new ArrayList<>(Arrays.asList(9, 2)));
        kolom1.put(3, new ArrayList<>(Arrays.asList(17, 9, 2))); 
        puntenMapPerRonde.put(1, kolom1);  

        Map<Integer, List<Integer>> kolom2 = new HashMap<>();
        kolom2.put(1, new ArrayList<>(Arrays.asList(3)));  
        kolom2.put(2, new ArrayList<>(Arrays.asList(10, 3)));  
        kolom2.put(3, new ArrayList<>(Arrays.asList(18, 10, 3)));  
        puntenMapPerRonde.put(2, kolom2);

        Map<Integer, List<Integer>> kolom3 = new HashMap<>();
        kolom3.put(1, new ArrayList<>(Arrays.asList(4)));  
        kolom3.put(2, new ArrayList<>(Arrays.asList(11, 4)));  
        kolom3.put(3, new ArrayList<>(Arrays.asList(19, 11, 4)));  
        puntenMapPerRonde.put(3, kolom3);

        Map<Integer, List<Integer>> kolom4 = new HashMap<>();
        kolom4.put(1, new ArrayList<>(Arrays.asList(5)));  
        kolom4.put(2, new ArrayList<>(Arrays.asList(12, 5)));  
        kolom4.put(3, new ArrayList<>(Arrays.asList(20, 12, 5)));  
        puntenMapPerRonde.put(4, kolom4);

        Map<Integer, List<Integer>> kolom5 = new HashMap<>();
        kolom5.put(1, new ArrayList<>(Arrays.asList(6)));  
        kolom5.put(2, new ArrayList<>(Arrays.asList(13, 6)));  
        kolom5.put(3, new ArrayList<>(Arrays.asList(21,13,6)));  
        puntenMapPerRonde.put(5, kolom5);
    }

    /**
     * Geeft de bovenste kleuren van een bepaalde kolom terug (maximaal de eerste drie verschillende kleuren).
     * 
     * @param kolom Het indexnummer van de kolom.
     * @return Een lijst van de eerste drie verschillende kleuren in de opgegeven kolom.
     * @throws IllegalArgumentException Als de opgegeven index buiten het bereik ligt.
     */
    public List<Kleur> geefBovensteKleuren(int kolom) {
        List<Kleur> bovensteKleuren = new ArrayList<>();

        if (kolom < 0 || kolom >= gebouwpuntenGebied.length) {
            throw new IllegalArgumentException("Ongeldige kolomindex.");
        }
        for (int rij = 11; rij >= 0; rij--) {
            List<Kleur> kleurenOpTegel = gebouwpuntenGebied[kolom][rij].getKleuren();

            for (Kleur kleur : kleurenOpTegel) {
                if (!bovensteKleuren.contains(kleur)) { 
                    bovensteKleuren.add(kleur);
                    if (bovensteKleuren.size() == 3) {
                        return bovensteKleuren;
                    }
                }
            }
        }

        return bovensteKleuren;
    }

    /**
     * Evalueert de gebouwpunten voor een bepaalde ronde en kent de punten toe aan de spelers.
     * 
     * @param ronde Het nummer van de ronde (0-5).
     */
    public void evalueerGebouwPuntengebied(int ronde) {
        for(int kolom = 0; kolom<6; kolom++) {
            List<Kleur> bovensteKleuren = this.geefBovensteKleuren(kolom);

            List<Integer> punten = puntenMapPerRonde.get(kolom).get(ronde);
            int aantalTeBeoordelenKleuren = Math.min(bovensteKleuren.size(), punten.size());

            int maxKleurenVoorRonde = Math.min(aantalTeBeoordelenKleuren, ronde);

            for (int i = 0; i < maxKleurenVoorRonde; i++) {
                Kleur kleur = bovensteKleuren.get(i);   
                Integer score = punten.get(i);         

                Speler speler = this.vindSpelerMetKleur(kleur);
                setSpelerScore(speler, score);
            }

        }

    }

    /**
     * Verkort de weergave van een array van DobbelVak objecten tot een stringrepresentatie.
     * 
     * @param vakken De array van DobbelVak objecten.
     * @return Een verkorte stringrepresentatie van de array van DobbelVak objecten.
     */

	private String verkortArray(DobbelVak[] vakken) {
	    StringBuilder sb = new StringBuilder("[");
	    for (int i = 0; i < vakken.length; i++) {
	        Kleur kleur = vakken[i].getKleur();
	        if (kleur != null) {
	            sb.append(kleur.name(), 0, Math.min(2, kleur.name().length()));
	        } else {
	            sb.append("  "); 
	        }
	        if (i < vakken.length - 1) {
	            sb.append(", ");
	        }
	    }
	    sb.append("]");
	    return sb.toString();
	}

}