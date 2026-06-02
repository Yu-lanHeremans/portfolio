module alhambra.g53_25 {
	exports persistentie;
	exports cui;
	exports utils;
	exports gui;
	exports main;
	exports domein;
	exports testen;
	exports dto;
	exports exceptions;

	requires java.sql;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires org.junit.jupiter.api;
	requires java.desktop;
	requires javafx.media;
	
	opens main to javafx.graphics;
}