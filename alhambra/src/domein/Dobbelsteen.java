package domein;

import utils.Gebouw;

/**
 * De klasse {@code Dobbelsteen} stelt een dobbelsteen voor die gebruikt wordt in het spel.
 * Elke worp van de dobbelsteen levert een willekeurig {@link Gebouw} op.
 */
public class Dobbelsteen {
    
    /**
     * De huidige waarde van de dobbelsteen, voorgesteld als een {@link Gebouw}.
     */
    private Gebouw waarde;

    /**
     * Maakt een nieuwe dobbelsteen aan en voert automatisch een eerste worp uit.
     * De waarde na aanmaak is een willekeurig gekozen {@link Gebouw}.
     */
    public Dobbelsteen() {
        this.rol(); 
    }

    /**
     * Werpt de dobbelsteen, waarbij een nieuwe willekeurige waarde wordt gegenereerd.
     * De waarde wordt bepaald door {@link Gebouw#willekeurig()}.
     */
    public void rol() {
        this.waarde = Gebouw.willekeurig();
    }

    /**
     * Geeft de huidige waarde van de dobbelsteen terug.
     *
     * @return het {@link Gebouw} dat momenteel op de dobbelsteen staat
     */
    public Gebouw getWaarde() {
        return waarde;
    }

    /**
     * Geeft een stringrepresentatie van de dobbelsteen, inclusief een emoji.
     *
     * @return een string in de vorm "🎲 [waarde]"
     */
    @Override
    public String toString() {
        return "🎲 " + waarde;
    }
}
