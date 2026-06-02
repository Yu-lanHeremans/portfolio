package cui;

import domein.DomeinController;
import dto.SpelerDTO;
import utils.Gebouw;
import utils.Kleur;
import exceptions.OngeldigeGebruikersnaamException;
import exceptions.GebruikersnaamInGebruikException;
import exceptions.OngeldigGeboortejaarException;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AlhambraApp {

    private final DomeinController dc;
    private Scanner scanner = new Scanner(System.in);
    int huidigJaar = Year.now().getValue();
    private List<SpelerDTO> beschikbareSpelers;
    private List<SpelerDTO> gekozenSpelers;
    private Map<SpelerDTO, Kleur> spelersKleuren;
    private List<Kleur> beschikbareKleuren;

    public AlhambraApp(DomeinController dc) {
        this.dc = dc;
    }

    public void start() {
        toonEnReageerOpMenu();
    }

    private void toonEnReageerOpMenu() {
        String[] keuzes = {
                "Registreer nieuwe speler",
                "Start nieuw spel",
                "Afsluiten"
        };
        int keuze = maakKeuze(keuzes);

        while (keuze != keuzes.length) {
            switch (keuze) {
                case 1:
                    registreerSpeler();
                    break;

                case 2:
                    startNieuwSpel();
                    
                    for (int i = 0; i < 3; i++) {
                        System.out.println("Ronde " + (i + 1) + " start...");
                        speelRonde();
                    }
                    System.out.println("Het spel is afgelopen na 3 rondes!\n");
                    eindigSpel();

                    
                    break;

                case 3:
                    System.out.println("Programma wordt afgesloten.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Ongeldige keuze. Probeer opnieuw.");
            }
            keuze = maakKeuze(keuzes);
        }
    }

	private void registreerSpeler() {
        System.out.print("Voer gebruikersnaam in: ");
        String gebruikersnaam = scanner.nextLine();

        System.out.print("Voer geboortejaar in: ");
        int geboortejaar = Integer.parseInt(scanner.nextLine());

        try {
            dc.registreerSpeler(gebruikersnaam, geboortejaar);
            System.out.println("Speler succesvol geregistreerd!");
        } catch (OngeldigeGebruikersnaamException | OngeldigGeboortejaarException | GebruikersnaamInGebruikException e) {
            System.out.println("Fout: " + e.getMessage());
        }
    }

	private void geefSpelers() {
	    try {
	        beschikbareSpelers = dc.geefAlleSpelers();

	        if (beschikbareSpelers.size() < 3) {
	            System.out.println("Er moeten minstens 3 geregistreerde spelers zijn.");
	            return;
	        }

	        gekozenSpelers = new ArrayList<>();
	        spelersKleuren = new HashMap<>();
	        beschikbareKleuren = new ArrayList<>(dc.getBeschikbareKleuren());
	    } catch (Exception e) {
	        System.out.println("Fout: " + e.getMessage());
	    }
	}


	private SpelerDTO kiesSpeler() {
	    try {
	        System.out.print("Kies een speler (gebruikersnaam): ");
	        String gekozenNaam = scanner.nextLine();

	        SpelerDTO gekozenSpeler = beschikbareSpelers.stream()
	                .filter(speler -> speler.gebruikersnaam().equalsIgnoreCase(gekozenNaam))
	                .findFirst()
	                .orElse(null);

	        if (gekozenSpeler == null || gekozenSpelers.contains(gekozenSpeler)) {
	            System.out.println("Ongeldige keuze. Kies een andere speler.");
	            return null;
	        }
	        return gekozenSpeler;
	    } catch (Exception e) {
	        System.out.println("Fout: " + e.getMessage());
	        return null;
	    }
	}
	

	private Kleur kiesKleur() {
	    try {
	        System.out.println("Beschikbare kleuren: " + beschikbareKleuren);
	        Kleur gekozenKleur = null;

	        while (gekozenKleur == null) {
	            System.out.print("Kies een kleur: ");
	            String gekozenKleurStr = scanner.nextLine().toLowerCase();

	            for (Kleur kleur : beschikbareKleuren) {
	                if (kleur.name().equalsIgnoreCase(gekozenKleurStr)) {
	                    gekozenKleur = kleur;
	                    break;
	                }
	            }

	            if (gekozenKleur == null) {
	                System.out.println("Ongeldige kleur of al gekozen. Kies een andere kleur.");
	            }
	        }
	        return gekozenKleur;
	    } catch (Exception e) {
	        System.out.println("Fout: " + e.getMessage());
	        return null;
	    }
	}
	

	private void startNieuwSpel() {
	    try {
	        geefSpelers();
	        if (beschikbareSpelers == null || beschikbareSpelers.size() < 3) {
	            return;
	        }

	        while (gekozenSpelers.size() < 6) {
	            List<SpelerDTO> actieveBeschikbareSpelers = new ArrayList<>(beschikbareSpelers);
	            actieveBeschikbareSpelers.removeAll(gekozenSpelers);
	            

	            System.out.println("\nBeschikbare spelers:");
	            for (SpelerDTO speler : actieveBeschikbareSpelers) {
	                System.out.println(speler.gebruikersnaam() + " (" + speler.geboortejaar() + ")");
	            }

	            SpelerDTO gekozenSpeler = kiesSpeler();
	            if (gekozenSpeler == null) continue;

	            Kleur gekozenKleur = kiesKleur();
	            if (gekozenKleur == null) continue;

	            gekozenSpelers.add(gekozenSpeler);
	            spelersKleuren.put(gekozenSpeler, gekozenKleur);
	            beschikbareKleuren.remove(gekozenKleur);

	            if (gekozenSpelers.size() >= 3 && gekozenSpelers.size() < 6) {
	            	String antwoord;
	            	while (true) {
	            		System.out.print("Wil je nog een speler toevoegen? (ja/nee): ");
	            		antwoord = scanner.nextLine().toLowerCase();
	            		if (antwoord.equals("ja") || antwoord.equals("nee")) {
	            			break;
	            		} else {
	            			System.out.println("Ongeldig antwoord. Geef 'ja' of 'nee' in.");
	            		}
	            	}
	            	if (antwoord.equals("nee")) {
	            		break;
	            	}
	            }

	            if (actieveBeschikbareSpelers.size() <= 1) {
	                System.out.println("Er zijn geen spelers meer om toe te voegen.");
	                break;
	            }
	        }

	        dc.startNieuwSpel(spelersKleuren);
	        System.out.println("\nNieuw spel gestart!");
	        toonSpelInfo();
	        System.out.println("\n" + dc.toonBord() + "\n");
	    } catch (Exception e) {
	        System.out.println("Fout: " + e.getMessage());
	    }
	}
	
	
    private void speelRonde() {
        dc.startRonde();
        int aantalSpelers = dc.aantalSpelers();
        int aantalZetstenen = dc.aantalZetstenen();
        
        for(int beurt = 0; beurt < aantalZetstenen; beurt++) {
        	for(int i = 0; i < aantalSpelers; i++) {

        		//Debug
        		//System.out.println("Beurt "+i+" : "+beurt);
           	 	SpelerDTO huidigeSpeler = dc.geefHuidigeSpeler();
                int zetStenenOver = aantalZetstenen-beurt;
                dc.gooiDobbelstenen();
                System.out.println("\n" + "Dobbelstenen worden geworpen voor " + huidigeSpeler.gebruikersnaam() + " met kleur " + huidigeSpeler.kleur() + ", huidige score: "+dc.geefSpelerScore(huidigeSpeler)+" en zetstenen: "+ zetStenenOver);
                System.out.println("\n" + dc.toonWorp() + "\n");

                boolean wilHersmijten = wilHersmijtenDobbelstenen();
                while (wilHersmijten && dc.huidigeAantalWorpen() < 3) {
                    List<Integer> dobbelstenenKeuze = leesInvoerDobbelstenen();
                    dc.hersmijtDobbelstenen(dobbelstenenKeuze);  
                    System.out.println(dc.toonWorp());
                    
                    if (dc.huidigeAantalWorpen() < 3) {
                        wilHersmijten = wilHersmijtenDobbelstenen();
                    } else {
                        wilHersmijten = false;
                    }
                }
                
                plaatsZetsteen(huidigeSpeler);
                
                System.out.println("Beurt afgerond voor " + huidigeSpeler.gebruikersnaam());
                System.out.println("\n" + dc.toonBord() + "\n");
                dc.volgendeAanBeurt();
           }
        	dc.resetSpelerWorpen();
        }
        dc.bepaalBeloningen();
        dc.evalueerGebouwPuntengebied();
       
    }

    private void plaatsZetsteen(SpelerDTO huidigeSpeler) {
        Map<Integer, Gebouw> gebouwMap = dc.getUniekeGebouwenUitWorp();
        
        if (gebouwMap.isEmpty()) {
            System.out.println("Er zijn geen unieke gebouwen beschikbaar om te plaatsen.");
            return;
        }

        System.out.println("Kies een gebouw om je zetsteen op te plaatsen:");
        for (Map.Entry<Integer, Gebouw> entry : gebouwMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        int gekozenKey = -1;
        while (true) {
            System.out.print("Voer het nummer in van het gebouw dat je wilt kiezen: ");
            String invoer = scanner.nextLine().trim();

            try {
                gekozenKey = Integer.parseInt(invoer);
                if (gebouwMap.containsKey(gekozenKey)) {
                    break;  
                } else {
                    System.out.println("Ongeldige keuze. Kies een van de beschikbare nummers.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ongeldige invoer. Voer een geldig getal in.");
            }
        }

        Gebouw gekozenGebouw = gebouwMap.get(gekozenKey);
        dc.plaatsZetsteen(huidigeSpeler, gekozenGebouw);
        System.out.println("Je hebt je zettegel geplaatst op " + gekozenGebouw);
    }



    private boolean wilHersmijtenDobbelstenen() {
        String antwoord = "";
        while (true) {
            System.out.print("Wil je de dobbelstenen opnieuw gooien? (ja/nee): ");
            antwoord = scanner.nextLine().toLowerCase();

            if (antwoord.equals("ja")) {
                return true;
            } else if (antwoord.equals("nee")) {
                return false;
            } else {
                System.out.println("Ongeldige invoer. Geef 'ja' of 'nee' in.");
            }
        }
    }


    private void toonSpelInfo() {
        try {
            List<SpelerDTO> spelInfo = dc.geefSpelersVoorSpel();
            System.out.println("\n" + "Speloverzicht:" + "\n");
            System.out.println("Startspeler: " + dc.geefStartSpeler().gebruikersnaam());
            for (SpelerDTO spelerDTO : spelInfo) {
                System.out.printf("Speler: %s, Leeftijd: %d, Kleur: %s, Zetstenen: %d\n",
                        spelerDTO.gebruikersnaam(),
                        huidigJaar - spelerDTO.geboortejaar(),
                        spelerDTO.kleur() != null ? spelerDTO.kleur().name() : "Geen kleur",
                        spelerDTO.zetStenen());
            }
        } catch (Exception e) {
            System.out.println("Fout: " + e.getMessage());
        }
    }

    private int maakKeuze(String[] keuzes) {
        int keuze = 0;
        boolean geldigeKeuze = false;
        do {
            System.out.println("\nMENU");
            System.out.println("======");
            for (int i = 0; i < keuzes.length; i++) {
                System.out.println(String.format("%d. %s", i + 1, keuzes[i]));
            }
            System.out.printf("Geef je keuze in: ");            
            while (!geldigeKeuze) {
                String invoer = scanner.nextLine().trim(); 
                if (invoer.matches("\\d+")) {
                    keuze = Integer.parseInt(invoer); 
                    
                    if (keuze >= 1 && keuze <= keuzes.length) {
                        geldigeKeuze = true; 
                    } else {
                        System.out.println("Kies een nummer tussen 1 en " + keuzes.length + ".");
                        break;
                    }
                } else {
                    System.out.println("Ongeldige invoer. Geef een getal in.");
                    break;
                }
            }

        } while (keuze == 0);
        return keuze;
    }

    private List<Integer> leesInvoerDobbelstenen() {
        List<Integer> dobbelstenenKeuze = new ArrayList<>();
        
        while (true) { 
            System.out.println("Kies de dobbelstenen die je opnieuw wilt gooien (gescheiden door spaties): ");
            String invoer = scanner.nextLine().trim();

            String[] delen = invoer.split("\\s+");
            dobbelstenenKeuze.clear();  
            
            boolean geldigeInvoer = true;
            for (String deel : delen) {
                try {
                    int nummer = Integer.parseInt(deel);
                    if (nummer >= 1 && nummer <= 8) {
                        dobbelstenenKeuze.add(nummer - 1);  
                    } else {
                        throw new IllegalArgumentException("Ongeldige keuze: " + nummer + ". Kies een dobbelsteen tussen 1 en 8.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ongeldige invoer: '" + deel + "'. Voer een geldig getal in.");
                    geldigeInvoer = false;
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());  
                    geldigeInvoer = false;
                    break;
                }
            }

            if (geldigeInvoer) {
                return dobbelstenenKeuze;  
            }
        }
    }

    private void eindigSpel() {
		System.out.println(dc.eindigSpel());
		
	}




}