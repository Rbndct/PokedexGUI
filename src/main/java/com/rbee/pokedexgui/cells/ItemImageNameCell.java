package com.rbee.pokedexgui.cells;

import com.rbee.pokedexgui.model.item.Item;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * The type Item image name cell.
 */
public class ItemImageNameCell extends TableCell<Item, String> {

    private final ImageView imageView = new ImageView();
    private final Label nameLabel = new Label();
    private final HBox container = new HBox(8);  // spacing between image and label

    private static final Image PLACEHOLDER_IMAGE = new Image(
            SpriteImageCell.class.getResourceAsStream("/com/rbee/pokedexgui/images/multiplepokeballs-logo.png"),
            40, 40, true, true);

    /**
     * Instantiates a new Item image name cell.
     */
    public ItemImageNameCell() {
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        imageView.setPreserveRatio(true);

        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        container.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().addAll(imageView, nameLabel);

        setPrefHeight(50);
    }

    @Override
    protected void updateItem(String itemName, boolean empty) {
        super.updateItem(itemName, empty);

        if (empty || itemName == null || itemName.isBlank()) {
            imageView.setImage(null);
            nameLabel.setText(null);
            setText(null);
            setGraphic(null);
        } else {
            nameLabel.setText(itemName);
            imageView.setImage(PLACEHOLDER_IMAGE);

            // ✅ Replace "feather" with "wing" for correct API path
            String formattedName = itemName.toLowerCase()
                    .replace(" ", "-")
                    .replace("feather", "wing");

            String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/" + formattedName + ".png";

            Image itemImage = new Image(imageUrl, 40, 40, true, true, true);

            itemImage.errorProperty().addListener((obs, wasError, isError) -> {
                if (isError) {
                    imageView.setImage(PLACEHOLDER_IMAGE);
                }
            });

            itemImage.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                if (newProgress.doubleValue() >= 1.0 && !itemImage.isError()) {
                    imageView.setImage(itemImage);
                }
            });

            setText(null);
            setGraphic(container);
        }
    }

}
