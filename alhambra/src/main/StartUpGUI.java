package main;

import domein.DomeinController;
import gui.SettingsScherm;
import gui.SpelScherm;
import gui.StartScherm;
import gui.StartSpelScherm;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartUpGUI extends Application {


	@Override
	public void start(Stage primaryStage) {
		DomeinController dc = new DomeinController();
		//StartScherm root = new StartScherm(dc);
		StartScherm root = new StartScherm(dc);
		
		Scene scene = new Scene(root);
		primaryStage.setTitle("Alhambra");
		primaryStage.setScene(scene);
		primaryStage.setWidth(800);
		primaryStage.setHeight(480);
		primaryStage.setResizable(true);
		primaryStage.setOnCloseRequest(root::quit);
		primaryStage.show();
		
		

	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
