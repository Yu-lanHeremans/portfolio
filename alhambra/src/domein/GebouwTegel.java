package domein;

import java.util.ArrayList;
import java.util.List;

import utils.Kleur;

/**
 * De klasse {@code GebouwTegel} stelt een tegel voor die één of meerdere kleuren kan bevatten.
 * Deze kleuren kunnen verwijzen naar gebouwen die op de tegel aanwezig zijn.
 */
public class GebouwTegel {

    /**
     * De lijst van kleuren (gebouwen) die aan deze tegel zijn toegevoegd.
     */
    private List<Kleur> kleuren;

    /**
     * Maakt een nieuwe {@code GebouwTegel} aan met een lege lijst van kleuren.
     */
    public GebouwTegel() {
        this.kleuren = new ArrayList<>();
    }

    /**
     * Geeft de lijst van kleuren terug die momenteel op de tegel aanwezig zijn.
     *
     * @return een lijst van kleuren
     */
    public List<Kleur> getKleuren() {
        return kleuren;
    }

    /**
     * Voegt een kleur toe aan de tegel, indien deze nog niet aanwezig is.
     *
     * @param kleur de toe te voegen kleur
     */
    public void voegKleurToe(Kleur kleur) {
        if (!kleuren.contains(kleur)) { 
            this.kleuren.add(kleur);
        }
    }

    /**
     * Controleert of een bepaalde kleur aanwezig is op deze tegel.
     *
     * @param kleur de kleur om te controleren
     * @return {@code true} als de kleur aanwezig is, anders {@code false}
     */
    public boolean heeftKleur(Kleur kleur) {
        return kleuren.contains(kleur);
    }

    /**
     * Verwijdert een specifieke kleur van de tegel, indien aanwezig.
     *
     * @param kleur de kleur die verwijderd moet worden
     */
    public void verwijderKleur(Kleur kleur) {
        kleuren.remove(kleur);
    }

    /**
     * Geeft een stringrepresentatie van de tegel.
     * Als er geen kleuren zijn, wordt een lege tegel weergegeven.
     *
     * @return een stringrepresentatie van de tegel
     */
    @Override
    public String toString() {
        return kleuren.isEmpty() ? "[ ]" : kleuren.toString();
    }
}
