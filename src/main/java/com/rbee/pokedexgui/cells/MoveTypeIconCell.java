package com.rbee.pokedexgui.cells;

import com.rbee.pokedexgui.model.move.Move;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class MoveTypeIconCell extends TableCell<Move, String> {

    private final ImageView primaryTypeImageView = createTypeImageView();
    private final ImageView secondaryTypeImageView = createTypeImageView();
    private final Label nameLabel = new Label();
    private final HBox iconBox = new HBox(4);  // spacing between icons
    private final HBox container = new HBox(8);  // spacing between icons and label

    private static final Image PLACEHOLDER_IMAGE = new Image(
            MoveTypeIconCell.class.getResourceAsStream("/com/rbee/pokedexgui/images/multiplepokeballs-logo.png"),
            24, 24, true, true);

    public MoveTypeIconCell() {
        iconBox.setAlignment(Pos.CENTER_LEFT);
        iconBox.getChildren().addAll(primaryTypeImageView, secondaryTypeImageView);

        primaryTypeImageView.setStyle("-fx-fit-width: 24px; -fx-fit-height: 24px;");
        secondaryTypeImageView.setStyle("-fx-fit-width: 24px; -fx-fit-height: 24px;");


        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        container.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().addAll(iconBox, nameLabel);

        setPrefHeight(40);
    }

    private static ImageView createTypeImageView() {
        ImageView iv = new ImageView();
        iv.setFitWidth(24);
        iv.setFitHeight(24);
        iv.setPreserveRatio(true);
        return iv;
    }

    @Override
    protected void updateItem(String moveName, boolean empty) {
        super.updateItem(moveName, empty);

        if (empty || moveName == null || moveName.isBlank()) {
            primaryTypeImageView.setImage(null);
            secondaryTypeImageView.setImage(null);
            nameLabel.setText(null);
            setText(null);
            setGraphic(null);
            return;
        }

        nameLabel.setText(moveName);

        Move move = getTableView().getItems().get(getIndex());

        loadTypeIcon(move.getPrimaryType(), primaryTypeImageView);
        loadTypeIcon(move.getSecondaryType(), secondaryTypeImageView);

        setText(null);
        setGraphic(container);
    }

    private void loadTypeIcon(String type, ImageView imageView) {
        if (type == null || type.isBlank()) {
            imageView.setImage(null);
            return;
        }

        String lowerType = type.toLowerCase();
        String url = "https://raw.githubusercontent.com/msikma/pokesprite/master/misc/types/masters/" + lowerType + ".png";

        Image image = new Image(url, 20, 20, true, true, true);

        image.errorProperty().addListener((obs, wasError, isError) -> {
            if (isError) imageView.setImage(PLACEHOLDER_IMAGE);
        });

        image.progressProperty().addListener((obs, oldProg, newProg) -> {
            if (newProg.doubleValue() >= 1.0 && !image.isError()) {
                imageView.setImage(image);
            }
        });
    }
}
