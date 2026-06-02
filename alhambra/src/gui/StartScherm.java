package gui;

import java.util.Optional;
import domein.DomeinController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
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
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class StartScherm extends VBox {
    private final DomeinController dc;
    private GridPane buttonGrid;
    private GridPane content;
    
    
    public StartScherm(DomeinController dc) {
        this.dc = dc;
        SettingsScherm settingsScherm = new SettingsScherm(dc);
		settingsScherm.speelMuziek();
        applySmoothZoomInEffect(this);
        buildGui();
    }

    
    private void buildGui() {
    	
    	content = new GridPane();
    	
        this.setPadding(new Insets(25));

        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage achtergrond = new BackgroundImage(
            new Image(getClass().getResource("/resources/images/AlhambraStartScreenHor.png").toExternalForm()), 
            BackgroundRepeat.NO_REPEAT, 
            BackgroundRepeat.NO_REPEAT, 
            BackgroundPosition.CENTER, 
            backgroundSize
        );
        this.setBackground(new Background(achtergrond));
        this.setAlignment(Pos.BOTTOM_CENTER);
        
        content.setMaxWidth(Region.USE_PREF_SIZE);
		content.setPrefWidth(Region.USE_COMPUTED_SIZE);
		content.setStyle(
	    	    "-fx-background-color: rgba(128, 128, 128, 0.6); "+ 
	    	    "-fx-border-color: white;"   +
	    	    "-fx-border-width: 2px;"      +                   
	    	    "-fx-border-radius: 5px;"      +                 
	    	    "-fx-padding: 20px;"+
	    	    "-fx-margin: 20px;"
	    	);
		
		setSchaduw(content);
        
        buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setVgap(15);
        buttonGrid.setHgap(10);

        RowConstraints spacer = new RowConstraints();
        spacer.setVgrow(Priority.ALWAYS);
        content.getRowConstraints().add(spacer);

        
        Button btnRegistreer = maakButton("Registreer Speler", 1);
        btnRegistreer.setOnAction(evt -> veranderScherm(new RegistratieScherm(dc)));
        
        Button btnStart = maakButton("Start spel", 2);
        btnStart.setOnAction(evt -> veranderScherm(new StartSpelScherm(dc)));

        Button btnSluitAf = maakButton("Afsluiten", 3);
        btnSluitAf.setOnAction(this::quit);
        
        
        Button btnSettings = maakButton("\u2699", 4);
        btnSettings.setFont(Font.font("System", 50));

        btnSettings.setOnAction(evt -> veranderScherm(new SettingsScherm(dc)));
        btnSettings.setStyle("-fx-border-width: 0; -fx-background-color: transparent; -fx-text-fill: white;");
        
        
        
        this.setAlignment(Pos.CENTER);
        content.setHgap(10);
        content.setVgap(20);
        
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
    
    
	private void veranderScherm(Pane newScreen) {
        Stage stage = (Stage) this.getScene().getWindow();
		stage.setScene(new Scene(newScreen));
    }
    
    
    public void quit(Event event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bevestig");
        alert.setContentText("Wil je de applicatie afsluiten?");
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        } else {
            event.consume();
        }
    }
    
    
    public void applySmoothZoomInEffect(StartScherm startScherm) {
        startScherm.setScaleX(1.3);
        startScherm.setScaleY(1.3);
        startScherm.setEffect(new GaussianBlur(15));

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1.5), startScherm);
        scaleTransition.setFromX(1.3);
        scaleTransition.setFromY(1.3);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

        GaussianBlur blur = new GaussianBlur();
        startScherm.setEffect(blur);
        Timeline blurTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(blur.radiusProperty(), 15)),
            new KeyFrame(Duration.seconds(1.2), new KeyValue(blur.radiusProperty(), 0))
        );

        ParallelTransition parallelTransition = new ParallelTransition(
            scaleTransition,
            blurTimeline
        );
        parallelTransition.play();
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












