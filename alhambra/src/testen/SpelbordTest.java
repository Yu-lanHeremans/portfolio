package testen;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import domein.Spelbord;
import domein.Speler;

import org.junit.jupiter.api.BeforeEach;
import utils.Kleur;

import java.util.*;

class SpelbordTest {

	private Spelbord spelbord;
    private Speler speler1;
    private Speler speler2;

    @BeforeEach
    public void setUp() {
    	speler1 = new Speler("Avatar1", 2000);
        speler2 = new Speler("Avatar2", 1998);

        List<Speler> spelers = Arrays.asList(speler1, speler2);
        List<Integer> bonusFiches = new ArrayList<>(Arrays.asList(3, 2, 1, 5, 4, 6));
        Map<Speler, Kleur> spelersKleuren = new HashMap<>();
        spelersKleuren.put(speler1, Kleur.ROOD);
        spelersKleuren.put(speler2, Kleur.BLAUW);

        spelbord = new Spelbord(bonusFiches, spelers, spelersKleuren);
    }

    @Test
    public void InitialisatieBord() {
        assertEquals(2, spelbord.getSpelerscores().size());
        assertEquals(0, spelbord.getRonde());
        assertNotNull(spelbord.getGebouwpuntenGebied());
        assertNotNull(spelbord.getDobbelGebied());
        assertEquals(6, spelbord.getFichesGebied().length);
    }

    @Test
    public void Fiches() {
        spelbord.volgendeRonde();
        String[] fiches = spelbord.getFichesGebied();
        long gevuld = Arrays.stream(fiches).filter(s -> !s.isEmpty()).count();
        assertTrue(gevuld > 0);
    }

    @Test
    public void Zetstenen() {
        spelbord.plaatsZetsteen(0, 0, 0, Kleur.ROOD);
        List<Kleur> hoogste = spelbord.geefHoogsteZetstenen(0);
        assertFalse(hoogste.isEmpty());
        assertEquals(Kleur.ROOD, hoogste.get(0));
    }

    @Test
    public void BepaalBeloningenGeeftPuntenEnStartfiche() {
        spelbord.volgendeRonde(); // nodig om fiches te plaatsen
        spelbord.plaatsZetsteen(0, 7, 0, Kleur.ROOD);  // hoogste
        spelbord.plaatsZetsteen(0, 6, 0, Kleur.BLAUW); // tweede hoogste

        spelbord.bepaalBeloningen();

        Map<Speler, Integer> scores = spelbord.getSpelerscores();
        assertTrue(scores.get(speler1) >= 0);
        assertTrue(scores.get(speler2) >= 0);

        Map<Speler, Boolean> startFiche = spelbord.getStartFiche();
        assertTrue(startFiche.containsKey(speler1));
    }

    @Test
    public void testResetSpelBordMaaktDobbelGebiedLeeg() {
        spelbord.plaatsZetsteen(0, 0, 0, Kleur.BLAUW);
        spelbord.resetSpelBord();
        assertNull(spelbord.getDobbelGebied()[0][0][0].getKleur());
    }
}
