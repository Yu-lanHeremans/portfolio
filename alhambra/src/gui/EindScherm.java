package gui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import domein.DomeinController;
import dto.SpelerDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import java.io.File;

public class EindScherm extends VBox {
		private final DomeinController dc;		
		public EindScherm(DomeinController dc) {
			this.dc = dc;
			buildGui();
		}
		
		private void buildGui() {
			
        	dc.eindigSpel();

			
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
	        this.setAlignment(Pos.CENTER);
	        this.setBackground(new Background(achtergrond));
	        this.setPadding(new Insets(25, 25, 25, 25));
	        content.setHgap(10);
	        content.setVgap(10);
	        content.setPadding(new Insets(25, 25, 25, 25));
	        content.setMaxWidth(Region.USE_PREF_SIZE);
			content.setPrefWidth(Region.USE_COMPUTED_SIZE);
			content.setStyle(
		    	    "-fx-background-color: rgba(255, 255, 255, 0.4);"+ 
		    	    "-fx-border-color: white;"   +
		    	    "-fx-border-width: 2px;"      +                   
		    	    "-fx-border-radius: 5px;"      +                 
		    	    "-fx-padding: 20px;"+
		    	    "-fx-margin: 20px;"
		    	);
	        
			
			Label lblTitle = new Label("Einde!\n");
			lblTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
			content.add(lblTitle, 0, 0);
			lblTitle.setAlignment(Pos.CENTER);
			
			
			Label lblScorebord = new Label(dc.eindigSpel());
			
			lblScorebord.setStyle("-fx-text-fill: white;");
			lblScorebord.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
			
			content.add(lblScorebord, 0, 1);
			
		    this.getChildren().addAll(content);
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
