package com.rbee.pokedexgui.controller.pokemon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.rbee.pokedexgui.cells.SpriteImageCell;
import com.rbee.pokedexgui.util.TypeUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import com.rbee.pokedexgui.model.pokemon.Pokemon;
import com.rbee.pokedexgui.manager.PokemonManager;
import com.rbee.pokedexgui.util.PokemonConstants;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import javax.swing.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PokemonViewController implements Initializable {

    private PokemonManager pokemonManager;

    @FXML
    private Pane dashboardContentPane;
    @FXML
    private ScrollPane addPokemonContentPane;

    @FXML
    private VBox viewPokemonTab;

    @FXML
    private Button dashboardTab;

    @FXML
    private Button addPokemonTab;

    @FXML
    private Button viewAllTab;

    @FXML
    private Button searchTab;

    private List < Button > tabButtons;

    @FXML
    private Spinner < Integer > pokemonNumberSpinner;

    @FXML
    private TextField pokemonNameField;

    @FXML
    private JFXComboBox < String > primaryTypeComboBox;

    @FXML
    private JFXComboBox < String > secondaryTypeComboBox;

    @FXML
    private JFXComboBox < String > typeFilterBox;

    @FXML
    private JFXCheckBox hasSecondaryTypeCheckBox;

    @FXML
    private Spinner < Integer > evolvesFromSpinner;

    @FXML
    private Spinner < Integer > evolvesToSpinner;

    @FXML
    private Spinner < Integer > evolutionLevelSpinner;

    @FXML
    private JFXCheckBox hasEvolvesFromCheckBox;

    @FXML
    private JFXCheckBox hasEvolvesToCheckBox;

    @FXML
    private Spinner < Integer > spinnerHP;

    @FXML
    private Spinner < Integer > spinnerAttack;

    @FXML
    private Spinner < Integer > spinnerDefense;

    @FXML
    private Spinner < Integer > spinnerSpecialAttack;

    @FXML
    private Spinner < Integer > spinnerSpecialDefense;

    @FXML
    private Spinner < Integer > spinnerSpeed;

    @FXML
    private JFXButton addButton;

    @FXML
    private TableView < Pokemon > pokemonTableView;

    @FXML private TableColumn < Pokemon, Integer > idColumn;
    @FXML private TableColumn < Pokemon, String > nameColumn;
    @FXML private TableColumn < Pokemon, List < String >> typeColumn;
    @FXML private TableColumn < Pokemon, Integer > hpColumn;
    @FXML private TableColumn < Pokemon, Integer > atkColumn;
    @FXML private TableColumn < Pokemon, Integer > defColumn;
    @FXML private TableColumn < Pokemon, Integer > spaColumn;
    @FXML private TableColumn < Pokemon, Integer > spdColumn;
    @FXML private TableColumn < Pokemon, Integer > speColumn;
    @FXML private TableColumn < Pokemon, Integer > totalColumn;

    @FXML private TextField searchTextField;
    @FXML private Button resetFiltersButton;

    private FilteredList < Pokemon > filteredList;

    private final Tooltip pokemonNumberTooltip = new Tooltip();
    private final Tooltip pokemonNameTooltip = new Tooltip();

    private ObservableList < String > allTypes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.pokemonManager = new PokemonManager();


        initializeTypeLists();
        setupColumns();

        setupTotalColumnBoldStyle(); // modularized call here

        nameColumn.getStyleClass().add("name-column");

        pokemonTableView.setFixedCellSize(60);

        idColumn.setCellFactory(col -> new SpriteImageCell());
        idColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokedexNumber()).asObject()
        );

        setupNameColumnWithTooltip(nameColumn);

        typeColumn.setCellValueFactory(cellData -> getCombinedTypes(cellData.getValue()));
        setupTypeColumnCellFactory(typeColumn);

        pokemonTableView.setItems(pokemonManager.getPokemonList());
        pokemonTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        setupTabButtons();
        setupSpinnerValueFactories();
        setupTypeFilterComboBox(typeFilterBox);
        setupTypeInputComboBox(primaryTypeComboBox);
        setupTypeInputComboBox(secondaryTypeComboBox);
        setupSecondaryTypeToggle();
        setupValidationListeners();

        addButton.setDisable(true);
        updateSaveButtonState();

        // Wrap original list in FilteredList
        filteredList = new FilteredList < > (pokemonManager.getPokemonList(), p -> true);

        // Bind filtered list to TableView
        pokemonTableView.setItems(filteredList);

        setupSearchFilter();

        setupResetFiltersButton();
    }

    private void initializeTypeLists() {
        allTypes = FXCollections.observableArrayList(
                Arrays.stream(TypeUtils.getValidTypes())
                        .map(type -> type.substring(0,1).toUpperCase() + type.substring(1))
                        .collect(Collectors.toList())
        );
    }


    private void setupResetFiltersButton() {
        resetFiltersButton.setOnAction(e -> {
            // Clear search field
            searchTextField.clear();

            // Clear type filters
            typeFilterBox.setValue(null);

            // Reset predicate to show all Pokémon
            filteredList.setPredicate(p -> true);
        });
    }

    private void setupSearchFilter() {
        searchTextField.textProperty().addListener((obs, oldValue, newValue) -> {
            applyCombinedFilter(); // Re-apply combined filters when search text changes
        });
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

    private void setupTabButtons() {
        tabButtons = List.of(dashboardTab, addPokemonTab, viewAllTab);

        for (Button tab: tabButtons) {
            tab.setOnAction(e -> setActiveTab(tab));
        }
        setActiveTab(dashboardTab); // default active tab
    }

    private void setupValidationListeners() {
        setupPokemonNameValidation(); // Name field listener
        setupPokemonNumberValidation(); // Number spinner
        setupEvolutionCheckboxListeners();
        setupPrimaryTypeListener();
        setupEvolutionValidation();
    }

    private void setupSpinnerValueFactories() {
        pokemonNumberSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        PokemonConstants.MIN_POKEDEX, PokemonConstants.MAX_POKEDEX, PokemonConstants.MIN_POKEDEX));
        commitSpinnerOnFocusLost(pokemonNumberSpinner);

        evolvesFromSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        PokemonConstants.MIN_POKEDEX, PokemonConstants.MAX_POKEDEX, PokemonConstants.MIN_POKEDEX));
        commitSpinnerOnFocusLost(evolvesFromSpinner);

        evolvesToSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        PokemonConstants.MIN_POKEDEX, PokemonConstants.MAX_POKEDEX, PokemonConstants.MIN_POKEDEX));
        commitSpinnerOnFocusLost(evolvesToSpinner);

        evolutionLevelSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        PokemonConstants.MIN_LEVEL + 1, PokemonConstants.MAX_LEVEL, PokemonConstants.MIN_LEVEL + 1));
        commitSpinnerOnFocusLost(evolutionLevelSpinner);

        spinnerHP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                PokemonConstants.MIN_BASE_STAT, PokemonConstants.MAX_BASE_STAT, PokemonConstants.DEFAULT_BASE_STAT));
        commitSpinnerOnFocusLost(spinnerHP);

        spinnerAttack.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                PokemonConstants.MIN_BASE_STAT, PokemonConstants.MAX_BASE_STAT, PokemonConstants.DEFAULT_BASE_STAT));
        commitSpinnerOnFocusLost(spinnerAttack);

        spinnerDefense.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                PokemonConstants.MIN_BASE_STAT, PokemonConstants.MAX_BASE_STAT, PokemonConstants.DEFAULT_BASE_STAT));
        commitSpinnerOnFocusLost(spinnerDefense);

        spinnerSpecialAttack.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                PokemonConstants.MIN_BASE_STAT, PokemonConstants.MAX_BASE_STAT, PokemonConstants.DEFAULT_BASE_STAT));
        commitSpinnerOnFocusLost(spinnerSpecialAttack);

        spinnerSpecialDefense.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                PokemonConstants.MIN_BASE_STAT, PokemonConstants.MAX_BASE_STAT, PokemonConstants.DEFAULT_BASE_STAT));
        commitSpinnerOnFocusLost(spinnerSpecialDefense);

        spinnerSpeed.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                PokemonConstants.MIN_BASE_STAT, PokemonConstants.MAX_BASE_STAT, PokemonConstants.DEFAULT_BASE_STAT));
        commitSpinnerOnFocusLost(spinnerSpeed);

        restrictSpinnerToIntegers(pokemonNumberSpinner);
        restrictSpinnerToIntegers(evolvesFromSpinner);
        restrictSpinnerToIntegers(evolvesToSpinner);
        restrictSpinnerToIntegers(evolutionLevelSpinner);
        restrictSpinnerToIntegers(spinnerAttack);
        restrictSpinnerToIntegers(spinnerDefense);
        restrictSpinnerToIntegers(spinnerHP);
        restrictSpinnerToIntegers(spinnerSpecialAttack);
        restrictSpinnerToIntegers(spinnerSpecialDefense);
        restrictSpinnerToIntegers(spinnerSpeed);
    }

    private boolean pokemonNameTouched = false;
    private boolean pokemonNumberTouched = false;

    private void setupPokemonNameValidation() {
        pokemonNameField.setTooltip(pokemonNameTooltip);

        // Track when the user has interacted with the field
        pokemonNameField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                pokemonNameTouched = true;
                validatePokemonName(); // Show error only if touched
                updateSaveButtonState();
            }
        });

        pokemonNameField.textProperty().addListener((obs, oldText, newText) -> {
            validatePokemonName(); // Still validates as user types (but tooltip only shows if touched)
            updateSaveButtonState();
        });
    }
    private boolean validatePokemonName() {
        String name = pokemonNameField.getText();

        // Only validate/show errors if touched
        if (!pokemonNameTouched) {
            pokemonNameTooltip.hide();
            pokemonNameField.setStyle("");
            return true; // skip error showing if not touched yet
        }

        if (name == null || name.trim().isEmpty()) {
            showErrorTooltip(pokemonNameField, pokemonNameTooltip, "Name cannot be empty.");
            pokemonNameField.setStyle("-fx-border-color: red;");
            return false;
        }

        if (!name.matches("[A-Za-z.\\-\\s'()]+")) {
            showErrorTooltip(pokemonNameField, pokemonNameTooltip, "Invalid characters in name.");
            pokemonNameField.setStyle("-fx-border-color: red;");
            return false;
        }

        String formattedName = name.substring(0, 1).toUpperCase() + name.substring(1);

        if (pokemonManager.isPokemonNameTaken(formattedName)) {
            showErrorTooltip(pokemonNameField, pokemonNameTooltip, "This Pokémon name already exists.");
            pokemonNameField.setStyle("-fx-border-color: red;");
            return false;
        }

        // Valid name
        pokemonNameTooltip.hide();
        pokemonNameField.setStyle("");
        return true;
    }

    private void setupPokemonNumberValidation() {
        // Mark as touched when user interacts with the spinner editor
        pokemonNumberSpinner.getEditor().focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) { // focus lost = considered "touched"
                pokemonNumberTouched = true;
                boolean valid = validatePokemonNumber();
                updateSaveButtonState();
            }
        });

        // Text input listener (manual edits)
        pokemonNumberSpinner.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (!pokemonNumberTouched) return; // skip validation if not touched yet

            boolean valid = validatePokemonNumber();
            if (valid) {
                pokemonNumberSpinner.setStyle("");
                pokemonNumberTooltip.hide();
            } else {
                pokemonNumberSpinner.setStyle("-fx-border-color: red;");
            }
            updateSaveButtonState();
        });

        // Value change listener (via arrows or scroll)
        pokemonNumberSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!pokemonNumberTouched) return; // skip if not touched

            boolean valid = validatePokemonNumber();
            if (valid) {
                pokemonNumberSpinner.setStyle("");
                pokemonNumberTooltip.hide();
            } else {
                pokemonNumberSpinner.setStyle("-fx-border-color: red;");
            }
            updateSaveButtonState();
        });
    }
    private boolean validatePokemonNumber() {
        if (!pokemonNumberTouched) {
            pokemonNumberTooltip.hide();
            pokemonNumberSpinner.setStyle("");
            return true;
        }

        String input = pokemonNumberSpinner.getEditor().getText();

        try {
            int number = Integer.parseInt(input);
            if (number < PokemonConstants.MIN_POKEDEX || number > PokemonConstants.MAX_POKEDEX) {
                showErrorTooltip(pokemonNumberSpinner, pokemonNumberTooltip,
                        "Number must be between " + PokemonConstants.MIN_POKEDEX + " and " + PokemonConstants.MAX_POKEDEX);
                return false;
            }
            if (pokemonManager.isPokedexNumberUnique(number)) {
                showErrorTooltip(pokemonNumberSpinner, pokemonNumberTooltip,
                        "This Pokédex number is already taken.");
                return false;
            }
            pokemonNumberTooltip.hide();
            return true;
        } catch (NumberFormatException e) {
            showErrorTooltip(pokemonNumberSpinner, pokemonNumberTooltip, "Please enter a valid number.");
            return false;
        }
    }

    private void setupTypeInputComboBox(JFXComboBox<String> comboBox) {
        comboBox.setItems(FXCollections.observableArrayList(allTypes)); // assign a fresh copy!
        comboBox.setPromptText("Select Type");

        Callback<ListView<String>, ListCell<String>> cellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        };
        comboBox.setCellFactory(cellFactory);
        comboBox.setButtonCell(cellFactory.call(null));
    }


    private void setupTypeFilterComboBox(JFXComboBox<String> comboBox) {
        // Get and capitalize all valid types
        String[] types = TypeUtils.getValidTypes();
        ObservableList<String> capitalizedTypes = FXCollections.observableArrayList();

        capitalizedTypes.add("Any"); // Add "Any" at the top

        for (String type : types) {
            capitalizedTypes.add(type.substring(0, 1).toUpperCase() + type.substring(1));
        }

        comboBox.setItems(capitalizedTypes);

        Callback<ListView<String>, ListCell<String>> cellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        };

        comboBox.setCellFactory(cellFactory);
        comboBox.setButtonCell(cellFactory.call(null));

        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyCombinedFilter());
    }



    private void applyCombinedFilter() {
        String searchQuery = searchTextField.getText() == null ? "" : searchTextField.getText().toLowerCase().trim();
        String selectedType = typeFilterBox.getValue();

        filteredList.setPredicate(pokemon -> {
            // Match search query (ID or name)
            boolean matchesSearch = pokemon.getName().toLowerCase().contains(searchQuery) ||
                    String.valueOf(pokemon.getPokedexNumber()).contains(searchQuery) ||
                    String.format("%03d", pokemon.getPokedexNumber()).contains(searchQuery);

            // Match type filter (check both primary and secondary)
            boolean matchesType = (selectedType == null || selectedType.isEmpty()) ||
                    pokemon.getPrimaryType().equalsIgnoreCase(selectedType) ||
                    (pokemon.getSecondaryType() != null && pokemon.getSecondaryType().equalsIgnoreCase(selectedType));

            return matchesSearch && matchesType;
        });
    }

    private void setupPrimaryTypeListener() {
        primaryTypeComboBox.valueProperty().addListener((obs, oldType, newType) -> {
            if (newType == null) {
                // Reset secondary type to all types
                secondaryTypeComboBox.setItems(FXCollections.observableArrayList(allTypes));
                return;
            }

            // Filter out the newType (case-insensitive)
            ObservableList<String> filtered = allTypes.filtered(type -> !type.equalsIgnoreCase(newType));

            secondaryTypeComboBox.setItems(FXCollections.observableArrayList(filtered));

            // Clear secondary if now invalid
            String secondaryValue = secondaryTypeComboBox.getValue();
            if (secondaryValue != null && secondaryValue.equalsIgnoreCase(newType)) {
                secondaryTypeComboBox.setValue(null);
            }
        });
    }



    private void setupEvolutionCheckboxListeners() {
        // Initially disable spinners if checkbox not selected
        evolvesFromSpinner.setDisable(!hasEvolvesFromCheckBox.isSelected());
        evolvesToSpinner.setDisable(!hasEvolvesToCheckBox.isSelected());
        evolutionLevelSpinner.setDisable(!hasEvolvesToCheckBox.isSelected());

        // Listener for "Has Evolves From?"
        hasEvolvesFromCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            evolvesFromSpinner.setDisable(!isSelected);
            // Removed resetting value here to preserve it
        });

        // Listener for "Has Evolves To?"
        hasEvolvesToCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            evolvesToSpinner.setDisable(!isSelected);
            evolutionLevelSpinner.setDisable(!isSelected);
            // Removed resetting values here to preserve them
        });
    }

    private void validateEvolutionSelfReference() {
        Integer dexNumber = pokemonNumberSpinner.getValue();
        Integer evolvesFrom = evolvesFromSpinner.getValue();
        Integer evolvesTo = evolvesToSpinner.getValue();

        boolean isValid = true;

        Tooltip evolvesFromTooltip = new Tooltip("A Pokémon cannot evolve from itself.");
        Tooltip evolvesToTooltip = new Tooltip("A Pokémon cannot evolve to itself.");

        // Evolves From Self-Reference
        if (hasEvolvesFromCheckBox.isSelected() && evolvesFrom != null && evolvesFrom.equals(dexNumber)) {
            evolvesFromSpinner.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            Tooltip.install(evolvesFromSpinner, evolvesFromTooltip);
            isValid = false;
        } else {
            evolvesFromSpinner.setStyle("");
            Tooltip.uninstall(evolvesFromSpinner, evolvesFromTooltip);
        }

        // Evolves To Self-Reference
        if (hasEvolvesToCheckBox.isSelected() && evolvesTo != null && evolvesTo.equals(dexNumber)) {
            evolvesToSpinner.setStyle("-fx-border-color: red; -fx-border-width: 2;");
            Tooltip.install(evolvesToSpinner, evolvesToTooltip);
            isValid = false;
        } else {
            evolvesToSpinner.setStyle("");
            Tooltip.uninstall(evolvesToSpinner, evolvesToTooltip);
        }

        // Control Add Button
        addButton.setDisable(!isValid);
    }

    private void setupEvolutionValidation() {
        evolvesFromSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateEvolutionSelfReference());
        evolvesToSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateEvolutionSelfReference());
        pokemonNumberSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateEvolutionSelfReference());
    }

    private void showErrorTooltip(Control control, Tooltip tooltip, String message) {
        tooltip.setText(message);
        // Only show tooltip if control is visible and on screen
        if (control.isVisible() && control.getScene() != null && control.getScene().getWindow().isShowing()) {
            javafx.geometry.Bounds bounds = control.localToScreen(control.getBoundsInLocal());
            if (bounds != null) {
                if (!tooltip.isShowing()) {
                    tooltip.show(control, bounds.getMinX(), bounds.getMaxY());
                }
            }
        }
    }
    private void updateSaveButtonState() {
        boolean isNameValid = validatePokemonName();
        boolean isNumberValid = validatePokemonNumber();
        boolean isPrimaryTypeSelected = primaryTypeComboBox.getValue() != null;

        // Add more checks as needed, like secondary type or other fields

        boolean allValid = isNameValid && isNumberValid && isPrimaryTypeSelected;

        addButton.setDisable(!allValid);
    }

    private void setActiveTab(Button activeTab) {
        // Update button styles
        for (Button tab: tabButtons) {
            tab.getStyleClass().remove("active-tab");
        }
        if (!activeTab.getStyleClass().contains("active-tab")) {
            activeTab.getStyleClass().add("active-tab");
        }

        // Show corresponding content pane, hide others
        dashboardContentPane.setVisible(false);
        addPokemonContentPane.setVisible(false);
        viewPokemonTab.setVisible(false);

        if (activeTab == dashboardTab) {
            dashboardContentPane.setVisible(true);
        } else if (activeTab == addPokemonTab) {
            addPokemonContentPane.setVisible(true);
        } else if (activeTab == viewAllTab) {
            viewPokemonTab.setVisible(true);
        }
        // ... add other panes visibility conditions
    }

    private void restrictSpinnerToIntegers(Spinner < Integer > spinner) {
        TextFormatter < Integer > formatter = new TextFormatter < > (c -> {
            String newText = c.getControlNewText();
            if (newText.matches("-?\\d*")) {
                return c;
            }
            return null;
        });
        spinner.getEditor().setTextFormatter(formatter);
    }

    private void commitSpinnerOnFocusLost(Spinner < Integer > spinner) {
        spinner.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                try {
                    spinner.increment(0); // commit value on focus lost
                } catch (Exception ignored) {
                    // fallback if spinner value factory is null
                    if (spinner.getValueFactory() != null) {
                        spinner.getValueFactory().setValue(spinner.getValueFactory().getValue());
                    }
                }
            }
        });
    }

    public void handleAddPokemon(ActionEvent actionEvent) {
        try {
            int pokedexNumber = pokemonNumberSpinner.getValue();
            String name = pokemonNameField.getText().trim();
            String primaryType = primaryTypeComboBox.getValue();
            String secondaryType = hasSecondaryTypeCheckBox.isSelected() ? secondaryTypeComboBox.getValue() : null;
            int evolvesFrom = hasEvolvesFromCheckBox.isSelected() ? evolvesFromSpinner.getValue() : 0;
            int evolvesTo = hasEvolvesToCheckBox.isSelected() ? evolvesToSpinner.getValue() : 0;
            int evolutionLevel = hasEvolvesToCheckBox.isSelected() ? evolutionLevelSpinner.getValue() : 0;
            int hp = spinnerHP.getValue();
            int attack = spinnerAttack.getValue();
            int defense = spinnerDefense.getValue();
            int specialAttack = spinnerSpecialAttack.getValue();
            int specialDefense = spinnerSpecialDefense.getValue();
            int speed = spinnerSpeed.getValue();

            Pokemon.PokemonStats stats = new Pokemon.PokemonStats(
                    hp, attack, defense, specialAttack, specialDefense, speed);

            Pokemon.PokemonEvolutionInfo evolution = new Pokemon.PokemonEvolutionInfo(
                    evolvesFrom, evolvesTo, evolutionLevel);

            Pokemon pokemon = new Pokemon(
                    pokedexNumber, name, primaryType, secondaryType, stats, evolution, ""
            );

            pokemonManager.getPokemonList().add(pokemon);
            JOptionPane.showMessageDialog(null, "Pokémon added successfully: " + name,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            resetFormFields();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void resetFormFields() {
        pokemonNumberSpinner.getValueFactory().setValue(PokemonConstants.MIN_POKEDEX);
        pokemonNameField.clear();
        primaryTypeComboBox.setValue(null);
        secondaryTypeComboBox.setValue(null);
        hasSecondaryTypeCheckBox.setSelected(false);
        evolvesFromSpinner.getValueFactory().setValue(null);
        evolvesToSpinner.getValueFactory().setValue(null);
        evolutionLevelSpinner.getValueFactory().setValue(PokemonConstants.MIN_LEVEL + 1);
        hasEvolvesFromCheckBox.setSelected(false);
        hasEvolvesToCheckBox.setSelected(false);
        spinnerHP.getValueFactory().setValue(PokemonConstants.DEFAULT_BASE_STAT);
        spinnerAttack.getValueFactory().setValue(PokemonConstants.DEFAULT_BASE_STAT);
        spinnerDefense.getValueFactory().setValue(PokemonConstants.DEFAULT_BASE_STAT);
        spinnerSpecialAttack.getValueFactory().setValue(PokemonConstants.DEFAULT_BASE_STAT);
        spinnerSpecialDefense.getValueFactory().setValue(PokemonConstants.DEFAULT_BASE_STAT);
        spinnerSpeed.getValueFactory().setValue(PokemonConstants.DEFAULT_BASE_STAT);

        updateSaveButtonState();
    }

    private void setupSecondaryTypeToggle() {
        // Initially disable secondaryTypeComboBox if checkbox is not checked
        secondaryTypeComboBox.setDisable(!hasSecondaryTypeCheckBox.isSelected());

        hasSecondaryTypeCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            secondaryTypeComboBox.setDisable(!isSelected);
            if (!isSelected) {
                // Optionally clear the secondary type selection when disabling
                secondaryTypeComboBox.setValue(null);
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

    private void setupColumns() {
        // ID column
        idColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokedexNumber()).asObject());

        // Name column
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        // Type column
        typeColumn.setCellValueFactory(cellData -> getCombinedTypes(cellData.getValue()));
        setupTypeColumnCellFactory(typeColumn);

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

}