package com.rbee.pokedexgui.controller.main;

import com.jfoenix.controls.JFXButton;
import com.rbee.pokedexgui.manager.ContentManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.application.HostServices;


import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    private HostServices hostServices;


    @FXML private JFXButton pokemonCard;
    @FXML private JFXButton movesCard;
    @FXML private JFXButton itemsCard;
    @FXML private JFXButton trainersCard;

    private ContentManager contentManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contentManager = ContentManager.getInstance();

        // Set up card actions
        pokemonCard.setOnAction(e -> contentManager.loadModule(ContentManager.ModuleType.POKEMON));
        movesCard.setOnAction(e -> contentManager.loadModule(ContentManager.ModuleType.MOVES));
        itemsCard.setOnAction(e -> contentManager.loadModule(ContentManager.ModuleType.ITEMS));
        trainersCard.setOnAction(e -> contentManager.loadModule(ContentManager.ModuleType.TRAINERS));
    }

    public void openGitHub() {
        if (hostServices != null) {
            hostServices.showDocument("https://github.com/rbee/pokedex");
        }
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }


}