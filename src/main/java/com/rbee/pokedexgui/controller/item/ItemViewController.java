package com.rbee.pokedexgui.controller.item;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import com.rbee.pokedexgui.cells.ItemImageNameCell;
import com.rbee.pokedexgui.manager.ActiveTrainerHolder;
import com.rbee.pokedexgui.model.item.Item;
import com.rbee.pokedexgui.manager.ItemManager;
import com.rbee.pokedexgui.model.trainer.Trainer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.Optional;
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
    private Trainer activeTrainer;

    private JFXSnackbar snackbar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemManager = ItemManager.getInstance();

        filteredList = new FilteredList<>(itemManager.getItemList(), p -> true);

        // Initialize snackbar with any parent container you have, e.g., itemTableView's parent or other container
        snackbar = new JFXSnackbar((Pane) itemTableView.getParent());

        activeTrainer = ActiveTrainerHolder.getActiveTrainer();

        setupItemTable();
        setupFilters();

        itemTableView.setItems(filteredList);
    }

    private void setupItemTable() {
        setupItemTableColumns();
        setupItemTableContextMenu();
    }

    private void setupItemTableColumns() {
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

        wrapColumnText(descriptionColumn);
        wrapColumnText(effectColumn);
    }

    private void setupItemTableContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem buyItemMenuItem = new MenuItem("Buy Item");
        contextMenu.getItems().add(buyItemMenuItem);

        buyItemMenuItem.setOnAction(event -> handleBuyItem());

        itemTableView.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return row;
        });
    }

    private void handleBuyItem() {
        Item selectedItem = itemTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showSnackbar("No item selected.");
            return;
        }

        Trainer activeTrainer = ActiveTrainerHolder.getActiveTrainer(); // <--- THIS LINE IS CRITICAL
        if (activeTrainer == null) {
            showSnackbar("No active trainer selected.");
            return;
        }

        System.out.println("Buying for trainer: " + activeTrainer.getName() + " (hashcode: " + activeTrainer.hashCode() + ")");

        TextInputDialog quantityDialog = new TextInputDialog("1");
        quantityDialog.setTitle("Buy Item");
        quantityDialog.setHeaderText("Buying " + selectedItem.getName());
        quantityDialog.setContentText("Enter quantity:");

        Optional<String> result = quantityDialog.showAndWait();
        if (result.isPresent()) {
            try {
                int quantity = Integer.parseInt(result.get());
                if (quantity <= 0) {
                    showSnackbar("Quantity must be positive.");
                    return;
                }

                boolean success = activeTrainer.buyItem(selectedItem, quantity);
                if (success) {
                    showSnackbar("Bought " + quantity + " × " + selectedItem.getName());
                } else {
                    showSnackbar("Not enough money to buy.");
                }
            } catch (NumberFormatException e) {
                showSnackbar("Invalid quantity entered.");
            }
        }
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

    private void showSnackbar(String message) {
        JFXSnackbarLayout layout = new JFXSnackbarLayout(message);
        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(layout, javafx.util.Duration.seconds(3)));
    }
}
