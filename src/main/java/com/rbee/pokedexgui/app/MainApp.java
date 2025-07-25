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
    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(getClass().getResourceAsStream("/com/rbee/pokedexgui/fonts/PressStart2P-Regular.ttf"), 12);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/rbee/pokedexgui/view/loading/LoadingScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 540);

        stage.setTitle("Enhanced Pokédex by Rbee :>");
        stage.setScene(scene);

        // Set window icon (top-left icon)
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/rbee/pokedexgui/images/pokemon-logo.jpg"))));

        stage.show();

    }
    public static void main(String[] args) {
        launch();
    }
}