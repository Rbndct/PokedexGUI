package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.controller.main.DashboardController;
import com.rbee.pokedexgui.manager.ActiveTrainerHolder;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Content manager.
 */
public class ContentManager {
    private static ContentManager instance;

    private StackPane contentArea;
    private HostServices hostServices;

    private final Map<ModuleType, Node> cachedModules = new HashMap<>();
    private final Map<ModuleType, Object> cachedControllers = new HashMap<>();

    /**
     * The enum Module type.
     */
    public enum ModuleType {
        /**
         * Dashboard module type.
         */
        DASHBOARD("main/Dashboard.fxml"),
        /**
         * Pokemon module type.
         */
        POKEMON("module/pokemon/PokemonView.fxml"),
        /**
         * Moves module type.
         */
        MOVES("module/moves/MovesView.fxml"),
        /**
         * Items module type.
         */
        ITEMS("module/items/ItemView.fxml"),
        /**
         * Trainers module type.
         */
        TRAINERS("module/trainer/TrainerView.fxml");

        private final String fxmlPath;

        ModuleType(String fxmlPath) {
            this.fxmlPath = fxmlPath;
        }

        /**
         * Gets fxml path.
         *
         * @return the fxml path
         */
        public String getFxmlPath() {
            return "/com/rbee/pokedexgui/view/" + fxmlPath;
        }
    }

    private ContentManager() {}

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ContentManager getInstance() {
        if (instance == null) {
            instance = new ContentManager();
        }
        return instance;
    }

    /**
     * Sets content area.
     *
     * @param contentArea the content area
     */
    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    /**
     * Sets host services.
     *
     * @param hostServices the host services
     */
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    /**
     * Load module.
     *
     * @param moduleType the module type
     */
    public void loadModule(ModuleType moduleType) {
        try {
            Node moduleContent = cachedModules.get(moduleType);
            Object controller;

            if (moduleContent == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(moduleType.getFxmlPath()));
                moduleContent = loader.load();
                controller = loader.getController();

                cachedModules.put(moduleType, moduleContent);
                cachedControllers.put(moduleType, controller);

                // Inject HostServices if Dashboard
                if (moduleType == ModuleType.DASHBOARD && controller instanceof DashboardController) {
                    ((DashboardController) controller).setHostServices(hostServices);
                }
            } else {
                controller = cachedControllers.get(moduleType);
            }

            // Instead of injecting active trainer here,
            // controllers should get active trainer themselves from ActiveTrainerHolder if needed

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(moduleContent);

                AnchorPane.setTopAnchor(moduleContent, 0.0);
                AnchorPane.setBottomAnchor(moduleContent, 0.0);
                AnchorPane.setLeftAnchor(moduleContent, 0.0);
                AnchorPane.setRightAnchor(moduleContent, 0.0);
            } else {
                System.err.println("Content area is not set in ContentManager.");
            }

        } catch (IOException e) {
            System.err.println("Failed to load module: " + moduleType);
            e.printStackTrace();
        }
    }

    /**
     * Preload all modules.
     *
     * @throws IOException the io exception
     */
    public void preloadAllModules() throws IOException {
        for (ModuleType moduleType : ModuleType.values()) {
            if (!cachedModules.containsKey(moduleType)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(moduleType.getFxmlPath()));
                Node moduleContent = loader.load();
                Object controller = loader.getController();

                if (moduleType == ModuleType.DASHBOARD && controller instanceof DashboardController) {
                    ((DashboardController) controller).setHostServices(hostServices);
                }

                cachedModules.put(moduleType, moduleContent);
                cachedControllers.put(moduleType, controller);
            }
        }
    }
}
