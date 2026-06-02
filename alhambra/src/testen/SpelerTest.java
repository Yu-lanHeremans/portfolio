package testen;

import domein.Speler;
import exceptions.OngeldigGeboortejaarException;
import exceptions.OngeldigeGebruikersnaamException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SpelerTest {
    
    private Speler speler;

    @Test
    void maakSpeler_alleGegevensCorrect_maaktObject() {
        speler = new Speler("avatar", 2003, 4, 25);
        Assertions.assertEquals("avatar", speler.getGebruikersnaam());
        Assertions.assertEquals(2003, speler.getGeboortejaar());
        Assertions.assertEquals(4, speler.getAantalGewonnen());
        Assertions.assertEquals(25, speler.getAantalGespeeld());
    }

    @Test
    void maakSpeler_correcteGebruikersnaamGeboortejaar_maaktObject() {
        speler = new Speler("avatar", 2003);
        Assertions.assertEquals("avatar", speler.getGebruikersnaam());
        Assertions.assertEquals(2003, speler.getGeboortejaar());
        Assertions.assertEquals(0, speler.getAantalGewonnen());
        Assertions.assertEquals(0, speler.getAantalGespeeld());
    }

    @Test
    void maakSpeler_ongeldigeGebruikersnaam_werptException() {
        Assertions.assertThrows(OngeldigeGebruikersnaamException.class, () -> new Speler("ab", 2000));
        Assertions.assertThrows(OngeldigeGebruikersnaamException.class, () -> new Speler("      ", 2000));
        Assertions.assertThrows(OngeldigeGebruikersnaamException.class, () -> new Speler(null, 2000));
    }

    @Test
    void maakSpeler_ongeldigGeboortejaar_werptException() {
        int huidigJaar = java.time.Year.now().getValue();
        Assertions.assertThrows(OngeldigGeboortejaarException.class, () -> new Speler("avatar", huidigJaar + 1));
        Assertions.assertThrows(OngeldigGeboortejaarException.class, () -> new Speler("avatar", huidigJaar - 101));
        Assertions.assertThrows(OngeldigGeboortejaarException.class, () -> new Speler("avatar", huidigJaar));
    }

    @Test
    void setAantalGewonnen_correcteWaarde_steltIn() {
        speler = new Speler("avatar", 2003);
        speler.setAantalGewonnen(5);
        Assertions.assertEquals(5, speler.getAantalGewonnen());
    }

    @Test
    void setAantalGespeeld_correcteWaarde_steltIn() {
        speler = new Speler("avatar", 2003);
        speler.setAantalGespeeld(10);
        Assertions.assertEquals(10, speler.getAantalGespeeld());
    }
}
