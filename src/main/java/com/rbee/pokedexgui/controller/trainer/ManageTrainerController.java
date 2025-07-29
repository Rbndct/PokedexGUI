package com.rbee.pokedexgui.controller.trainer;

import com.rbee.pokedexgui.cells.ItemImageNameCell;
import com.rbee.pokedexgui.cells.PokemonListCell;
import com.rbee.pokedexgui.cells.SpriteImageCell;
import com.rbee.pokedexgui.manager.MoveManager;
import com.rbee.pokedexgui.manager.TrainerManager;
import com.rbee.pokedexgui.model.item.Item;
import com.rbee.pokedexgui.model.move.Move;
import com.rbee.pokedexgui.model.pokemon.Pokemon;
import com.rbee.pokedexgui.model.trainer.Trainer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ManageTrainerController implements Initializable {

    @FXML private TableView < Pokemon > storagePokemonTable;
    @FXML private TableColumn < Pokemon, Integer > idColumn;
    @FXML private TableColumn < Pokemon, String > nameColumn;
    @FXML private TableColumn < Pokemon, List < String >> typeColumn;
    @FXML private TableColumn < Pokemon, Integer > levelColumn;
    @FXML private TableColumn < Pokemon, Integer > hpColumn;
    @FXML private TableColumn < Pokemon, Integer > atkColumn;
    @FXML private TableColumn < Pokemon, Integer > defColumn;
    @FXML private TableColumn < Pokemon, Integer > spaColumn;
    @FXML private TableColumn < Pokemon, Integer > spdColumn;
    @FXML private TableColumn < Pokemon, Integer > speColumn;
    @FXML private TableColumn < Pokemon, Integer > totalColumn;

    @FXML private TableView < Pokemon > activePokemonTable;
    @FXML private TableColumn < Pokemon, Integer > colActivePokedexNo;
    @FXML private TableColumn < Pokemon, String > colActiveName;
    @FXML private TableColumn < Pokemon, List < String >> colActiveType;
    @FXML private TableColumn < Pokemon, Integer > colActiveLevel;
    @FXML private TableColumn < Pokemon, Integer > colActiveHp;
    @FXML private TableColumn < Pokemon, Integer > colActiveAtk;
    @FXML private TableColumn < Pokemon, Integer > colActiveDef;
    @FXML private TableColumn < Pokemon, Integer > colActiveSpAtk;
    @FXML private TableColumn < Pokemon, Integer > colActiveSpDef;
    @FXML private TableColumn < Pokemon, Integer > colActiveSpeed;
    @FXML private TableColumn < Pokemon, Integer > colActiveTotal;

    @FXML private TableView < Item > trainerItemTable;

    @FXML private TableColumn < Item, String > colItemName;

    @FXML private TableColumn < Item, String > colItemCategory;

    @FXML private TableColumn < Item, String > colItemEffect;

    @FXML private TableColumn < Item, Integer > colItemQuantity;

    @FXML
    private Label lblRemainingMoney;

    @FXML
    private Label trainerMoney;

    @FXML
    private Label pokemonMoveCountLabel;

    @FXML
    private Label lblActiveCount;
    @FXML
    private Label lblStorageCount;

    @FXML private ComboBox < Pokemon > pokemonComboBox;

    @FXML
    private Label pokemonTypeLabel;

    @FXML
    private ImageView pokemonImageView;

    @FXML private TableView < Move > currentMovesTable;
    @FXML
    private TableColumn < Move, String > moveNameCol;
    @FXML
    private TableColumn < Move, String > moveTypeCol;
    @FXML private TableColumn < Move, String > moveCategoryCol;
    @FXML private TableView < Move > compatibleMovesTable;
    @FXML private TableColumn < Move, String > compMoveNameCol;
    @FXML private TableColumn < Move, String > compMoveTypeCol;
    @FXML private TableColumn < Move, String > compMoveClassCol;

    private Trainer trainer;
    private TrainerManager trainerManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup storage Pokémon table
        setupStoragePokemonTableColumns();
        setupTotalColumnBoldStyle();
        setupNameColumnWithTooltip(nameColumn);

        storagePokemonTable.setFixedCellSize(60);
        storagePokemonTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Set image cell factory for storage ID column (if not already in setupStoragePokemonTableColumns)
        idColumn.setCellFactory(col -> new SpriteImageCell());

        // Setup item table and context menu
        setupItemTableColumns();
        setupTrainerItemTableContextMenu();

        // Setup active Pokémon table
        setupActivePokemonTableColumns();
        setupActiveTotalColumnBoldStyle();
        setupNameColumnWithTooltip(colActiveName);

        activePokemonTable.setFixedCellSize(60);
        activePokemonTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Set image cell factory for active Pokémon ID column
        colActivePokedexNo.setCellFactory(col -> new SpriteImageCell());

        setupActivePokemonTableContextMenu();
        setupStoragePokemonTableContextMenu();
        updatePokemonCounts();

        setupPokemonComboBox();
        setupMoveTables();
    }

    // Called by the loader to pass the Trainer to manage
    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;

        if (trainer != null) {
            if (trainer.getStorage() != null) {
                storagePokemonTable.setItems(trainer.getStorage());
            } else {
                storagePokemonTable.getItems().clear();
            }

            if (trainer.getLineup() != null) {
                activePokemonTable.setItems(trainer.getLineup());
            } else {
                activePokemonTable.getItems().clear();
            }

            if (trainer.getItemList() != null) {
                trainerItemTable.setItems(trainer.getItemList());
            } else {
                trainerItemTable.getItems().clear();
            }

            // Remove old listeners before adding new ones (to avoid duplicates)
            trainer.getLineup().removeListener(lineupChangeListener);
            trainer.getStorage().removeListener(storageChangeListener);

            // Add listeners to update counts when lineup or storage change
            trainer.getLineup().addListener(lineupChangeListener);
            trainer.getStorage().addListener(storageChangeListener);

            updateMoneyLabels();
            updatePokemonCounts();

            // Update Pokémon ComboBox items and select first Pokémon
            updatePokemonComboBoxItems();

            if (!pokemonComboBox.getItems().isEmpty()) {
                pokemonComboBox.getSelectionModel().selectFirst();
            }

            // --- Refresh move tables for selected Pokémon ---
            refreshMoveTables();
        }
    }

    // Define these listeners as fields in your controller so you can reuse and remove them safely
    private final ListChangeListener < Pokemon > lineupChangeListener = c -> {
        updatePokemonCounts();
        updatePokemonComboBoxItems();
    };

    private final ListChangeListener < Pokemon > storageChangeListener = c -> {
        updatePokemonCounts();
        updatePokemonComboBoxItems();
    };

    // Inject the TrainerManager so you can manipulate trainers (save, update, etc.)
    public void setTrainerManager(TrainerManager trainerManager) {
        this.trainerManager = trainerManager;
    }

    private void setupStoragePokemonTableColumns() {
        // ID column
        idColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokedexNumber()).asObject());

        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        typeColumn.setCellValueFactory(cellData -> getCombinedTypes(cellData.getValue()));
        setupTypeColumnCellFactory(typeColumn);

        levelColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBaseLevel()).asObject());

        // Stats columns
        hpColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getHp()).asObject());
        atkColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getAttack()).asObject());
        defColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getDefense()).asObject());
        spaColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getSpAttack()).asObject());
        spdColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getSpDefense()).asObject());
        speColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getSpeed()).asObject());

        // Total column
        totalColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getTotalBaseStats()).asObject());
    }

    private void setupActivePokemonTableColumns() {
        // ID / Pokedex Number column
        colActivePokedexNo.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokedexNumber()).asObject());

        // Name column
        colActiveName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        // Type(s) column
        colActiveType.setCellValueFactory(cellData ->
                getCombinedTypes(cellData.getValue()));
        setupTypeColumnCellFactory(colActiveType);

        // Level column
        colActiveLevel.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getBaseLevel()).asObject());

        // Stats columns
        colActiveHp.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getHp()).asObject());
        colActiveAtk.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getAttack()).asObject());
        colActiveDef.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getDefense()).asObject());
        colActiveSpAtk.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getSpAttack()).asObject());
        colActiveSpDef.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getSpDefense()).asObject());
        colActiveSpeed.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getSpeed()).asObject());

        // Total column
        colActiveTotal.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokemonStats().getTotalBaseStats()).asObject());
    }

    private void setupTotalColumnBoldStyle() {
        totalColumn.getStyleClass().add("total-column"); // keep for CSS targeting, optional if forced style used

        totalColumn.setCellFactory(column -> new TableCell < Pokemon, Integer > () {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(""); // reset style
                } else {
                    setText(String.valueOf(item));
                    setStyle("-fx-font-weight: bold; -fx-alignment: CENTER-RIGHT;"); // Bold and right aligned
                }
            }
        });
    }

    private void setupActiveTotalColumnBoldStyle() {
        colActiveTotal.getStyleClass().add("total-column"); // optional for CSS targeting

        colActiveTotal.setCellFactory(column -> new TableCell < Pokemon, Integer > () {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(""); // reset style
                } else {
                    setText(String.valueOf(item));
                    setStyle("-fx-font-weight: bold; -fx-alignment: CENTER-RIGHT;"); // bold and right-aligned
                }
            }
        });
    }

    private void setupNameColumnWithTooltip(TableColumn < Pokemon, String > nameColumn) {
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName())
        );

        nameColumn.setCellFactory(new Callback < > () {
            @Override
            public TableCell < Pokemon, String > call(TableColumn < Pokemon, String > col) {
                return new TableCell < Pokemon, String > () {
                    private final Label label = new Label();

                    {
                        label.setWrapText(false); // no wrapping to keep row height consistent
                        setGraphic(label);
                    }

                    @Override
                    protected void updateItem(String name, boolean empty) {
                        super.updateItem(name, empty);
                        if (empty || name == null) {
                            label.setText(null);
                            setTooltip(null);
                        } else {
                            label.setText(name);
                            setTooltip(new Tooltip(name)); // tooltip shows full name on hover
                        }
                    }
                };
            }
        });
    }

    public void setupTypeColumnCellFactory(TableColumn < Pokemon, List < String >> typeColumn) {
        typeColumn.setCellFactory(col -> new TableCell < > () {
            private final VBox badgeContainer = new VBox(4); // 4 px spacing

            {
                badgeContainer.setPrefHeight(40); // match your row height
                badgeContainer.setMaxHeight(40);
                badgeContainer.setMinHeight(40);
                badgeContainer.setFillWidth(true);
                badgeContainer.setAlignment(Pos.CENTER); // always center badges vertically and horizontally
            }

            @Override
            protected void updateItem(List < String > types, boolean empty) {
                super.updateItem(types, empty);
                if (empty || types == null || types.isEmpty()) {
                    setGraphic(null);
                } else {
                    badgeContainer.getChildren().clear();

                    for (String type: types) {
                        Label badge = new Label(type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());
                        badge.getStyleClass().setAll("type-badge", "type-" + type.toLowerCase());

                        badge.setPrefHeight(18);
                        badge.setMinHeight(18);
                        badge.setMaxHeight(18);
                        badge.setMaxWidth(Double.MAX_VALUE);
                        badge.setAlignment(Pos.CENTER);

                        badgeContainer.getChildren().add(badge);
                    }

                    setGraphic(badgeContainer);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setAlignment(Pos.CENTER); // center cell content vertically and horizontally
                }
            }
        });
    }

    private ObservableValue < List < String >> getCombinedTypes(Pokemon p) {
        ObservableList < String > types = FXCollections.observableArrayList();
        if (p.getPrimaryType() != null && !p.getPrimaryType().isEmpty()) {
            types.add(p.getPrimaryType());
        }
        if (p.getSecondaryType() != null && !p.getSecondaryType().isEmpty()) {
            types.add(p.getSecondaryType());
        }
        return new SimpleObjectProperty < > (types);
    }

    private void setupItemTableColumns() {
        // Name column: show item name and image using custom cell
        colItemName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        colItemName.setCellFactory(column -> new ItemImageNameCell());

        // Category column: simple text
        colItemCategory.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory()));

        // Effect column: simple text with wrapping
        colItemEffect.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEffect()));
        wrapColumnText(colItemEffect);

        colItemQuantity.setCellValueFactory(cellData -> {
            Item item = cellData.getValue();
            int qty = trainer != null ? trainer.getItemQuantity(item) : 0;
            return new SimpleIntegerProperty(qty).asObject();
        });
    }

    private void wrapColumnText(TableColumn < Item, String > column) {
        column.setCellFactory(tc -> new TableCell < > () {
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

    private void setupTrainerItemTableContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        // Sell Item menu
        MenuItem sellItemMenu = new MenuItem("Sell Item");
        sellItemMenu.setOnAction(event -> {
            Item selectedItem = trainerItemTable.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                showAlert(Alert.AlertType.WARNING, "No item selected", "Please select an item to sell.");
                return;
            }

            int quantityToSell = 1; // You can make this dynamic if you want

            boolean sold = trainer.sellItem(selectedItem, quantityToSell);

            if (sold) {
                double sellPrice = selectedItem.getSellingPrice() * 0.5 * quantityToSell;
                trainer.setMoney(trainer.getMoney() + sellPrice);

                // Refresh the TableView's item list in case the item was removed
                trainerItemTable.setItems(FXCollections.observableArrayList(trainer.getItemList()));

                updateMoneyLabels();

                showAlert(Alert.AlertType.INFORMATION, "Item Sold",
                        "Sold 1 " + selectedItem.getName() + " for ₽" + String.format("%.2f", sellPrice));
            } else {
                showAlert(Alert.AlertType.ERROR, "Sale Failed", "Not enough quantity to sell.");
            }
        });

        // Use Item menu
        MenuItem useItemMenu = new MenuItem("Use Item");
        useItemMenu.setOnAction(event -> {
            Item selectedItem = trainerItemTable.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                showAlert(Alert.AlertType.WARNING, "No item selected", "Please select an item to use.");
                return;
            }
            showUseItemDialog(selectedItem);
        });

        contextMenu.getItems().addAll(useItemMenu, sellItemMenu);

        trainerItemTable.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return row;
        });
    }

    // Your dialog for selecting which Pokémon to use the item on
    private void showUseItemDialog(Item item) {
        // Create a dialog
        Dialog<Pokemon> dialog = new Dialog<>();
        dialog.setTitle("Use Item");
        dialog.setHeaderText("Use " + item.getName());

        // Set the button types
        ButtonType useButtonType = new ButtonType("Use", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(useButtonType, ButtonType.CANCEL);

        // Create the ComboBox with Pokémon
        ComboBox<Pokemon> pokemonComboBox = new ComboBox<>();
        pokemonComboBox.setItems(getAllPokemon());
        pokemonComboBox.setCellFactory(listView -> new PokemonListCell());  // Your custom cell with image + name
        pokemonComboBox.setButtonCell(new PokemonListCell()); // Show selected Pokémon nicely

        // Optional: Select first by default if available
        if (!pokemonComboBox.getItems().isEmpty()) {
            pokemonComboBox.getSelectionModel().selectFirst();
        }

        // Layout for dialog content
        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Select a Pokémon to use the item on:"), pokemonComboBox);
        dialog.getDialogPane().setContent(content);

        // Enable/disable use button depending on selection
        Node useButton = dialog.getDialogPane().lookupButton(useButtonType);
        useButton.setDisable(pokemonComboBox.getSelectionModel().isEmpty());
        pokemonComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            useButton.setDisable(newVal == null);
        });

        // Result converter to return selected Pokémon when "Use" is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == useButtonType) {
                return pokemonComboBox.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Optional<Pokemon> result = dialog.showAndWait();

        result.ifPresent(selectedPokemon -> {
            applyItemToPokemon(item, selectedPokemon);
        });
    }


    // Example method for applying item effect to a Pokémon
    private void applyItemToPokemon(Item item, Pokemon pokemon) {
//        // Implement your item effect logic here
//        // Example: healing potion, status cure, held item, etc.
//
//        boolean success = false;
//        String message = "";
//
//        if (item.isHealingItem()) {
//            success = pokemon.heal(item.getHealAmount());
//            message = success ? "Healed " + pokemon.getName() : pokemon.getName() + " is already full health!";
//        } else if (item.isHeldItem()) {
//            if (pokemon.getHeldItem() != null) {
//                // You could confirm replacing current held item here
//                pokemon.setHeldItem(null); // discard current item
//            }
//            pokemon.setHeldItem(item);
//            success = true;
//            message = pokemon.getName() + " is now holding " + item.getName();
//        } else {
//            // other item effects...
//            message = "Item effect applied to " + pokemon.getName();
//            success = true;
//        }
//
//        if (success) {
//            trainer.removeItem(item, 1);
//            refreshItemTable();
//            refreshPokemonTables();
//            updateMoneyLabels();
//        }
//
//        showAlert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING, "Item Used", message);
    }

    private void updateMoneyLabels() {
        if (trainer != null) {
            String formattedMoney = String.format("₽%,.2f", trainer.getMoney());
            if (lblRemainingMoney != null) {
                lblRemainingMoney.setText(formattedMoney);
            }
            if (trainerMoney != null) {
                trainerMoney.setText(formattedMoney);
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updatePokemonCounts() {
        if (trainer != null) {
            lblActiveCount.setText("Active: " + trainer.getLineupCount());
            lblStorageCount.setText("Storage: " + trainer.getStorageCount());
        } else {
            lblActiveCount.setText("Active: 0");
            lblStorageCount.setText("Storage: 0");
        }
    }

    private void setupActivePokemonTableContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem moveToStorage = getMenuItem();

        MenuItem releaseItem = createReleaseMenuItem(activePokemonTable);

        contextMenu.getItems().addAll(moveToStorage, releaseItem);

        activePokemonTable.setRowFactory(tv -> {
            TableRow < Pokemon > row = new TableRow < > ();
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return row;
        });
    }

    private MenuItem getMenuItem() {
        MenuItem moveToStorage = new MenuItem("Move to Storage");

        moveToStorage.setOnAction(event -> {
            Pokemon selected = activePokemonTable.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            if (trainer.moveToStorage(selected)) {
                refreshPokemonTables();
                updatePokemonCounts();
            } else {
                showAlert(Alert.AlertType.ERROR, "Cannot move", "Failed to move Pokémon to storage.");
            }
        });
        return moveToStorage;
    }

    private void setupStoragePokemonTableContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem moveToActive = new MenuItem("Move to Active Lineup");

        moveToActive.setOnAction(event -> {
            Pokemon selected = storagePokemonTable.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            if (trainer.moveToLineup(selected)) {
                refreshPokemonTables();
                updatePokemonCounts();
            } else {
                showAlert(Alert.AlertType.ERROR, "Cannot move", "Lineup is full (max 6 Pokémon).");
            }
        });

        MenuItem releaseItem = createReleaseMenuItem(storagePokemonTable);

        contextMenu.getItems().addAll(moveToActive, releaseItem);

        storagePokemonTable.setRowFactory(tv -> {
            TableRow < Pokemon > row = new TableRow < > ();
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return row;
        });
    }

    private MenuItem createReleaseMenuItem(TableView < Pokemon > table) {
        MenuItem releaseItem = new MenuItem("Release");
        releaseItem.setOnAction(event -> {
            Pokemon selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Release");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to release " + selected.getName() + "? This action cannot be undone.");

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (trainer.removePokemon(selected)) {
                        refreshPokemonTables();
                        updatePokemonCounts();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Release Failed", "Failed to release the selected Pokémon.");
                    }
                }
            });
        });
        return releaseItem;
    }

    private void refreshPokemonTables() {
        activePokemonTable.setItems(FXCollections.observableArrayList(trainer.getLineup()));
        storagePokemonTable.setItems(FXCollections.observableArrayList(trainer.getStorage()));
    }

    private ObservableList < Pokemon > getAllPokemon() {
        ObservableList < Pokemon > allPokemon = FXCollections.observableArrayList();
        if (trainer != null) {
            if (trainer.getLineup() != null) allPokemon.addAll(trainer.getLineup());
            if (trainer.getStorage() != null) allPokemon.addAll(trainer.getStorage());
        }
        return allPokemon;
    }

    private void configurePokemonComboBoxCellFactory() {
        pokemonComboBox.setCellFactory(lv -> new PokemonListCell());
        pokemonComboBox.setButtonCell(new PokemonListCell());
    }

    private void setupPokemonComboBox() {
        ObservableList < Pokemon > allPokemon = getAllPokemon();
        pokemonComboBox.setItems(allPokemon);

        configurePokemonComboBoxCellFactory();

        pokemonComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldPoke, newPoke) -> {
            if (newPoke != null) {
                updatePokemonTypeLabel(newPoke);
                updateMoveCountLabel(newPoke);
                updatePokemonImageView(newPoke);

                // Update current moves table with selected Pokémon's moves
                updateCurrentMovesTable(newPoke);

                // Update compatible moves table with compatible moves based on Pokémon's types
                updateCompatibleMovesTable(newPoke);

            } else {
                pokemonTypeLabel.setText("Type: ???");
                pokemonMoveCountLabel.setText("Moves Known: 0/4");
                pokemonImageView.setImage(null);

                currentMovesTable.getItems().clear();
                compatibleMovesTable.getItems().clear();
            }
        });

        if (!allPokemon.isEmpty()) {
            pokemonComboBox.getSelectionModel().selectFirst();
        }
    }

    private void updatePokemonTypeLabel(Pokemon pokemon) {
        String primary = pokemon.getPrimaryType();
        String secondary = pokemon.getSecondaryType();
        String typeText = (secondary == null || secondary.isEmpty()) ?
                primary :
                primary + " / " + secondary;
        pokemonTypeLabel.setText("Type: " + typeText);
    }

    private void updatePokemonImageView(Pokemon pokemon) {
        String url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" +
                pokemon.getPokedexNumber() + ".png";

        Image image = new Image(url, 96, 96, true, true, true);

        // Optionally set placeholder image while loading
        Image placeholder = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/rbee/pokedexgui/images/multiplepokeballs-logo.png")));

        pokemonImageView.setImage(placeholder);

        image.errorProperty().addListener((obs, wasError, isError) -> {
            if (isError) {
                pokemonImageView.setImage(placeholder);
            }
        });

        image.progressProperty().addListener((obs, oldProg, newProg) -> {
            if (newProg.doubleValue() >= 1.0 && !image.isError()) {
                pokemonImageView.setImage(image);
            }
        });
    }

    private void updateMoveCountLabel(Pokemon pokemon) {
        int moveCount = pokemon.getMoveSet() != null ? pokemon.getMoveSet().size() : 0;
        pokemonMoveCountLabel.setText("Moves Known: " + moveCount + "/4");
    }

    private void updatePokemonComboBoxItems() {
        if (trainer == null) {
            pokemonComboBox.setItems(FXCollections.emptyObservableList());
            return;
        }

        // Combine active lineup and storage into one list
        ObservableList < Pokemon > combinedList = FXCollections.observableArrayList();

        if (trainer.getLineup() != null) {
            combinedList.addAll(trainer.getLineup());
        }

        if (trainer.getStorage() != null) {
            for (Pokemon p: trainer.getStorage()) {
                if (!combinedList.contains(p)) { // avoid duplicates
                    combinedList.add(p);
                }
            }
        }

        pokemonComboBox.setItems(combinedList);
    }

    private void setupMoveTables() {
        // Current Moves Table
        moveNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName())
        );

        moveTypeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPrimaryType())
        );

        moveCategoryCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassification().name())
        );

        // Compatible Moves Table
        compMoveNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName())
        );

        compMoveTypeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPrimaryType())
        );

        compMoveClassCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClassification().name())
        );
        setupCompatibleMovesContextMenu();
        setupCurrentMovesContextMenu();

    }

    private void refreshMoveTables() {
        Pokemon selectedPokemon = pokemonComboBox.getSelectionModel().getSelectedItem();
        if (selectedPokemon == null) {
            currentMovesTable.setItems(FXCollections.emptyObservableList());
            compatibleMovesTable.setItems(FXCollections.emptyObservableList());
            return;
        }
        updateCurrentMovesTable(selectedPokemon);
        updateCompatibleMovesTable(selectedPokemon);
    }

    private void updateCurrentMovesTable(Pokemon pokemon) {
        currentMovesTable.setItems(pokemon.getMoveSet());
    }

    private void updateCompatibleMovesTable(Pokemon pokemon) {
        ObservableList<Move> allMoves = MoveManager.getInstance().getMoveList();

        List<String> pokeTypes = Arrays.asList(pokemon.getPrimaryType(), pokemon.getSecondaryType())
                .stream()
                .filter(type -> type != null && !type.trim().isEmpty())
                .collect(Collectors.toList());

        List<Move> compatibleMoves = allMoves.stream()
                .filter(move ->
                        !pokemon.getMoveSet().contains(move) &&
                                pokeTypes.stream()
                                        .anyMatch(type ->
                                                type.equalsIgnoreCase(move.getPrimaryType()) ||
                                                        type.equalsIgnoreCase(move.getSecondaryType())
                                        )
                )
                .collect(Collectors.toList());

        compatibleMovesTable.setItems(FXCollections.observableArrayList(compatibleMoves));
    }


    private void setupCompatibleMovesContextMenu() {
        compatibleMovesTable.setRowFactory(tv -> {
            TableRow < Move > row = new TableRow < > ();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem teachMove = new MenuItem("Teach Move");
            teachMove.setOnAction(e -> {
                Move selectedMove = row.getItem();
                if (selectedMove == null) return;

                Pokemon selectedPokemon = pokemonComboBox.getSelectionModel().getSelectedItem();
                if (selectedPokemon == null) return;

                ObservableList < Move > currentMoves = selectedPokemon.getMoveSet();

                if (currentMoves.size() < 4) {
                    currentMoves.add(selectedMove);
                    refreshMoveTables();
                } else {
                    showForgetMoveDialog(selectedPokemon, selectedMove);
                }
            });
            contextMenu.getItems().add(teachMove);

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }

    private void setupCurrentMovesContextMenu() {
        currentMovesTable.setRowFactory(tv -> {
            TableRow < Move > row = new TableRow < > ();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem forgetMove = new MenuItem("Forget Move");
            forgetMove.setOnAction(e -> {
                Move selectedMove = row.getItem();
                if (selectedMove == null) return;

                if (selectedMove.getClassification() == Move.Classification.HM) {
                    showAlert(Alert.AlertType.WARNING, "Cannot Forget", "HM moves cannot be forgotten.");
                    return;
                }

                Pokemon selectedPokemon = pokemonComboBox.getSelectionModel().getSelectedItem();
                if (selectedPokemon == null) return;

                selectedPokemon.getMoveSet().remove(selectedMove);
                refreshMoveTables();
            });
            contextMenu.getItems().add(forgetMove);

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }

    private void showForgetMoveDialog(Pokemon pokemon, Move newMove) {
        List < Move > forgettableMoves = pokemon.getMoveSet().stream()
                .filter(m -> m.getClassification() != Move.Classification.HM)
                .collect(Collectors.toList());

        if (forgettableMoves.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Cannot Learn Move", "All current moves are HMs and cannot be forgotten.");
            return;
        }

        Dialog < Move > dialog = new Dialog < > ();
        dialog.setTitle("Forget a Move");
        dialog.setHeaderText("Move Limit Reached");
        dialog.setResizable(false);

        // Set the button types
        ButtonType forgetButtonType = new ButtonType("Forget Move", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(forgetButtonType, ButtonType.CANCEL);

        // Create ComboBox with moves to forget
        ComboBox < Move > moveComboBox = new ComboBox < > (FXCollections.observableArrayList(forgettableMoves));
        moveComboBox.setConverter(new StringConverter < > () {
            @Override
            public String toString(Move move) {
                return move == null ? "" : move.getName();
            }
            @Override
            public Move fromString(String string) {
                return null; // Not used
            }
        });
        moveComboBox.getSelectionModel().selectFirst();

        dialog.getDialogPane().setContent(new VBox(new Label("Select a move to forget to learn " + newMove.getName() + ":"), moveComboBox));

        // Enable/disable Forget button depending on selection
        Node forgetButton = dialog.getDialogPane().lookupButton(forgetButtonType);
        forgetButton.setDisable(moveComboBox.getSelectionModel().isEmpty());

        moveComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            forgetButton.setDisable(newVal == null);
        });

        // Result converter for dialog result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == forgetButtonType) {
                return moveComboBox.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Optional < Move > result = dialog.showAndWait();
        result.ifPresent(moveToForget -> {
            pokemon.getMoveSet().remove(moveToForget);
            pokemon.getMoveSet().add(newMove);
            refreshMoveTables();
        });
    }

}