package domein;

import java.util.List;
import java.util.Map;
import utils.Gebouw;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * De {@code Ronde} klasse vertegenwoordigt één speelfase in het spel.
 * Beheert de volgorde van spelers, hun dobbelsteenworpen, dobbelstenen zelf,
 * en houdt scores bij.
 */
public class Ronde {
    private int nummer;
    private Speler huidigeSpeler;
    private List<Speler> spelers;
    private List<Dobbelsteen> dobbelstenen;
    private Map<Speler, Integer> spelerWorpen;
    private Map<Speler, Integer> scores;

    /**
     * Maakt een nieuwe ronde aan.
     *
     * @param nummer       Het nummer van de ronde.
     * @param startSpeler  De speler die de ronde start.
     * @param spelers      De lijst van spelers die deelnemen.
     */
    public Ronde(int nummer, Speler startSpeler, List<Speler> spelers) {
        this.nummer = nummer;
        this.dobbelstenen = new ArrayList<>();
        this.spelerWorpen = new HashMap<>();
        setSpelers(spelers);
        initScores();
        setHuidigeSpeler(startSpeler);
        setSpelerWorpen();
        initialiseerDobbelstenen();
    }

    /**
     * Initialiseert de beginscores van alle spelers op 0.
     */
    private void initScores() {
        this.scores = new HashMap<>();
        for (Speler speler : spelers) {
            scores.put(speler, 0);
        }
    }

    /**
     * Zet het aantal worpen van elke speler op 0.
     */
    private void setSpelerWorpen() {
        for (Speler speler : spelers) {
            spelerWorpen.put(speler, 0);
        }
    }

    /**
     * Geeft de lijst van spelers terug.
     *
     * @return lijst van spelers.
     */
    public List<Speler> getSpelers() {
        return spelers;
    }

    private void setSpelers(List<Speler> spelers) {
        this.spelers = spelers;
    }

    /**
     * Geeft het nummer van de ronde.
     *
     * @return het ronde-nummer.
     */
    public int getNummer() {
        return nummer;
    }

    /**
     * Geeft de huidige speler.
     *
     * @return de huidige speler.
     */
    public Speler getHuidigeSpeler() {
        return huidigeSpeler;
    }

    /**
     * Geeft de lijst van dobbelstenen.
     *
     * @return lijst van dobbelstenen.
     */
    public List<Dobbelsteen> getDobbelstenen() {
        return dobbelstenen;
    }

    /**
     * Stelt de huidige speler in.
     *
     * @param speler de speler die aan de beurt is.
     */
    public void setHuidigeSpeler(Speler speler) {
        this.huidigeSpeler = speler;
    }

    /**
     * Initialiseert 8 dobbelstenen voor gebruik in de ronde.
     */
    private void initialiseerDobbelstenen() {
        for (int i = 0; i < 8; i++) {
            dobbelstenen.add(new Dobbelsteen());
        }
    }

    /**
     * Laat de huidige speler alle dobbelstenen werpen.
     * Werpen is beperkt tot 3 pogingen per ronde.
     */
    public void gooiDobbelstenen() {
        int worpen = spelerWorpen.get(huidigeSpeler);
        for (Dobbelsteen d : dobbelstenen) {
            d.rol();
        }
        spelerWorpen.put(huidigeSpeler, worpen + 1);
    }

    /**
     * Geeft het aantal worpen per speler terug.
     *
     * @return een map van spelers met hun aantal worpen.
     */
    public Map<Speler, Integer> getSpelerWorpen() {
        return spelerWorpen;
    }

    /**
     * Stelt de map met worpen per speler in.
     *
     * @param spelerWorpen de nieuwe mapping van spelers naar worpen.
     */
    public void setSpelerWorpen(Map<Speler, Integer> spelerWorpen) {
        this.spelerWorpen = spelerWorpen;
    }

    /**
     * Laat de huidige speler bepaalde dobbelstenen opnieuw werpen.
     *
     * @param dobbelstenenKeuze indices van de dobbelstenen die opnieuw geworpen moeten worden.
     * @throws IllegalStateException als de speler al 3 keer geworpen heeft.
     */
    public void hersmijtDobbelstenen(List<Integer> dobbelstenenKeuze) {
        if (spelerWorpen.get(huidigeSpeler) < 3) {
            int worpen = spelerWorpen.get(huidigeSpeler);
            for (int i : dobbelstenenKeuze) {
                dobbelstenen.get(i).rol();
            }
            spelerWorpen.put(huidigeSpeler, worpen + 1);
        } else {
            throw new IllegalStateException("Je kunt niet meer dan 3 keer dobbelstenen werpen per ronde.");
        }
    }

    /**
     * Geeft de waarden van de dobbelstenen als gebouwen.
     *
     * @return lijst van gebouwwaarden van de dobbelstenen.
     */
    public List<Gebouw> getDobbelstenenWaarden() {
        List<Gebouw> waarden = new ArrayList<>();
        for (Dobbelsteen d : dobbelstenen) {
            waarden.add(d.getWaarde());
        }
        return waarden;
    }

    /**
     * Geeft een stringrepresentatie van de huidige worp van de dobbelstenen.
     *
     * @return string van de worpwaarden.
     */
    public String toonWorp() {
        StringBuilder sb = new StringBuilder("");
        for (Dobbelsteen d : dobbelstenen) {
            sb.append(d.getWaarde()).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Geeft de huidige scores van alle spelers terug.
     *
     * @return map van spelers en hun scores.
     */
    public Map<Speler, Integer> getScores() {
        return scores;
    }

    /**
     * Stelt de score van een bepaalde speler in.
     *
     * @param speler de speler.
     * @param score  de toe te kennen score.
     */
    public void setScore(Speler speler, int score) {
        this.scores.put(speler, score);
    }

    /**
     * Zet de huidige speler naar de volgende in de spelerslijst.
     * Herstart bij begin als einde bereikt is.
     *
     * @throws IllegalStateException als de huidige speler niet gevonden wordt.
     */
    public void volgendeSpeler() {
        int huidigeIndex = spelers.indexOf(huidigeSpeler);

        if (huidigeIndex == -1) {
            throw new IllegalStateException("Huidige speler bestaat niet in de lijst van spelers.");
        }

        int volgendeIndex = (huidigeIndex + 1) % spelers.size();
        setHuidigeSpeler(spelers.get(volgendeIndex));
    }

    /**
     * Geeft het aantal spelers in de ronde.
     *
     * @return aantal spelers.
     */
    public int getAantalSpelers() {
        return spelers.size();
    }

    /**
     * Reset het aantal worpen voor alle spelers naar 0.
     */
    public void resetSpelerWorpen() {
        for (Speler speler : spelers) {
            spelerWorpen.put(speler, 0);
        }
    }
}
