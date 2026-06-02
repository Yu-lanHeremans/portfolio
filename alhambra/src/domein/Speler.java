package domein;

import java.time.Year;

import exceptions.OngeldigGeboortejaarException;
import exceptions.OngeldigeGebruikersnaamException;

/**
 * De klasse {@code Speler} stelt een speler voor in het spel.
 * Ze houdt gegevens bij zoals gebruikersnaam, geboortejaar, aantal gewonnen en gespeelde spellen.
 * Validatie wordt uitgevoerd bij het instellen van gebruikersnaam en geboortejaar.
 * 
 * Een speler moet minstens 6 jaar oud zijn en een geldige gebruikersnaam van minimaal 6 karakters hebben.
 * 
 */
public class Speler {
    
    /** De unieke gebruikersnaam van de speler. */
    private String gebruikersnaam;
    
    /** Het geboortejaar van de speler. */
    private int geboortejaar;
    
    /** Het aantal gewonnen spellen door deze speler. */
    private int aantalGewonnen;
    
    /** Het aantal gespeelde spellen door deze speler. */
    private int aantalGespeeld;
    
    /** Het huidige jaar, gebruikt voor leeftijdsvalidatie. */
    private final int HUIDIG_JAAR = Year.now().getValue();

    /**
     * Maakt een nieuwe speler aan met nul gewonnen en nul gespeelde spellen.
     *
     * @param gebruikersnaam de gebruikersnaam van de speler (minstens 6 tekens)
     * @param geboortejaar het geboortejaar van de speler (leeftijd tussen 6 en 100 jaar)
     * @throws OngeldigeGebruikersnaamException als de gebruikersnaam ongeldig is
     * @throws OngeldigGeboortejaarException als het geboortejaar ongeldig is
     */
    public Speler(String gebruikersnaam, int geboortejaar) {
        this(gebruikersnaam, geboortejaar, 0, 0);
    }

    /**
     * Maakt een nieuwe speler aan met opgegeven statistieken.
     *
     * @param gebruikersnaam de gebruikersnaam van de speler
     * @param geboortejaar het geboortejaar van de speler
     * @param aantalGewonnen het aantal gewonnen spellen
     * @param aantalGespeeld het aantal gespeelde spellen
     */
    public Speler(String gebruikersnaam, int geboortejaar, int aantalGewonnen, int aantalGespeeld) {
        setGebruikersnaam(gebruikersnaam);
        setGeboortejaar(geboortejaar);
        setAantalGewonnen(0); // Scores worden bewust gereset bij creatie
        setAantalGespeeld(0);
    }

    /**
     * Geeft de gebruikersnaam van de speler terug.
     *
     * @return de gebruikersnaam
     */
    public String getGebruikersnaam() {
        return gebruikersnaam;
    }

    /**
     * Geeft het geboortejaar van de speler terug.
     *
     * @return het geboortejaar
     */
    public int getGeboortejaar() {
        return geboortejaar;
    }

    /**
     * Geeft het aantal gewonnen spellen van de speler terug.
     *
     * @return het aantal gewonnen spellen
     */
    public int getAantalGewonnen() {
        return aantalGewonnen;
    }

    /**
     * Stelt het aantal gewonnen spellen van de speler in.
     *
     * @param aantalGewonnen het nieuwe aantal gewonnen spellen
     */
    public void setAantalGewonnen(int aantalGewonnen) {
        this.aantalGewonnen = aantalGewonnen;
    }

    /**
     * Geeft het aantal gespeelde spellen van de speler terug.
     *
     * @return het aantal gespeelde spellen
     */
    public int getAantalGespeeld() {
        return aantalGespeeld;
    }

    /**
     * Stelt het aantal gespeelde spellen van de speler in.
     *
     * @param aantalGespeeld het nieuwe aantal gespeelde spellen
     */
    public void setAantalGespeeld(int aantalGespeeld) {
        this.aantalGespeeld = aantalGespeeld;
    }

    /**
     * Verhoogt het aantal gespeelde spellen met 1.
     */
    public void verhoogAantalGespeeld() {
        this.aantalGespeeld += 1;
    }

    /**
     * Verhoogt het aantal gewonnen spellen met 1.
     */
    public void verhoogAantalGewonnen() {
        this.aantalGewonnen += 1;
    }

    /**
     * Valideert of een gebruikersnaam geldig is.
     *
     * @param gebruikersnaam de te valideren gebruikersnaam
     * @throws OngeldigeGebruikersnaamException als de gebruikersnaam ongeldig is
     */
    private void valideerGebruikersnaam(String gebruikersnaam) {
        if (gebruikersnaam == null || gebruikersnaam.trim().isEmpty() || gebruikersnaam.length() < 6) {
            throw new OngeldigeGebruikersnaamException("Gebruikersnaam moet minstens 6 karakters bevatten en mag niet alleen uit spaties bestaan.");
        }
    }

    /**
     * Valideert of het geboortejaar geldig is.
     *
     * @param geboortejaar het te valideren geboortejaar
     * @throws OngeldigGeboortejaarException als het geboortejaar ongeldig is
     */
    private void valideerGeboortejaar(int geboortejaar) {
        int leeftijd = HUIDIG_JAAR - geboortejaar;
        if (geboortejaar > HUIDIG_JAAR || leeftijd < 6 || leeftijd > 100) {
            throw new OngeldigGeboortejaarException("Leeftijd moet tussen 6 en 100 jaar zijn.");
        }
    }

    /**
     * Stelt de gebruikersnaam in na validatie.
     *
     * @param gebruikersnaam de nieuwe gebruikersnaam
     */
    private void setGebruikersnaam(String gebruikersnaam) {
        this.valideerGebruikersnaam(gebruikersnaam);
        this.gebruikersnaam = gebruikersnaam;
    }

    /**
     * Stelt het geboortejaar in na validatie.
     *
     * @param geboortejaar het nieuwe geboortejaar
     */
    private void setGeboortejaar(int geboortejaar) {
        this.valideerGeboortejaar(geboortejaar);
        this.geboortejaar = geboortejaar;
    }
}
