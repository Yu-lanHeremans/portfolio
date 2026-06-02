package main;

import cui.AlhambraApp;
import domein.DomeinController;

public class StartUp {
    public static void main(String[] args) {
       
    	new AlhambraApp(new DomeinController()).start();
        
    }
}