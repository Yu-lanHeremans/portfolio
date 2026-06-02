package testen;

import domein.Spel;
import domein.Speler;
import dto.SpelerDTO;
import utils.Kleur;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class SpelTest {
    
    private Spel spel;
    private List<Speler> spelers;
    private Map<Speler, Kleur> spelersKleuren;

    @BeforeEach
    void setUp() {
        spelers = new ArrayList<>();
        spelersKleuren = new HashMap<>();

        Speler speler1 = new Speler("testSpeler1", 2000);
        Speler speler2 = new Speler("testSpeler2", 1998);
        Speler speler3 = new Speler("testSpeler3", 2005);

        spelers.add(speler1);
        spelers.add(speler2);
        spelers.add(speler3);

        spelersKleuren.put(speler1, Kleur.BLAUW);
        spelersKleuren.put(speler2, Kleur.GROEN);
        spelersKleuren.put(speler3, Kleur.GEEL);

        spel = new Spel(spelers, spelersKleuren);
    }

    @Test
    void maakSpel_correcteGegevens_maaktObject() {
        Assertions.assertNotNull(spel);
        Assertions.assertEquals(3, spel.getSpelers().size());
        Assertions.assertEquals(3, spel.getSpelersKleuren().size());
    }

    @Test
    void startSpeler_isEenVanDeSpelers() {
        Assertions.assertTrue(spel.getSpelers().contains(spel.getStartSpeler()));
    }

    @Test
    void getSpelersKleuren_bevatAlleSpelersMetJuisteKleur() {
        for (Speler speler : spelers) {
            Assertions.assertEquals(spelersKleuren.get(speler), spel.getSpelersKleuren().get(speler));
        }
    }
    
    @Test
    void maakSpel_teWeinigSpelers_werptException() {
        List<Speler> teWeinigSpelers = Arrays.asList(new Speler("avatar1", 2000), new Speler("avatar2", 1998));
        Map<Speler, Kleur> kleuren = new HashMap<>();
        kleuren.put(teWeinigSpelers.get(0), Kleur.BLAUW);
        kleuren.put(teWeinigSpelers.get(1), Kleur.GROEN);

        Assertions.assertThrows(IllegalArgumentException.class, () -> new Spel(teWeinigSpelers, kleuren));
    }

    @Test
    void maakSpel_duplicaatKleur_werptException() {
        List<Speler> spelers = Arrays.asList(new Speler("avatar1", 2000), new Speler("avatar2", 1998), new Speler("avatar3", 2005));
        Map<Speler, Kleur> kleuren = new HashMap<>();
        kleuren.put(spelers.get(0), Kleur.BLAUW);
        kleuren.put(spelers.get(1), Kleur.GROEN);
        kleuren.put(spelers.get(2), Kleur.BLAUW); // Dubbele kleur

        Assertions.assertThrows(IllegalArgumentException.class, () -> new Spel(spelers, kleuren));
    }

    @Test
    void getSpelerKleur_nietBestaandeSpeler_geeftNull() {
        Speler onbekendeSpeler = new Speler("avatar4", 1995);
        Assertions.assertNull(spel.getSpelersKleuren().get(onbekendeSpeler));
    }

    @Test
    void setStartSpeler_nietInLijst_werptException() {
        Speler onbekendeSpeler = new Speler("avatar5", 1992);
        Assertions.assertThrows(IllegalArgumentException.class, () -> spel.setStartSpeler(onbekendeSpeler));
    }

    @Test
    void getAantalZetstenen_correcteWaardeVoorAantalSpelers() {
        int aantalZetstenen = spel.getSpelers().size() == 3 ? 5 : spel.getSpelers().size() == 4 ? 4 : 3;
        Assertions.assertEquals(aantalZetstenen, spel.getAantalZetstenen());
    }

    @Test
    void setStartSpeler_correcteSpeler_steltIn() {
        Speler nieuweStartSpeler = spelers.get(1);
        spel.setStartSpeler(nieuweStartSpeler);
        Assertions.assertEquals(nieuweStartSpeler, spel.getStartSpeler());
    }

    @Test
    void toonSpelInfo_geeftCorrecteOutput() {
        String spelInfo = spel.toonSpelInfo();
        Assertions.assertTrue(spelInfo.contains("Startspeler:"));
        
        for (Speler speler : spelers) {
            Kleur kleur = spelersKleuren.get(speler);
            int aantalZetstenen = spel.getAantalZetstenen();
            SpelerDTO expectedDTO = new SpelerDTO(speler.getGebruikersnaam(), speler.getGeboortejaar(), kleur, aantalZetstenen);
            Assertions.assertTrue(spelInfo.contains(expectedDTO.toString()));
        }
    }
}
