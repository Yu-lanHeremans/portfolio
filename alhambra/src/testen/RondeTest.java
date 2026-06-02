package testen;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import domein.Ronde;
import domein.Speler;

class RondeTest {

	private Ronde ronde;
	private Speler speler1;
    private Speler speler2;
    private List<Speler> spelers;
	
    @BeforeEach
    public void setUp() {
        speler1 = new Speler("Avatar1", 2000);
        speler2 = new Speler("Avatar2", 1998);
        spelers = Arrays.asList(speler1, speler2);
        ronde = new Ronde(1, speler1, spelers);
    }

    @Test
    public void EersteRondeTest() {
    	//juiste ronde
        assertEquals(1, ronde.getNummer());
        //juiste startspelr
        assertEquals(speler1, ronde.getHuidigeSpeler());
        //juiste aantal spelers
        assertEquals(2, ronde.getAantalSpelers());
        //juiste dobbelsteen
        assertEquals(8, ronde.getDobbelstenen().size());

        //checkt of elke speel start met 0 worpen en punten
        for (Speler speler : spelers) {
            assertEquals(0, (int) ronde.getSpelerWorpen().get(speler));
            assertEquals(0, (int) ronde.getScores().get(speler));
        }
    }

    @Test
    public void DobbelteenVerhoogtWorpen() {
        ronde.gooiDobbelstenen();
        assertEquals(1, ronde.getSpelerWorpen().get(speler1));
    }

    @Test
    public void HersmijtDobbelstenenVerhoogtWorpen() {
        ronde.gooiDobbelstenen();
        ronde.hersmijtDobbelstenen(Arrays.asList(0, 1, 2));
        assertEquals(2, ronde.getSpelerWorpen().get(speler1));
    }

    @Test
    public void HersmijtDobbelstenenMaxWorpen() {
        ronde.gooiDobbelstenen(); // worp 1
        ronde.hersmijtDobbelstenen(List.of(0)); // worp 2
        ronde.hersmijtDobbelstenen(List.of(1)); // worp 3

        assertThrows(IllegalStateException.class, () -> {
            ronde.hersmijtDobbelstenen(List.of(2)); // worp 4 zou moeten falen
        });
    }

    @Test
    public void VolgendeSpelerTest() {
        assertEquals(speler1, ronde.getHuidigeSpeler());
        ronde.volgendeSpeler();
        assertEquals(speler2, ronde.getHuidigeSpeler());
        ronde.volgendeSpeler();
        assertEquals(speler1, ronde.getHuidigeSpeler());
    }

    @Test
    public void ResetSpelerWorpen() {
        ronde.gooiDobbelstenen();
        assertEquals(1, ronde.getSpelerWorpen().get(speler1));

        ronde.resetSpelerWorpen();
        for (Speler speler : spelers) {
            assertEquals(0, (int) ronde.getSpelerWorpen().get(speler));
        }
    }
}

