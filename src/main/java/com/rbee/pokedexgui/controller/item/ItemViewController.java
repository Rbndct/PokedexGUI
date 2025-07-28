package com.rbee.pokedexgui.controller.item;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.rbee.pokedexgui.cells.ItemImageNameCell;
import com.rbee.pokedexgui.model.item.Item;
import com.rbee.pokedexgui.manager.ItemManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ItemViewController implements Initializable {

    @FXML private TextField searchTextField;
    @FXML private JFXComboBox<String> typeFilterBox;
    @FXML private JFXButton resetFiltersButton;

    @FXML private TableView<Item> itemTableView;
    @FXML private TableColumn<Item, String> nameColumn;
    @FXML private TableColumn<Item, String> categoryColumn;
    @FXML private TableColumn<Item, String> descriptionColumn;
    @FXML private TableColumn<Item, String> effectColumn;

    private ItemManager itemManager;
    private FilteredList<Item> filteredList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemManager = new ItemManager();
        filteredList = new FilteredList<>(itemManager.getItemList(), p -> true);

        setupItemTable();
        setupFilters();

        itemTableView.setItems(filteredList);
    }

    private void setupItemTable() {
        // Name column: show item name and image using custom cell
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setCellFactory(column -> new ItemImageNameCell());

        // Category column: simple text
        categoryColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory()));

        // Description and Effect columns: simple text with wrapping
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));
        effectColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEffect()));

        // Apply improved wrapping to Description and Effect columns
        wrapColumnText(descriptionColumn);
        wrapColumnText(effectColumn);
    }

    private void wrapColumnText(TableColumn<Item, String> column) {
        column.setCellFactory(tc -> new TableCell<>() {
            private final Label label = new Label();

            {
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);
                setGraphic(label);
                setPrefHeight(Control.USE_COMPUTED_SIZE);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    label.setText(null);
                    setGraphic(null);
                } else {
                    label.setText(item);
                    setGraphic(label);
                    this.setPrefHeight(label.prefHeight(column.getWidth()) + 10);
                }
            }
        });
    }

    private void setupFilters() {
        typeFilterBox.setItems(FXCollections.observableArrayList(
                "All", "Vitamin", "Leveling Item", "Feather", "Evolution Stone"
        ));
        typeFilterBox.getSelectionModel().select("All");

        searchTextField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        typeFilterBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        resetFiltersButton.setOnAction(e -> {
            searchTextField.clear();
            typeFilterBox.getSelectionModel().select("All");
        });
    }

    private void applyFilters() {
        String keyword = searchTextField.getText().toLowerCase().trim();
        String selectedType = typeFilterBox.getValue();

        filteredList.setPredicate(item -> {
            boolean matchesSearch = keyword.isEmpty() ||
                    item.getName().toLowerCase().contains(keyword) ||
                    item.getDescription().toLowerCase().contains(keyword) ||
                    item.getEffect().toLowerCase().contains(keyword);

            boolean matchesType = selectedType == null || selectedType.equals("All") ||
                    item.getCategory().equalsIgnoreCase(selectedType);

            return matchesSearch && matchesType;
        });
    }
}
