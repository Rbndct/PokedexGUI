package com.rbee.pokedexgui.controller.main;

import com.jfoenix.controls.JFXButton;
import com.rbee.pokedexgui.manager.ContentManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;


import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private JFXButton btnDashboard;
    @FXML private JFXButton btnPokemon;
    @FXML private JFXButton btnMoves;
    @FXML private JFXButton btnItems;
    @FXML private JFXButton btnTrainer;

    private ContentManager contentManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contentManager = ContentManager.getInstance();
        contentManager.setContentArea(contentArea);

        // Set up button actions
        btnDashboard.setOnAction(e -> loadDashboard());
        btnPokemon.setOnAction(e -> loadPokemonModule());
        btnMoves.setOnAction(e -> loadMovesModule());
        btnItems.setOnAction(e -> loadItemsModule());
        btnTrainer.setOnAction(e -> loadTrainersModule());

        // Load dashboard by default
        loadDashboard();
    }

    private void loadDashboard() {
        contentManager.loadModule(ContentManager.ModuleType.DASHBOARD);
        updateActiveButton(null);
    }

    private void loadPokemonModule() {
        contentManager.loadModule(ContentManager.ModuleType.POKEMON);
        updateActiveButton(btnPokemon);
    }

    private void loadMovesModule() {
        contentManager.loadModule(ContentManager.ModuleType.MOVES);
        updateActiveButton(btnMoves);
    }

    private void loadItemsModule() {
        contentManager.loadModule(ContentManager.ModuleType.ITEMS);
        updateActiveButton(btnItems);
    }

    private void loadTrainersModule() {
        contentManager.loadModule(ContentManager.ModuleType.TRAINERS);
        updateActiveButton(btnTrainer);
    }

    private void updateActiveButton(JFXButton activeButton) {
        // Remove active class from all buttons
        btnPokemon.getStyleClass().remove("nav-card-active");
        btnMoves.getStyleClass().remove("nav-card-active");
        btnItems.getStyleClass().remove("nav-card-active");
        btnTrainer.getStyleClass().remove("nav-card-active");

        // Add active class to selected button
        if (activeButton != null) {
            activeButton.getStyleClass().add("nav-card-active");
        }
    }
}