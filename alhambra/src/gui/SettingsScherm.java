package gui;

import java.io.File;
import java.net.URL;

import domein.DomeinController;
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

public class SettingsScherm extends VBox {
		private final DomeinController dc;
		private boolean muziek = true;
		Button btnMuziek = new Button();
		private static MediaPlayer mediaPlayer;
		
		public SettingsScherm(DomeinController dc) {
			this.dc = dc;
			buildGui();
			speelMuziek();
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
	        
			
			Label lblTitle = new Label("Instellingen");
			lblTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
			content.add(lblTitle, 1, 0);
			lblTitle.setAlignment(Pos.CENTER);
			
			
			Button btnTerug = new Button("\u2190"); //Pijltje terug
			content.add(btnTerug, 0, 0);
			btnTerug.setAlignment(Pos.TOP_LEFT);
			btnTerug.setFont(Font.font("System", 40));
			btnTerug.setStyle("-fx-border-width: 0; -fx-background-color: none");
			btnTerug.setOnAction(evt -> {
				this.getScene().setRoot(new StartScherm(dc));
			});
			
			Label lblMuziek = new Label("Muziek: ");
			content.add(lblMuziek, 4, 2);
			btnMuziek = new Button("\uD83D\uDD0A");
			btnMuziek.setFont(Font.font(30));
		    btnMuziek.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
			content.add(btnMuziek, 5, 2);
			
			
			
			Tooltip.install(btnMuziek, new Tooltip("Zet muziek aan/uit"));
		    Slider volumeSlider = new Slider(0, 1, 0.5);
		    volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
		        mediaPlayer.setVolume(newVal.doubleValue());
		    });
		    content.add(volumeSlider, 6, 2);
		    
		    this.getChildren().addAll(content);
		}
		
		
		public void speelMuziek() {

		    if (mediaPlayer == null) {
		        URL resource = getClass().getResource("/resources/audio/ACO_Music.mp3");
		        if (resource == null) {
		            throw new RuntimeException("Audio niet gevonden!");
		        }
		        Media media = new Media(resource.toExternalForm());
		        mediaPlayer = new MediaPlayer(media);
		        mediaPlayer.play();
		        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		    }

		    btnMuziek.setOnMouseClicked(evt -> {
		        if (muziek) {
		            btnMuziek.setText("\uD83D\uDD07"); 
		            muziek = false;
		            mediaPlayer.pause();
		        }
		        else {
		            btnMuziek.setText("\uD83D\uDD0A");
		            muziek = true;
		            mediaPlayer.play();
		        }
		    });	
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
