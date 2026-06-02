package domein;

import java.util.*;
import dto.SpelerDTO;
import utils.Gebouw;
import utils.Kleur;

/**
 * De klasse {@code Spel} vertegenwoordigt het domeinmodel van een bordspel.
 * Deze klasse beheert het spelverloop, de spelers, hun kleuren, zetstenen,
 * het spelbord, rondes en bepaalt de winnaar aan het einde van het spel.
 */
public class Spel {

    /**
     * Lijst van spelers die deelnemen aan het spel.
     */
    private List<Speler> spelers;

    /**
     * Map die elke speler koppelt aan een unieke kleur.
     */
    private Map<Speler, Kleur> spelersKleuren;

    /**
     * Map die elke speler koppelt aan hun aantal beschikbare zetstenen.
     */
    private Map<Speler, Integer> spelersZetstenen;

    /**
     * De speler die het spel mag starten.
     */
    private Speler startSpeler;

    /**
     * Lijst van bonusfiches (getallen van 1-3) die gebruikt worden op het spelbord.
     */
    private List<Integer> bonusFiches;

    /**
     * De huidige ronde van het spel.
     */
    private int huidigeRonde;

    /**
     * Lijst van alle rondes die tot nu toe gespeeld zijn.
     */
    private List<Ronde> rondes;

    /**
     * Het spelbord dat tijdens het spel gebruikt wordt.
     */
    private Spelbord spelbord;

    /**
     * De winnaar of winnaars van het spel.
     */
    private List<Speler> winnaars = new ArrayList<>();

    /**
     * Maakt een nieuw spel aan met de opgegeven spelers en hun kleuren.
     *
     * @param spelers         De lijst van spelers.
     * @param spelersKleuren  Een map met spelers en hun gekozen kleuren.
     */
    public Spel(List<Speler> spelers, Map<Speler, Kleur> spelersKleuren) {
        setSpelers(spelers);
        setSpelersKleuren(spelersKleuren);
        setStartSpeler(spelers.get(new Random().nextInt(spelers.size())));
        setSpelersZetstenen();
        genereerBonusFiches();
        huidigeRonde = 0;
        rondes = new ArrayList<>();
        spelbord = new Spelbord(bonusFiches, spelers, spelersKleuren);
    }

    /**
     * Retourneert informatie over het spel en de spelers.
     *
     * @return String met informatie over de spelers en hun attributen.
     */
    public String toonSpelInfo() {
        StringBuilder info = new StringBuilder("Startspeler: " + startSpeler.getGebruikersnaam() + "\n");
        for (Speler speler : spelers) {
            Kleur kleur = spelersKleuren.get(speler);
            Integer aantalZetstenen = spelersZetstenen.get(speler);
            if (aantalZetstenen == null) {
                aantalZetstenen = 0;
            }
            info.append(new SpelerDTO(speler.getGebruikersnaam(), speler.getGeboortejaar(), kleur, aantalZetstenen))
                .append("\n");
        }
        return info.toString();
    }

    /**
     * Geeft het aantal zetstenen per speler op basis van het aantal spelers.
     *
     * @return Het aantal zetstenen.
     */
    public int getAantalZetstenen() {
        int aantalSpelers = spelers.size();
        if (aantalSpelers == 3) return 5;
        if (aantalSpelers == 4) return 4;
        return 3;
    }

    public List<Speler> getSpelers() {
        return new ArrayList<>(spelers);
    }

    public Map<Speler, Kleur> getSpelersKleuren() {
        return new HashMap<>(spelersKleuren);
    }

    public Speler getStartSpeler() {
        return startSpeler;
    }

    private void setSpelers(List<Speler> spelers) {
        if (spelers == null || spelers.size() < 3 || spelers.size() > 6) {
            throw new IllegalArgumentException("Het aantal spelers moet tussen 3 en 6 liggen.");
        }
        this.spelers = new ArrayList<>(spelers);
    }

    private void setSpelersKleuren(Map<Speler, Kleur> spelersKleuren) {
        if (spelersKleuren == null || spelersKleuren.size() != spelers.size()) {
            throw new IllegalArgumentException("Elke speler moet een unieke kleur hebben.");
        }
        Set<Kleur> gebruikteKleuren = new HashSet<>(spelersKleuren.values());
        if (gebruikteKleuren.size() != spelersKleuren.size()) {
            throw new IllegalArgumentException("Elke speler moet een unieke kleur kiezen.");
        }
        this.spelersKleuren = new HashMap<>(spelersKleuren);
    }

    public void setStartSpeler(Speler startSpeler) {
        if (!spelers.contains(startSpeler)) {
            throw new IllegalArgumentException("Startspeler moet een van de gekozen spelers zijn.");
        }
        this.startSpeler = startSpeler;
    }

    private void setSpelersZetstenen() {
        spelersZetstenen = new HashMap<>();
        for (Speler speler : spelers) {
            spelersZetstenen.put(speler, getAantalZetstenen());
        }
    }

    private void genereerBonusFiches() {
        Random random = new Random();
        bonusFiches = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            bonusFiches.add(random.nextInt(3) + 1);
        }
        Collections.shuffle(bonusFiches);
    }

    public List<Integer> getBonusFiches() {
        return new ArrayList<>(bonusFiches);
    }

    /**
     * Start een nieuwe ronde. Werpt foutmelding als het spel is afgelopen.
     */
    public void startNieuweRonde() {
        if (huidigeRonde > 3) {
            throw new IllegalStateException("Het spel is afgelopen!");
        }
        huidigeRonde++;
        if (huidigeRonde > 1) {
            this.spelbord.resetSpelBord();
            Speler nieuweStartSpeler = bepaalNieuweStartSpeler();
            if (nieuweStartSpeler != null) {
                this.startSpeler = nieuweStartSpeler;
            }
        }
        Ronde ronde = new Ronde(this.huidigeRonde, this.startSpeler, this.spelers);
        rondes.add(ronde);
        this.spelbord.volgendeRonde();
    }

    private Map<Speler, Boolean> getStartFiche() {
        return this.spelbord.getStartFiche();
    }

    private Speler bepaalNieuweStartSpeler() {
        for (Map.Entry<Speler, Boolean> entry : this.getStartFiche().entrySet()) {
            if (entry.getValue()) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Ronde getHuidigeRonde() {
        return rondes.get(rondes.size() - 1);
    }

    public Spelbord getSpelbord() {
        return spelbord;
    }

    private void bepaalWinnaar() {
        Map<Speler, Integer> scores = this.spelbord.getSpelerscores();
        int hoogsteScore = Integer.MIN_VALUE;
        winnaars.clear();
        for (Map.Entry<Speler, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > hoogsteScore) {
                hoogsteScore = entry.getValue();
                winnaars.clear();
                winnaars.add(entry.getKey());
            } else if (entry.getValue() == hoogsteScore) {
                winnaars.add(entry.getKey());
            }
        }
    }

    private String toonScoreOverzicht() {
        StringBuilder overzicht = new StringBuilder("Scoreoverzicht:\n");
        for (Speler speler : spelers) {
            overzicht.append(String.format("%s - Gespeeld: %d, Gewonnen: %d\n",
                    speler.getGebruikersnaam(), speler.getAantalGespeeld(), speler.getAantalGewonnen()));
        }
        return overzicht.toString();
    }

    /**
     * Beëindigt het spel, bepaalt de winnaar(s) en verhoogt statistieken.
     *
     * @return Een string met het resultaat van het spel.
     */
    public String eindigSpel() {
        this.bepaalWinnaar();
        for (Speler speler : spelers) {
            speler.verhoogAantalGespeeld();
        }
        for (Speler winnaar : winnaars) {
            winnaar.verhoogAantalGewonnen();
        }
        StringBuilder resultaat = new StringBuilder();
        if (!winnaars.isEmpty()) {
            if (winnaars.size() == 1) {
                resultaat.append(String.format("\nDe winnaar van het spel is %s\n", winnaars.get(0).getGebruikersnaam()));
            } else {
                resultaat.append("\nDe winnaars van het spel zijn ");
                resultaat.append(String.join(", ", winnaars.stream().map(Speler::getGebruikersnaam).toList()));
                resultaat.append("\n");
            }
        } else {
            resultaat.append("Er is geen winnaar bepaald.\n");
        }
        resultaat.append(toonScoreOverzicht());
        return resultaat.toString();
    }

    /**
     * Plaatst een zetsteen op het bord voor een speler.
     *
     * @param huidigeSpeler  De speler die een zet doet.
     * @param gekozenGebouw  Het gekozen gebouwtype.
     */
    public void plaatsZetsteen(SpelerDTO huidigeSpeler, Gebouw gekozenGebouw) {
        Map<Speler, Integer> aantalWorpen = this.getHuidigeRonde().getSpelerWorpen();
        Speler speler = spelers.stream()
            .filter(s -> s.getGebruikersnaam().equals(huidigeSpeler.gebruikersnaam()))
            .findFirst().orElse(null);
        if (speler == null) throw new IllegalArgumentException("Speler niet gevonden.");
        Kleur kleur = spelersKleuren.get(speler);
        if (kleur == null) throw new IllegalArgumentException("Geen kleur gevonden voor deze speler.");
        int gebouwIndex = gekozenGebouw.ordinal();
        Integer worpen = aantalWorpen.get(speler);
        if (worpen == null || worpen < 1 || worpen > 3) {
            throw new IllegalArgumentException("Ongeldig aantal worpen: " + worpen);
        }
        int kolom = worpen - 1;
        int rij = (int) getHuidigeRonde().getDobbelstenen().stream()
            .filter(d -> d.getWaarde() == gekozenGebouw).count() - 1;
        spelbord.plaatsZetsteen(gebouwIndex, rij, kolom, kleur);
    }

    /**
     * Geeft de huidige score van een speler.
     *
     * @param huidigeSpeler De speler waarvan de score wordt opgevraagd.
     * @return De score van de speler.
     */
    public int geefSpelerScore(SpelerDTO huidigeSpeler) {
        Speler speler = spelers.stream()
            .filter(s -> s.getGebruikersnaam().equals(huidigeSpeler.gebruikersnaam()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Speler niet gevonden voor DTO: " + huidigeSpeler.gebruikersnaam()));
        return spelbord.getSpelerscores().get(speler);
    }

    /**
     * Bepaalt de beloningen aan het einde van een ronde.
     */
    public void bepaalBeloningen() {
        spelbord.bepaalBeloningen();
    }

    /**
     * Evalueert de punten in het gebouwgebied op het bord.
     */
    public void evalueerGebouwPuntengebied() {
        spelbord.evalueerGebouwPuntengebied(huidigeRonde);
    }
}
