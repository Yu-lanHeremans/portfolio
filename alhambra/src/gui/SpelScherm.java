package gui;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import domein.Dobbelsteen;
import domein.DomeinController;
import domein.GebouwTegel;
import domein.Spel;
import domein.Spelbord;
import dto.SpelerDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.Gebouw;
import utils.Kleur;

public class SpelScherm extends StackPane {
    private final DomeinController dc;
    private GridPane bordGrid;
    private GridPane infoGrid;
    private GridPane meldingGrid;
    	private Label lblMelding;
    StackPane dobbelBox;
    private Pane dicePane;
	private String[] kleurEngels = new String[]{"blue", "green", "white", "yellow", "orange", "red"};
	private String[] bonusFichesNaam = new String[] {"Een", "Twee", "Drie"};
	private List<String> bonusFichesNummer = new ArrayList<>(Arrays.asList("1", "2", "3"));    
	int huidigJaar = Year.now().getValue();
    private HBox mainContent;
    private VBox spelContent;
    private VBox infoContent;
    private Pane meldingPane;

    public boolean hersmijtenGedrukt = false;
    public boolean beurtKlaar = false; //Voorkomt meerdere zetstenen plaatsen per beurt

    List<Double[]> ingenomenCoordsList = new ArrayList<>();

    private VBox meldingContent;
    private Double[][] fichesCoords = {
    	    {84.2, 443.8},
    	    {165.4, 443.8},
    	    {250.6, 443.8},
    	    {335.2, 443.8},
    	    {420.0, 443.8},
    	    {504.0, 443.8}
    	};

    private Double[][] gebouwPuntenCoords = {
    		{69.2, 421.4},  
    	    {61.8, 387.0},  
    	    {65.4, 352.6},  
    	    {85.6, 322.2},  
    	    {102.6, 293.6}, 
    	    {103.0, 260.8}, 
    	    {83.7, 233.5},  
    	    {71.4, 203.0},  
    	    {78.8, 169.6},  
    	    {103.2, 145.4}, 
    	    {110.8, 112.9}, 
    	    {109.8, 79.1}
    	};
    
    private Double [][] zetstenenCoords = {
    		{57.4, 708.0},
    		{83.8, 714.4},
    		{109.4, 720.8},
    };
    
    
    private Set<Integer> selectedDiceIndices = new HashSet<>(); // indexen van geklikte stenen bijhouden
    private Map<Integer, Rectangle> diceMap = new HashMap<>();  // gezette stenen opslaan obv hun index
    
    private boolean nietMeerSmijten = false;
    private int beurtNr = 1;
    private int rondeNr = 1;

    
    
    
    public SpelScherm(DomeinController dc) {
    	
        this.dc = dc;
        buildGui();
    }
    
        
    private void buildGui() {
    	meldingContent = new VBox();
    	infoContent = new VBox();
    	spelContent = new VBox();
    		Pane spelbordPane = new Pane();  
			spelContent.getChildren().add(spelbordPane);
    	mainContent = new HBox();
    	dobbelBox = new StackPane();
    	dicePane = new Pane(); // pane om dobbelstenen te verspreiden
    	
    	
    	//MainContent alle ruimte geven
    	mainContent.setMaxWidth(Double.MAX_VALUE);
    	
        bordGrid = new GridPane();
        bordGrid.setPadding(new Insets(10));
        
        meldingGrid = new GridPane();
        meldingGrid.setAlignment(Pos.CENTER);
        
        HBox.setHgrow(meldingContent, Priority.ALWAYS);
        
        meldingContent.setStyle(
        	    "-fx-background-color: rgba(128, 128, 128, 0.3); "+ 
        	    "-fx-border-color: white;"   +
        	    "-fx-border-width: 2px;"      +                   
        	    "-fx-border-radius: 5px;"      +                 
        	    "-fx-padding: 20px;"
        	);
        
    	infoGrid = new GridPane();
    	infoGrid.setPadding(new Insets(10));
    	    
    	infoGrid.setAlignment(Pos.CENTER_RIGHT);
    	infoContent.setStyle(
        	    "-fx-background-color: rgba(128, 128, 128, 0.3); "+ 
        	    "-fx-border-color: white;"   +
        	    "-fx-border-width: 2px;"      +                   
        	    "-fx-border-radius: 5px;"      +                 
        	    "-fx-padding: 20px;"+
        	    "-fx-margin: 20px;"
        	);

    	ImageView background = new ImageView(
            new Image(getClass().getResource("/resources/images/AlhambraSpelScherm.png").toExternalForm())
        );
        
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(this.widthProperty());
        background.fitHeightProperty().bind(this.heightProperty());
        
        
        Image bgSpelBord= new Image(getClass().getResource("/resources/images/spelbord.png").toExternalForm());

        // spelbord als achtergrond van de VBox
        BackgroundImage spelbordImage = new BackgroundImage(
        	bgSpelBord,
            BackgroundRepeat.NO_REPEAT,       
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,         
            new BackgroundSize(
                100, 100,                      
                true,                          
                true,                       
                true,                        
                false                          
            )
        );
        
        
        spelContent.setBackground(new Background(spelbordImage));
        spelContent.setMinSize(590, 590 * (bgSpelBord.getHeight() / bgSpelBord.getWidth())); // respecteren van aspect ratio  
        
        ImageView dobbelPlaats = new ImageView(
                new Image(getClass().getResource("/resources/images/Dobbelsteenplaats.png").toExternalForm())
            );
        
        dobbelPlaats.setPreserveRatio(true);
        dobbelPlaats.setFitWidth(400);
        
        
        dobbelBox.getChildren().addAll(dobbelPlaats, dicePane); // stapelen van image and dobbelstenen
        dobbelBox.setAlignment(Pos.TOP_CENTER);
        infoContent.getChildren().addAll(dobbelBox, infoGrid);
        
        Tooltip ttDobbel = new Tooltip("Smijt stenen");
        Button btnDobbel = new Button("");
        btnDobbel.setStyle("-fx-background-color: none; -fx-border-color: none;");
        btnDobbel.setTooltip(ttDobbel);
        
        Image imgDobbel = new Image(getClass().getResourceAsStream("/resources/images/dobbelsteen.png"));
        ImageView ivDobbel = new ImageView(imgDobbel);
        ivDobbel.setFitWidth(60);
        ivDobbel.setPreserveRatio(true);
        infoGrid.add(btnDobbel, 0, 2);        

        
        btnDobbel.setGraphic(ivDobbel);
        
        btnDobbel.setOnAction(event -> {
        	if (!beurtKlaar && !nietMeerSmijten) {
        	smijtStenen();
        	}
        });
        
        Tooltip ttHersmijten = new Tooltip("Hersmijt aangeklikte stenen");
        
        Button btnHersmijten = new Button();
        btnHersmijten.setTooltip(ttHersmijten);
        btnHersmijten.setStyle("-fx-background-color: none; -fx-border-color: none;");
        Image imgHersmijten = new Image(getClass().getResourceAsStream("/resources/images/dobbelsteenOpnieuw.png"));
        ImageView ivHersmijten = new ImageView(imgHersmijten);
        ivHersmijten.setFitWidth(60);
        ivHersmijten.setPreserveRatio(true);
        infoGrid.add(btnHersmijten, 0, 3);

        btnHersmijten.setGraphic(ivHersmijten);
        btnHersmijten.setOnAction(e -> {
            hersmijtenGedrukt = true;
        if (dc.huidigeAantalWorpen() < 3 && !beurtKlaar && !nietMeerSmijten) {
        	if (!selectedDiceIndices.isEmpty()) {
                hersmijtGeselecteerdeStenen();

            } else {
            	Alert alert = new Alert(Alert.AlertType.INFORMATION, "Selecteer eerst dobbelstenen");
    			alert.showAndWait(); 
    			}
        } else {
        	Alert alert = new Alert(Alert.AlertType.INFORMATION, "U mag niet nogmaals smijten");
        	alert.showAndWait();
        }
        	hersmijtenGedrukt = false;
            
        });

        Tooltip ttKlaarMetSmijten = new Tooltip("Klaar met smijten");
        
        Button btnKlaarMetSmijten = new Button();
        btnKlaarMetSmijten.setTooltip(ttKlaarMetSmijten);
        btnKlaarMetSmijten.setStyle("-fx-background-color: none; -fx-border-color: none;");
        Image imgKlaar = new Image(getClass().getResourceAsStream("/resources/images/done.png"));
        ImageView ivKlaar = new ImageView(imgKlaar);
        ivKlaar.setFitWidth(60);
        ivKlaar.setPreserveRatio(true);
        
        btnKlaarMetSmijten.setGraphic(ivKlaar);
                
        btnKlaarMetSmijten.setOnAction(evt -> {
        	if (!beurtKlaar && !nietMeerSmijten) {
        		nietMeerSmijten = true;
        		plaatsZetsteen(dc.geefHuidigeSpeler());                     	
        	}
    	});
        
        infoGrid.add(btnKlaarMetSmijten, 1, 3);
        
       
        
        lblMelding = new Label("");
        lblMelding.setText("");
        lblMelding.setFont(Font.font("System", FontWeight.NORMAL, 15));
        lblMelding.setStyle("-fx-text-fill: white;");
        meldingGrid.add(lblMelding, 0, 0);

        
        Label lblTitelBord = new Label("Spelbord");
        bordGrid.add(lblTitelBord, 0, 0);

        //HBox.setHgrow(meldingContent, Priority.ALWAYS);
        
        meldingContent.getChildren().addAll(meldingGrid);
        mainContent.getChildren().addAll(spelContent, meldingContent, infoContent);
        this.getChildren().addAll(background, mainContent);
        
        speelRonde();
        toonSpelInfo();

    }
    
    
    private void toonSpelInfo() {
    	
    	
    	try {
            List<SpelerDTO> spelInfo = dc.geefSpelersVoorSpel();
            
            Label lblOverzicht = new Label("Speloverzicht:" + "\n");
            lblOverzicht.setFont(Font.font("System", FontWeight.NORMAL, 20));
            lblOverzicht.setStyle("-fx-text-fill: white;");
            infoGrid.add(lblOverzicht, 0, 4);
            

            
            Label lblStartSpeler = new Label();
            lblStartSpeler.setText("Startspeler: " + dc.geefStartSpeler().gebruikersnaam());
            
            lblStartSpeler.setFont(Font.font("System", FontWeight.NORMAL, 15));
            lblStartSpeler.setStyle("-fx-text-fill: white;");

            
            infoGrid.add(lblStartSpeler, 0, 5);
            
            int spelerInfoPlaats = 6;
            
            for (SpelerDTO spelerDTO : spelInfo) {
                        Label lblSpelerInfo = new Label();
                        
                        lblSpelerInfo.setText(String.format("Speler: %s (%d) Kleur: %s, Zetstenen: %d\n",
                        spelerDTO.gebruikersnaam(),
                        huidigJaar - spelerDTO.geboortejaar(),
                        spelerDTO.kleur().name(),
                        spelerDTO.zetStenen()));
                        
            	         Kleur gevraagdeKleur = spelerDTO.kleur();
            	         int i = gevraagdeKleur.ordinal();
                        
                        
                        lblSpelerInfo.setFont(Font.font("System", FontWeight.NORMAL, 15));
                        lblSpelerInfo.setStyle("-fx-text-fill: " + kleurEngels[i] + ";");
                        infoGrid.add(lblSpelerInfo, 0, spelerInfoPlaats);
                        spelerInfoPlaats++; 

            }
            
        } catch (Exception e) {
            System.out.println("Fout: " + e.getMessage());
        }
    }
    

    
    private void speelRonde() {
    	
    	
    	if (rondeNr <= 3) {
        	
        	((Pane) spelContent.getChildren().get(0)).getChildren().removeIf(node -> 
                node instanceof Circle && "zetsteen".equals(node.getUserData())
            );
            dc.startRonde();
            toonSpelInfo();
            setFiches();
            startBeurt();

        }
        else {
        	Stage stage = (Stage) this.getScene().getWindow();
     		stage.setScene(new Scene(new EindScherm(dc)));
     		
        }
    }
    
    
    
    private void startBeurt() {
    	
    	//Dobbelstenen-zone leegmaken
         dicePane.getChildren().clear();
         selectedDiceIndices.clear();
         diceMap.clear();
        
         //Reset beurt-vlag
         beurtKlaar = false;
         
         //als alle spelers hun beurt gehad hebben: vorige gebouwstenen verwijderen en ...
         if (beurtNr > dc.aantalSpelers() * dc.aantalZetstenen() && rondeNr <= 3) {
        	 ((Pane) spelContent.getChildren().get(0)).getChildren().removeIf(node -> 
             node instanceof Circle && "gebouwsteen".equals(node.getUserData())
         );
        	 
        	 dc.bepaalBeloningen();
             dc.evalueerGebouwPuntengebied();
             plaatsGebouwPunten();

             beurtNr = 1; // resetten van beurtTeller
             rondeNr++;
             speelRonde();
         }
         
         lblMelding.setText(
            "Beurt voor " + dc.geefHuidigeSpeler().gebruikersnaam() + 
            "\nSmijt uw stenen..." +
            "\nAantal worpen over: " + (3 - dc.huidigeAantalWorpen()) +
            "\nHuidige score: " + dc.geefSpelerScore(dc.geefHuidigeSpeler()) +
            "\nZetstenen: " + dc.aantalZetstenen()
            
        );
         
         meldingContent.getChildren().removeIf(node -> 
         node instanceof Button && "btnzetsteen".equals(node.getUserData()));
    }
    
    

    
    private void setFiches() {

    	//Om makkelijk ingenomen coords te verwijderen:
    	List<Double[]>beschikbareCoordsList = new ArrayList<>(Arrays.asList(fichesCoords));
    	String[] fichesGebied = dc.geefFichesGebied();
    	
    	System.out.println(String.join(", ", dc.geefFichesGebied()));    	
    	
    	for (int i = 0; i <  6; i++) {
        	Double[] coord = beschikbareCoordsList.get(i);
            Double x = coord[0];  // X position
            Double y = coord[1];  // Y position
            
            
            if ((fichesGebied[i]).equals("STRT")) {
        		ImageView startFiche = new ImageView(
        	            new Image(getClass().getResource("/resources/images/startFiche.png").toExternalForm())
        	        );
        		startFiche.setTranslateX(x);
        	    startFiche.setTranslateY(y);
        		startFiche.setFitWidth(40);
        	    startFiche.setPreserveRatio(true);
        	    startFiche.setStyle("-fx-background-radius: 5;");
        	    ((Pane) spelContent.getChildren().get(0)).getChildren().add(startFiche);
        	
        	}
            
            if (fichesGebied[i].isEmpty() || fichesGebied[i] == null) {
            	continue;
            }
            

            else {
                try {
                    
                if (bonusFichesNummer.contains(fichesGebied[i])) {
                int index = bonusFichesNummer.indexOf(fichesGebied[i]);
        		ImageView bonusFiche = new ImageView(
        	            new Image(getClass().getResource(String.format("/resources/images/bonus%s.png", bonusFichesNaam[index])).toExternalForm())
        	        );
        		bonusFiche.setTranslateX(x);
        		bonusFiche.setTranslateY(y);
        		bonusFiche.setFitWidth(40);
        		bonusFiche.setPreserveRatio(true);
        	    ((Pane) spelContent.getChildren().get(0)).getChildren().add(bonusFiche);

        			}
            }	catch (Exception e) {
        	Alert alert = new Alert(Alert.AlertType.ERROR, "Fout: " + e.getMessage());
			alert.showAndWait();
            	
            		}  
            	
        	}
    	}
    }
    
    
    private String[] getDobbelWaarden() {
        String worp = dc.waardenWorp();
        return worp.split(" ");
    }
    
    
    public String[] getGeklikteStenen() {
        return selectedDiceIndices.stream()
               .map(Object::toString)
               .toArray(String[]::new);
    }
    
    
    private void smijtStenen() {
    	
    	if (beurtKlaar) {
    		return;
    	}
    		
    	selectedDiceIndices.clear();
    	dc.gooiDobbelstenen();
    	diceMap.clear();
    	
    	if (dc.huidigeAantalWorpen() >= 3) {
    		Alert alert = new Alert(Alert.AlertType.ERROR, "Max aantal worpen bereikt");
			alert.showAndWait();
			return;
    	}
    	
        Random r = new Random();
        //op grootte van de foto
        double minX = 15;
        double maxX = 400;
        double minY = 15;
        double maxY = 400;
        
        dicePane.getChildren().removeIf(node -> node instanceof Rectangle);
        
        String[] worp = getDobbelWaarden();
        
        // bijhouden posities van al geplaatste stenen(x, y)
        List<Rectangle> placedDice = new ArrayList<>();
        
        for (int i = 0; i < worp.length; i++) {
        	 final int dobbelIndex = i;  // final kopie van i, deed gelijk raar anders
        	String db = worp[i];
            Rectangle vorm = new Rectangle(0, 0, 60, 60);
            vorm.setArcWidth(30.0);
            vorm.setArcHeight(30.0);

            String imagePad = "/resources/images/" + db + ".jpg";
            ImagePattern dobbelsteen = new ImagePattern(
                new Image(getClass().getResource(imagePad).toExternalForm())
            );
            vorm.setFill(dobbelsteen);
            
            // zoeken naar vrije plaatsen
            boolean plaatsGevonden;
            double x, y;
            int pogingen = 0;
            int maxPogingen = 100; // voorkomen van infinite loops
            
            do {
                plaatsGevonden = true;
                x = r.nextDouble(minX, maxX - 60); 
                y = r.nextDouble(minY, maxY - 60);
                
                // checken of nieuwe plaats overlapt met bestaande 
                for (Rectangle existingDice : placedDice) {
                    if (Math.abs(existingDice.getLayoutX() - x) < 60 && 
                        Math.abs(existingDice.getLayoutY() - y) < 60) {
                        plaatsGevonden = false;
                        break;
                    }
                }
                
                pogingen++;
                if (pogingen >= maxPogingen) {
                    // worst case: toch plaasten wanneer geen plaats gevonden
                    break;
                }
            } while (!plaatsGevonden);
            
            vorm.setLayoutX(x);
            vorm.setLayoutY(y);
            vorm.setStyle("-fx-background-radius: 5;");
            diceMap.put(i, vorm);
            
            vorm.setOnMouseClicked(event -> {
            	
                if (selectedDiceIndices.contains(dobbelIndex)) {
                    // deselecteren als deze al aangeklikt is
                    vorm.setStroke(null);
                    selectedDiceIndices.remove(dobbelIndex);
                } else {
                    // selecteren bij nieuwe klik
                    vorm.setStroke(Color.GOLDENROD);
                    vorm.setStrokeWidth(4);
                    selectedDiceIndices.add(dobbelIndex);   
                }
            });
            dicePane.getChildren().add(vorm);
            placedDice.add(vorm); // deze steen zijn plaats tracken
        	}
    	
    }
    
    
    private void hersmijtGeselecteerdeStenen() {
        if (selectedDiceIndices.isEmpty()) return;
        
        
        List<Integer> dobbelstenenKeuze = new ArrayList<>(selectedDiceIndices);
        dc.hersmijtDobbelstenen(dobbelstenenKeuze);
        
        String[] nieuweWorp = getDobbelWaarden();
        
        for (int i : selectedDiceIndices) {
            Rectangle dice = diceMap.get(i);
            if (dice != null) {
                String worp = nieuweWorp[i];
                ImagePattern newImage = new ImagePattern(
                    new Image(getClass().getResource("/resources/images/" + worp + ".jpg").toExternalForm())
                );
                dice.setFill(newImage);
                dice.setStroke(null);
            }
        }
        selectedDiceIndices.clear();
    }
    
    

    
    
    private void plaatsGebouwPunten() {
    	List<Double[]> beschikbareCoordsList = new ArrayList<>(Arrays.asList(gebouwPuntenCoords));
    	List<SpelerDTO> spelers = dc.geefSpelersVoorSpel();
    	//beste zetsteenresultaat krijgt 2 gebouwpunten, tweede beste krijgt 1 gebouwpunt en bijhorende fiche
    	
    	GebouwTegel[][] gebouwtegels = dc.geefGebouwPuntenGebied();

    	for (int i = 0; i < 6; i++) {
    		for (int j = 11; j >= 0; j--) {
        	GebouwTegel gt = gebouwtegels[i][j];
    		

        	Double[] coords = beschikbareCoordsList.get(j);
	    	Double x = coords[0] + (i * 85);  // X positie, maal 85 omdat de plaats tussen de kolommen zoveel is
	        Double y = coords[1];  // Y positie

	        Circle circle = new Circle();
			circle.setRadius(15);

        	List<Kleur> tegelsKleuren = gt.getKleuren();
        	for (Kleur k : tegelsKleuren) {
        		if (gt.heeftKleur(k)) {
        			circle.setUserData("gebouwsteen");
        			circle.setFill(Color.valueOf(kleurEngels[k.ordinal()]));
        			circle.setTranslateX(x);
        			circle.setTranslateY(y);
        		}
        		else {
        			break;
        		}
        	}
        	
        	
		    ((Pane) spelContent.getChildren().get(0)).getChildren().add(circle);
			   	
    		}
    	}    			
    			
  }    
    

    private void plaatsZetsteen(SpelerDTO huidigeSpeler) {
    	Map<Integer, Gebouw> gebouwMap = dc.getUniekeGebouwenUitWorp();
    	List<Double[]> beschikbareCoordsList = new ArrayList<>(Arrays.asList(zetstenenCoords));

    	
    	if (gebouwMap.isEmpty()) {
    		lblMelding.setText("Er zijn geen unieke gebouwen beschikbaar om te plaatsen");
    		return;
    	}
    	
    	
    	
    	for (Gebouw gebouw : gebouwMap.values()) {
    		ImageView gebouwImg = new ImageView(
                    new Image(getClass().getResource("/resources/images/" + gebouw + ".jpg").toExternalForm())
                );
    		
    		
    		
    		gebouwImg.setFitWidth(40);
    		gebouwImg.setStyle("-fx-border-radius: 5px;");
    		gebouwImg.setPreserveRatio(true);
    		Tooltip ttZetsteen = new Tooltip("Plaats zetsteen op " + gebouw.name());

    		Button btnZetsteen = new Button();
    		btnZetsteen.setUserData("btnzetsteen");
    		btnZetsteen.setTooltip(ttZetsteen);
    		btnZetsteen.setStyle("-fx-background-color: none; -fx-border-color: none;");
     		btnZetsteen.setGraphic(gebouwImg);
     		meldingContent.getChildren().addAll(btnZetsteen);

     		
    		btnZetsteen.setOnAction(evt -> {
    			
    			if (!beurtKlaar) {

    				
    				int worpen = dc.huidigeAantalWorpen();
    				int rij = dc.zetsteenRij(gebouw);
    				Double[] coords = null;
    				Double x = null;
    				Double y = null;

    				boolean plaatsGevonden = false;
    				int huidigeRij = dc.zetsteenRij(gebouw);
    				int huidigeKol = worpen - 1;

    				
    				outerLoop:
    				do  {
    				   
    				    if (huidigeRij < rij) {
    				    	huidigeKol = 0;
    				    }
    				    
    				    while (huidigeKol < 3) {
    				        if (huidigeKol >= beschikbareCoordsList.size()) {
    				        	huidigeKol++;
    				            continue;
    				        }
    				        
    				        
    				        coords = beschikbareCoordsList.get(huidigeKol);
    				        x = coords[0] + (gebouw.geefIndex() * 85);
    				        y = coords[1] + ((huidigeRij - 1) * -36);
    				        Double[] ingenomenCoords = {x, y};
    				        
    				        
    				        if (!bevatCoord(ingenomenCoordsList, ingenomenCoords)) {
    				            ingenomenCoordsList.add(ingenomenCoords);
    				            plaatsGevonden = true;
    				            break outerLoop;
    				        }
    				        
    				        huidigeKol++;
    				    }
    				    
    				    huidigeRij--;
    				} while (huidigeRij >= 0);

    				if (!plaatsGevonden) {
    					
    				    throw new IllegalStateException("Geen vrije plaats gevonden voor zetsteen");
    				}
            		
        			
    			Circle circle = new Circle();
    			circle.setUserData("zetsteen");  // markeren als zetsteen, zo makkelijk deze verwijderen bij einde van ronde
    			circle.setRadius(12);
    			circle.setFill(Color.valueOf(kleurEngels[dc.geefHuidigeSpeler().kleur().ordinal()].toUpperCase()));
    			circle.setLayoutX(x);
    			circle.setLayoutY(y);
        		dc.plaatsZetsteen(huidigeSpeler, gebouw);
        		
    		    ((Pane) spelContent.getChildren().get(0)).getChildren().add(circle);
    		    
    		    
    		    beurtKlaar = true;
    		    nietMeerSmijten = false;
    		    beurtNr++;
    		    dc.volgendeAanBeurt();
    		    dc.resetSpelerWorpen();
    		    startBeurt();    		    
    		    
    			}
    		});
    	
    	lblMelding.setText("Kies een gebouw om je zetsteen op te plaatsen:");
    			
    	}		
    	
    	
    }
    
    private boolean bevatCoord(List<Double[]> list, Double[] coords) {
        for (Double[] existing : list) {
            if (existing[0].equals(coords[0]) && existing[1].equals(coords[1])) {
                return true;
            }
        }
        return false;
    }


}