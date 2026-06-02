package domein;

import java.util.List;

import exceptions.GebruikersnaamInGebruikException;
import persistentie.SpelerMapper;

/**
 * De SpelerRepository beheert de opslag en het ophalen van {@link Speler} objecten.
 * Deze klasse fungeert als tussenlaag tussen de applicatie en de persistentiemodule
 * (de {@link SpelerMapper}) en biedt methoden om spelers toe te voegen, bij te werken
 * en op te vragen.
 */
public class SpelerRepository {

    private final SpelerMapper mapper;

    /**
     * Constructor voor het aanmaken van een nieuwe SpelerRepository.
     * Dit initialiseert de interne {@link SpelerMapper}.
     */
    public SpelerRepository() {
        mapper = new SpelerMapper();
    }

    /**
     * Voegt een nieuwe speler toe aan de opslag. 
     * Als de gebruikersnaam al bestaat, wordt een {@link GebruikersnaamInGebruikException} gegooid.
     *
     * @param speler de {@link Speler} die toegevoegd moet worden.
     * @throws GebruikersnaamInGebruikException als de gebruikersnaam al in gebruik is.
     */
    public void voegToe(Speler speler) {
        if (bestaatSpeler(speler.getGebruikersnaam())) 
            throw new GebruikersnaamInGebruikException();
        
        mapper.voegToe(speler);
    }

    /**
     * Controleert of er al een speler bestaat met de gegeven gebruikersnaam.
     *
     * @param gebruikersnaam de gebruikersnaam van de speler die gecontroleerd moet worden.
     * @return true als de speler bestaat, anders false.
     */
    private boolean bestaatSpeler(String gebruikersnaam){
        return mapper.geefSpeler(gebruikersnaam) != null;
    }  

    /**
     * Haalt een lijst op van alle spelers.
     *
     * @return een lijst van alle {@link Speler} objecten.
     */
    public List<Speler> geefAlleSpelers() {
        return mapper.geefAlleSpelers();
    }

    /**
     * Werk de gegevens van een bestaande speler bij.
     *
     * @param speler de {@link Speler} met bijgewerkte gegevens.
     */
    public void update(Speler speler) {
        mapper.update(speler);
    }
}
