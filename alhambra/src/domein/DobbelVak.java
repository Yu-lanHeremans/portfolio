package domein;
import utils.Kleur;


/**
 * De klasse {@code Dobbelsteen} stelt een dobbelsteen voor die gebruikt wordt in het spel.
 * Elke worp van de dobbelsteen levert een willekeurig {@link Gebouw} op.
 */
public class DobbelVak {
	
	/**
     * De waarde die op dit vakje staat, meestal het resultaat van een dobbelsteenworp.
     */
    private int waarde;
    
    /**
     * De kleur die aan dit vakje is toegekend, of {@code null} als er geen kleur is.
     */
    private Kleur kleur;

    /**
     * Maakt een nieuw {@code DobbelVak}-object aan met een gegeven waarde en zonder kleur.
     *
     * @param waarde de waarde van het vak
     */
    public DobbelVak(int waarde) {
        this.waarde = waarde;
        this.kleur = null;
    }

    /**
     * Geeft de waarde van dit vak terug.
     *
     * @return de waarde van het vak
     */
    public int getWaarde() {
        return waarde;
    }

    /**
     * Stelt de waarde van dit vak in.
     *
     * @param waarde de nieuwe waarde voor het vak
     */
    public void setWaarde(int waarde) {
        this.waarde = waarde;
    }

    /**
     * Geeft de kleur van dit vak terug.
     *
     * @return de kleur van het vak, of {@code null} als er geen kleur is ingesteld
     */
    public Kleur getKleur() {
        return kleur;
    }

    /**
     * Stelt de kleur van dit vak in.
     *
     * @param kleur de kleur die aan het vak wordt toegekend
     */
    public void setKleur(Kleur kleur) {
        this.kleur = kleur;
    }

    /**
     * Geeft een stringrepresentatie van het vak.
     * Als er geen kleur is, wordt enkel de waarde getoond, anders de waarde en kleur.
     *
     * @return de stringrepresentatie in de vorm van [waarde] of [waarde, kleur]
     */
    @Override
    public String toString() {
        return kleur == null ? String.format("[%d]", waarde) : String.format("[%d, %s]", waarde, kleur.name());
    }
}
