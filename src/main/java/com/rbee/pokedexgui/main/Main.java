package com.rbee.pokedexgui.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/rbee/pokedexgui/fxml/loading_screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 540);
        stage.setTitle("Enhanced Pokédex GUI");
        stage.setScene(scene);

        // Set window icon (top-left icon)
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/rbee/pokedexgui/images/pokemon-logo.jpg"))));

        stage.show();

    }
    public static void main(String[] args) {
        launch();
    }
}