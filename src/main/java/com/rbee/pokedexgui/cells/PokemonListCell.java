package com.rbee.pokedexgui.cells;

import com.rbee.pokedexgui.model.pokemon.Pokemon;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class PokemonListCell extends ListCell<Pokemon> {
    private final ImageView imageView = new ImageView();
    private final Label nameLabel = new Label();
    private final HBox container = new HBox(8); // spacing between image and label

    private static final Image PLACEHOLDER_IMAGE = new Image(
            PokemonListCell.class.getResourceAsStream("/com/rbee/pokedexgui/images/multiplepokeballs-logo.png"),
            40, 40, true, true);

    public PokemonListCell() {
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        imageView.setPreserveRatio(true);

        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        container.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().addAll(imageView, nameLabel);
    }

    @Override
    protected void updateItem(Pokemon pokemon, boolean empty) {
        super.updateItem(pokemon, empty);

        if (empty || pokemon == null) {
            setText(null);
            setGraphic(null);
        } else {
            nameLabel.setText(pokemon.getName());
            imageView.setImage(PLACEHOLDER_IMAGE);

            String spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"
                    + pokemon.getPokedexNumber() + ".png";

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

            setGraphic(container);
            setText(null);
        }
    }
}
