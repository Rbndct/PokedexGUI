package com.rbee.pokedexgui.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        Font.loadFont(getClass().getResourceAsStream("/com/rbee/pokedexgui/fonts/PressStart2P-Regular.ttf"), 12);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/rbee/pokedexgui/view/loading/LoadingScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 540);

        primaryStage.setTitle("Enhanced Pokédex by Rbee :>");
        primaryStage.setScene(scene);

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/rbee/pokedexgui/images/pokemon-logo.jpg"))));

        primaryStage.show();

        com.rbee.pokedexgui.manager.ContentManager.getInstance().setHostServices(getHostServices());
    }

    // Method to update title dynamically
    public static void setActiveTrainerTitle(String trainerName) {
        if (primaryStage != null) {
            primaryStage.setTitle("Enhanced Pokédex by Rbee :>  |  Active Trainer: " + trainerName);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
