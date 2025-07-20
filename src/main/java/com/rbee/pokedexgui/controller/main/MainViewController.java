package com.rbee.pokedexgui.controller.main;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainViewController extends Application {
    @FXML
    private AnchorPane contentArea;
    @FXML
    private JFXButton btnPokemon, btnMoves, btnItems, btnTrainer;


    @FXML
    private VBox sidebarVBox;

    public void initialize() {
        sidebarVBox.setClip(null); // Allow overflow
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }
}
