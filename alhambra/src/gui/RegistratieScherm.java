package gui;

import domein.DomeinController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RegistratieScherm extends VBox {
		private final DomeinController dc;
		private GridPane content;
		
		
		public RegistratieScherm(DomeinController dc) {
			this.dc = dc;
			buildGui();
			
		}
		
		private void buildGui() {
			
			content = new GridPane();
			
			content.setMaxWidth(Region.USE_PREF_SIZE);
			content.setPrefWidth(Region.USE_COMPUTED_SIZE);
			content.setStyle(
		    	    "-fx-background-color: rgba(255, 255, 255, 0.4); "+ 
		    	    "-fx-border-color: white;"   +
		    	    "-fx-border-width: 2px;"      +                   
		    	    "-fx-border-radius: 5px;"      +                 
		    	    "-fx-padding: 20px;"+
		    	    "-fx-margin: 20px;"
		    	);
			
			setSchaduw(content);
			
			BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
	        BackgroundImage achtergrond = new BackgroundImage(
	            new Image(getClass().getResource("/resources/images/AlhambraStartScreenHor.png").toExternalForm()), 
	            BackgroundRepeat.NO_REPEAT, 
	            BackgroundRepeat.NO_REPEAT, 
	            BackgroundPosition.CENTER, 
	            backgroundSize
	        );
	        this.setAlignment(Pos.CENTER);
	        this.setBackground(new Background(achtergrond));
			
			content.setAlignment(Pos.CENTER);
			content.setHgap(10);
			content.setVgap(10);
			
			content.setPadding(new Insets(25, 25, 25, 25));
			
			Label lblTitle = new Label("ALHAMBRA\nRegistratie");
			lblTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
			content.add(lblTitle, 0, 0, 2, 1);
			GridPane.setColumnSpan(lblTitle, 2); // Ensures the title spans two columns
			GridPane.setHalignment(lblTitle, javafx.geometry.HPos.CENTER);
			
			
			Label lblUsername = new Label("Gebruikersnaam: ");
			content.add(lblUsername, 0, 1);
			TextField txfUsername = new TextField();
			content.add(txfUsername, 1, 1);
			
			
			Label lblJaar = new Label("Geboortejaar: ");
			content.add(lblJaar, 0, 2);
			TextField txfJaar = new TextField();
			content.add(txfJaar, 1, 2);
			
			
			Label lblMessage = new Label();
			content.add(lblMessage, 2, 3);
			
			
			Button btnTerug = maakButton("\u2190", 0); //Pijltje terug
			btnTerug.setAlignment(Pos.TOP_LEFT);
			btnTerug.setFont(Font.font(40));
			btnTerug.setStyle("-fx-border-width: 0; -fx-background-color: none");
			btnTerug.setOnAction(evt -> {
				content.getScene().setRoot(new StartScherm(dc));
			});
			
			
			Button btnVoegSpelerToe = maakButton("Registreer", 3);
			
			btnVoegSpelerToe.setOnAction(evt -> {
				try {
					dc.registreerSpeler(txfUsername.getText(), Integer.parseInt(txfJaar.getText()));
					lblMessage.setText("Speler is succesvol geregistreerd");
					txfUsername.clear();
					txfJaar.clear();
					
				} catch (Exception ex) {
					lblMessage.setText("Fout: " + ex.getMessage());
				}
			});
			
			this.getChildren().addAll(content);
	
		}
		
		private Button maakButton(String text, int row) {
	        Button button = new Button(text);
	        button.setPrefWidth(125);
	        content.add(button, 0, row);
	        button.setStyle("-fx-background-color: white");
	        RowConstraints rc = new RowConstraints();
	        rc.setValignment(VPos.BOTTOM);
	        content.getRowConstraints().add(rc);
	        return button;
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
