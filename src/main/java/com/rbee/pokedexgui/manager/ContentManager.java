package com.rbee.pokedexgui.manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ContentManager {
    private static ContentManager instance;
    private StackPane contentArea;
    private Map<ModuleType, Node> cachedModules = new HashMap<>();

    public enum ModuleType {
        DASHBOARD("main/Dashboard.fxml"),
        POKEMON("module/pokemon/PokemonView.fxml"),
        MOVES("module/moves/MovesView.fxml"),
        ITEMS("module/items/ItemView.fxml"),
        TRAINERS("module/trainer/TrainerView.fxml");

        private final String fxmlPath;

        ModuleType(String fxmlPath) {
            this.fxmlPath = fxmlPath;
        }

        public String getFxmlPath() {
            return "/com/rbee/pokedexgui/view/" + fxmlPath;
        }
    }


    private ContentManager() {}

    public static ContentManager getInstance() {
        if (instance == null) {
            instance = new ContentManager();
        }
        return instance;
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    public void loadModule(ModuleType moduleType) {
        try {
            Node moduleContent = cachedModules.get(moduleType);

            if (moduleContent == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(moduleType.getFxmlPath()));
                moduleContent = loader.load();
                cachedModules.put(moduleType, moduleContent);
            }

            // Clear current content and add new
            contentArea.getChildren().clear();
            contentArea.getChildren().add(moduleContent);

            // Make content fill the entire AnchorPane
            AnchorPane.setTopAnchor(moduleContent, 0.0);
            AnchorPane.setBottomAnchor(moduleContent, 0.0);
            AnchorPane.setLeftAnchor(moduleContent, 0.0);
            AnchorPane.setRightAnchor(moduleContent, 0.0);

        } catch (IOException e) {
            System.err.println("Failed to load module: " + moduleType);
            e.printStackTrace();
        }
    }
}