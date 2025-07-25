package com.rbee.pokedexgui.cells;

import com.rbee.pokedexgui.model.pokemon.Pokemon;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class SpriteImageCell extends TableCell<Pokemon, Integer> {
    private final ImageView imageView = new ImageView();
    private final Label idLabel = new Label();
    private final HBox container = new HBox(8);  // 8 px spacing between image and label

    private static final Image PLACEHOLDER_IMAGE = new Image(
            SpriteImageCell.class.getResourceAsStream("/com/rbee/pokedexgui/images/multiplepokeballs-logo.png"),
            40, 40, true, true);

    public SpriteImageCell() {
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        imageView.setPreserveRatio(true);

        idLabel.setTextFill(Color.WHITE);
        idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        container.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().addAll(imageView, idLabel);

        setPrefHeight(50);
    }

    @Override
    protected void updateItem(Integer pokeId, boolean empty) {
        super.updateItem(pokeId, empty);

        if (empty || pokeId == null) {
            imageView.setImage(null);
            idLabel.setText(null);
            setText(null);
            setGraphic(null);
        } else {
            idLabel.setText(String.format("#%03d", pokeId));
            imageView.setImage(PLACEHOLDER_IMAGE); // Show placeholder first

            String spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokeId + ".png";
            Image sprite = new Image(spriteUrl, 40, 40, true, true, true);

            sprite.errorProperty().addListener((obs, wasError, isError) -> {
                if (isError) {
                    imageView.setImage(PLACEHOLDER_IMAGE);
                }
            });

            sprite.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                if (newProgress.doubleValue() >= 1.0 && !sprite.isError()) {
                    imageView.setImage(sprite);
                }
            });

            setText(null);
            setGraphic(container);
        }
    }
}
