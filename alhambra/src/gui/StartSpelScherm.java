package gui;

import java.util.HashMap;
import java.util.Map;

import domein.DomeinController;
import dto.SpelerDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import utils.Kleur;

public class StartSpelScherm extends VBox {
	private final DomeinController dc;
	public Map<SpelerDTO, Kleur> spelersKleuren = new HashMap<>();
	private final GridPane spelersTable = new GridPane();
	private String[] kleurEngels = new String[]{"blue", "green", "white", "yellow", "orange", "red"};
	
	
	public StartSpelScherm(DomeinController dc) {
		this.dc = dc;
		buildGui();
	}
	
	private void buildGui() {
		
		GridPane content = new GridPane();
		setSchaduw(content);
		
		BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage achtergrond = new BackgroundImage(
            new Image(getClass().getResource("/resources/images/AlhambraStartScreenHor.png").toExternalForm()), 
            BackgroundRepeat.NO_REPEAT, 
            BackgroundRepeat.NO_REPEAT, 
            BackgroundPosition.CENTER, 
            backgroundSize
        );
        this.setBackground(new Background(achtergrond));
        this.setAlignment(Pos.CENTER);
        VBox.setVgrow(content, Priority.NEVER);

		
		content.setAlignment(Pos.CENTER);
		content.setHgap(10);
		content.setVgap(10);
		content.setGridLinesVisible(false);
		content.setPadding(new Insets(25, 25, 25, 25));
		content.setMaxWidth(Region.USE_PREF_SIZE);
		content.setPrefWidth(Region.USE_COMPUTED_SIZE);
		content.setStyle(
	    	    "-fx-background-color: rgba(255, 255, 255, 0.6); "+ 
	    	    "-fx-border-color: white;"   +
	    	    "-fx-border-width: 2px;"      +                   
	    	    "-fx-border-radius: 5px;"      +                 
	    	    "-fx-padding: 20px;"+
	    	    "-fx-margin: 20px;"
	    	);
		
		
		ObservableList<String> keuzeSpelers = FXCollections.observableArrayList();
		final ComboBox<String> beschikbareSpelersComboBox = new ComboBox<String>(keuzeSpelers);
		content.add(beschikbareSpelersComboBox, 0, 1);
		beschikbareSpelersComboBox.setPrefWidth(200);
		
		
		for (SpelerDTO spelerDTO : dc.geefAlleSpelers()) {
			beschikbareSpelersComboBox.getItems().addAll(spelerDTO.gebruikersnaam() + " (" +  spelerDTO.geboortejaar() + ")");
                }
		
		ObservableList<Kleur> kleurenSpelers = FXCollections.observableArrayList();
		final ComboBox<Kleur> beschikbareKleurenComboBox = new ComboBox<Kleur>(kleurenSpelers);
		content.add(beschikbareKleurenComboBox, 0, 2);
		beschikbareKleurenComboBox.setPrefWidth(200);

		
		for (Kleur kleur : dc.getBeschikbareKleuren() ) {
			beschikbareKleurenComboBox.getItems().add(kleur);
                }
		
		Button btnTerug = new Button("\u2190"); //Pijltje terug
		content.add(btnTerug, 0, 0, content.getColumnCount(), 1);
		btnTerug.setFont(Font.font(40));
		btnTerug.setAlignment(Pos.TOP_LEFT);
		btnTerug.setStyle("-fx-border-width: 0; -fx-background-color: none");
		
		btnTerug.setOnAction(evt -> {
			this.getScene().setRoot(new StartScherm(dc));
		});
		
		
		Button btnVoegSpelerToeAanSpel = new Button("Voeg toe");
		content.add(btnVoegSpelerToeAanSpel, 0, 3);
		btnVoegSpelerToeAanSpel.setPrefWidth(70);
		btnVoegSpelerToeAanSpel.setStyle("-fx-background-color: white");
		btnVoegSpelerToeAanSpel.setOnAction(evt -> {
		    String spelerNaam = beschikbareSpelersComboBox.getValue();
		    Kleur gekozenKleur = beschikbareKleurenComboBox.getValue();

		try {
		    
		        SpelerDTO geselecteerdeSpeler = dc.geefAlleSpelers().stream()
		            .filter(speler -> speler.gebruikersnaam().equals(spelerNaam.split(" \\(")[0]))
		            .findFirst()
		            .orElse(null);
		        
		            spelersKleuren.put(geselecteerdeSpeler, gekozenKleur);
		            updateSpelersTable();
		            
		            beschikbareSpelersComboBox.getItems().remove(beschikbareSpelersComboBox.getValue());
		            beschikbareKleurenComboBox.getItems().remove(beschikbareKleurenComboBox.getValue());
		            
		            beschikbareSpelersComboBox.getSelectionModel().clearSelection();
		            beschikbareKleurenComboBox.getSelectionModel().clearSelection();
		    
		            
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Fout: Beide velden moeten ingevuld zijn");
			alert.showAndWait();
		}
		});

		Button btnStartSpel = new Button("Start");
		btnStartSpel.setStyle("-fx-background-color: white;");
		content.add(btnStartSpel, 2, 3);
		GridPane.setHalignment(btnStartSpel, HPos.RIGHT);
		btnStartSpel.setPrefWidth(70);
		btnStartSpel.setOnAction(evt -> {
			try {
				dc.startNieuwSpel(spelersKleuren);
			    veranderScherm(new SpelScherm(dc));
			} catch (Exception e) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Fout: " + e.getMessage());
				alert.showAndWait();
			}
		});
		
		
		content.add(spelersTable, 2, 0, 1, 1);
		updateSpelersTable(); 
		
		this.getChildren().addAll(content);
	}

	
	public void updateSpelersTable() {
	    spelersTable.getChildren().clear();

	    Label lblSpeler = new Label("Speler");
	    Label lblKleur = new Label("Kleur");

	    lblSpeler.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
	    lblKleur.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

	    lblSpeler.setPrefWidth(100);
	    spelersTable.add(lblSpeler, 0, 0);
	    
	    lblKleur.setPrefWidth(100);
	    spelersTable.add(lblKleur, 1, 0);

	   
	    
	    int row = 1;
	    for (Map.Entry<SpelerDTO, Kleur> entry : spelersKleuren.entrySet()) {
	        Label spelerLabel = new Label(entry.getKey().gebruikersnaam());
	        Label kleurLabel = new Label(entry.getValue().toString());
	        
	         Kleur gevraagdeKleur = entry.getValue();
	         int i = gevraagdeKleur.ordinal();

	        spelersTable.add(spelerLabel, 0, row);
	        spelersTable.add(kleurLabel, 1, row);
	        kleurLabel.setStyle("-fx-text-fill: " + kleurEngels[i] );

	        row++;
	    }
	}
	
	private void veranderScherm(Pane newScreen) {
        Stage stage = (Stage) this.getScene().getWindow();
		stage.setScene(new Scene(newScreen));
        stage.setFullScreen(true);
    }

	
	 private void setSchaduw(GridPane g) {
	    	DropShadow dropShadow = new DropShadow();
			dropShadow.setRadius(10);          // Blur radius
			dropShadow.setOffsetX(10);          // Horizontal offset
			dropShadow.setOffsetY(15);          // Vertical offset
			dropShadow.setColor(Color.rgb(0, 0, 0, 0.6)); // Shadow color (black with 30% opacity)
			
			g.setEffect(dropShadow);
		
	    }
	
	
}









