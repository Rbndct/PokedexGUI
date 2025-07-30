package com.rbee.pokedexgui.controller.pokemon;

import com.jfoenix.controls.*;
import com.rbee.pokedexgui.cells.SpriteImageCell;
import com.rbee.pokedexgui.manager.ActiveTrainerHolder;
import com.rbee.pokedexgui.model.trainer.Trainer;
import com.rbee.pokedexgui.util.TypeUtils;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import com.rbee.pokedexgui.model.pokemon.Pokemon;
import com.rbee.pokedexgui.manager.PokemonManager;
import com.rbee.pokedexgui.util.PokemonConstants;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class PokemonViewController implements Initializable {
    /* Manager */
    private PokemonManager pokemonManager;
    private Trainer activeTrainer;

    /* Snackbar Containers */
    @FXML private StackPane contentStackPane;
    @FXML private HBox snackbarContainer;
    private JFXSnackbar snackbar;

    /* Dashboard Labels */
    @FXML private Text totalPokemonLabel;
    @FXML private Text topTypeLabel;
    @FXML private Text highestBaseStatLabel;

    /* Layout Containers */
    @FXML private Pane dashboardContentPane;
    @FXML private ScrollPane addPokemonContentPane;
    @FXML private VBox viewPokemonTab;

    /* Navigation / Tab Buttons */
    @FXML private Button dashboardTab;
    @FXML private Button addPokemonTab;
    @FXML private Button viewAllTab;
    private List<Button> tabButtons;

    /* Input Fields */
    @FXML private TextField pokemonNameField;

    /* ComboBoxes */
    @FXML private JFXComboBox<String> primaryTypeComboBox;
    @FXML private JFXComboBox<String> secondaryTypeComboBox;
    @FXML private JFXComboBox<String> typeFilterBox;

    /* CheckBoxes */
    @FXML private JFXCheckBox hasSecondaryTypeCheckBox;
    @FXML private JFXCheckBox hasEvolvesFromCheckBox;
    @FXML private JFXCheckBox hasEvolvesToCheckBox;

    /* Stat Spinners */
    @FXML private Spinner<Integer> spinnerHP;
    @FXML private Spinner<Integer> spinnerAttack;
    @FXML private Spinner<Integer> spinnerDefense;
    @FXML private Spinner<Integer> spinnerSpecialAttack;
    @FXML private Spinner<Integer> spinnerSpecialDefense;
    @FXML private Spinner<Integer> spinnerSpeed;

    /* Evolution Spinners */
    @FXML private Spinner<Integer> pokemonNumberSpinner;
    @FXML private Spinner<Integer> evolvesFromSpinner;
    @FXML private Spinner<Integer> evolvesToSpinner;
    @FXML private Spinner<Integer> evolutionLevelSpinner;

    /* Action Buttons */
    @FXML private JFXButton addButton;


    /* TableView and Columns */
    @FXML private TableView<Pokemon> pokemonTableView;
    @FXML private TableColumn<Pokemon, Integer> idColumn;
    @FXML private TableColumn<Pokemon, String> nameColumn;
    @FXML private TableColumn<Pokemon, List<String>> typeColumn;
    @FXML private TableColumn<Pokemon, Integer> hpColumn;
    @FXML private TableColumn<Pokemon, Integer> atkColumn;
    @FXML private TableColumn<Pokemon, Integer> defColumn;
    @FXML private TableColumn<Pokemon, Integer> spaColumn;
    @FXML private TableColumn<Pokemon, Integer> spdColumn;
    @FXML private TableColumn<Pokemon, Integer> speColumn;
    @FXML private TableColumn<Pokemon, Integer> totalColumn;

    /* Filter/Search Components */
    @FXML private TextField searchTextField;
    @FXML private Button resetFiltersButton;

    /* Data Structures */
    private FilteredList<Pokemon> filteredList;
    private ObservableList<String> allTypes;

    /* Tooltips for Validation */
    private final Tooltip pokemonNumberTooltip = new Tooltip();
    private final Tooltip pokemonNameTooltip = new Tooltip();
    private final Tooltip primaryTypeTooltip = new Tooltip();
    private final Tooltip secondaryTypeTooltip = new Tooltip();
    private final Tooltip evolvesFromTooltip = new Tooltip();
    private final Tooltip evolvesToTooltip = new Tooltip();
    private final Tooltip evolutionLevelTooltip = new Tooltip();

    /* Validation Flags */
    private boolean pokemonNumberSpinnerTouched = false;
    private boolean pokemonNameFieldTouched = false;
    private boolean primaryTypeTouched = false;
    private boolean secondaryTypeTouched = false;
    private boolean evolvesFromTouched = false;
    private boolean evolvesToTouched = false;
    private boolean evolutionLevelTouched = false;

    /* Charts and Lists */
    @FXML private PieChart typeDistributionChart;
    @FXML private ListView<Pokemon> recentAdditionsListView;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize manager and data structures
        this.pokemonManager = new PokemonManager();
        activeTrainer = ActiveTrainerHolder.getActiveTrainer();


        // Initialize observable filtered list wrapping the master list
        filteredList = new FilteredList<>(pokemonManager.getPokemonList(), p -> true);

        // Setup UI components and bindings
        initializeTypeLists();

        setupColumns();
        setupTotalColumnBoldStyle();

        // Style adjustments
        nameColumn.getStyleClass().add("name-column");
        pokemonTableView.setFixedCellSize(60);
        pokemonTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Setup columns' cell factories and value factories
        idColumn.setCellFactory(col -> new SpriteImageCell());
        idColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPokedexNumber()).asObject()
        );
        setupNameColumnWithTooltip(nameColumn);

        typeColumn.setCellValueFactory(cellData -> getCombinedTypes(cellData.getValue()));
        setupTypeColumnCellFactory(typeColumn);

        // Bind filtered list to table
        pokemonTableView.setItems(filteredList);

        // Setup form controls and filters
        setupTabButtons();
        setupSpinnerValueFactories();

        setupTypeFilterComboBox(typeFilterBox);
        setupTypeInputComboBox(primaryTypeComboBox);
        setupTypeInputComboBox(secondaryTypeComboBox);
        setupSecondaryTypeToggle();

        setupPrimaryTypeListener();

        // Disable add button initially
        addButton.setDisable(true);

        // Setup filters and search
        setupSearchFilter();
        setupResetFiltersButton();

        // Add validation listeners to inputs
        addPokemonNumberSpinnerValidationListener();
        addPokemonNameFieldValidationListener();
        addTypeFieldValidationListeners();
        addEvolutionValidationListeners();

        // Setup snackbar for notifications
        snackbar = new JFXSnackbar(contentStackPane);
        snackbarContainer.setVisible(false);

        // Initialize dashboard stats and charts
        updateDashboardStats();
        updateTypeDistributionChart();

        // Setup recent additions list view
        setupRecentAdditionsListView();

        // Listen to master Pokémon list changes to update dashboard info dynamically
        pokemonManager.getPokemonList().addListener((ListChangeListener<Pokemon>) c -> {
            updateDashboardStats();
            updateTypeDistributionChart();
        });
        setupPokemonRowClick();

        setupPokemonTableContextMenu();
    }





    private void addPokemonNameFieldValidationListener() {
        // Focus listener to mark "touched"
        pokemonNameField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // lost focus
                pokemonNameFieldTouched = true;
                validateAndUpdateForm();
            }
        });

        // Text change listener — only validate if already touched
        pokemonNameField.textProperty().addListener((obs, oldText, newText) -> {
            if (!pokemonNameFieldTouched) return;
            validateAndUpdateForm();
        });
    }

    private boolean validatePokemonNameField() {
        String input = pokemonNameField.getText() == null ? "" : pokemonNameField.getText().trim();

        if (input.isEmpty()) {
            showTooltip(pokemonNameField, pokemonNameTooltip, "Pokémon name cannot be empty.");
            pokemonNameField.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        if (!input.matches("[A-Za-z.\\-\\s'()]+")) {
            showTooltip(pokemonNameField, pokemonNameTooltip,
                    "Invalid characters in name. Only letters, dashes, apostrophes, periods, parentheses, and spaces are allowed.");
            pokemonNameField.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        if (pokemonManager.isPokemonNameTaken(input)) {
            showTooltip(pokemonNameField, pokemonNameTooltip,
                    "This Pokémon name already exists. If it's a regional variant, add the region in parentheses (e.g., 'Meowth (Galar)').");
            pokemonNameField.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        // VALID
        pokemonNameField.setStyle("-fx-border-color: transparent;");
        pokemonNameTooltip.hide();
        return true;
    }

    private void addPokemonNumberSpinnerValidationListener() {
        pokemonNumberSpinner.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // lost focus = touched
                pokemonNumberSpinnerTouched = true;
                validateAndUpdateForm();
            }
        });

        pokemonNumberSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!pokemonNumberSpinnerTouched) return;
            validateAndUpdateForm();
        });
    }

    private boolean validatePokemonNumberSpinner() {
        Integer value = pokemonNumberSpinner.getValue();

        if (value == null) {
            showTooltip(pokemonNumberSpinner, pokemonNumberTooltip, "Pokédex number is required.");
            pokemonNumberSpinner.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        if (value < PokemonConstants.MIN_POKEDEX || value > PokemonConstants.MAX_POKEDEX) {
            showTooltip(pokemonNumberSpinner, pokemonNumberTooltip,
                    "Number must be between " + PokemonConstants.MIN_POKEDEX + " and " + PokemonConstants.MAX_POKEDEX + ".");
            pokemonNumberSpinner.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        if (!pokemonManager.isPokedexNumberUnique(value)) {
            showTooltip(pokemonNumberSpinner, pokemonNumberTooltip, "This Pokédex number is already used.");
            pokemonNumberSpinner.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        // Valid input
        pokemonNumberSpinner.setStyle("-fx-border-color: transparent;");
        pokemonNumberTooltip.hide();
        return true;
    }

    private void addTypeFieldValidationListeners() {
        // Primary type combo box focus tracking
        primaryTypeComboBox.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) {
                primaryTypeTouched = true;
                validateAndUpdateForm();
            }
        });

        // Selection listener for primary type
        primaryTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (primaryTypeTouched) {
                validateAndUpdateForm();
            }
        });

        // Secondary type combo box focus tracking
        secondaryTypeComboBox.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) {
                secondaryTypeTouched = true;
                validateAndUpdateForm();
            }
        });

        // Selection listener for secondary type
        secondaryTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (secondaryTypeTouched) {
                validateAndUpdateForm();
            }
        });

        // Listener for the checkbox (toggling secondary requirement)
        hasSecondaryTypeCheckBox.selectedProperty().addListener((obs, wasChecked, isChecked) -> {
            validateAndUpdateForm();
        });
    }

    private boolean validateTypeComboBoxes() {
        boolean isValid = true;

        // Validate Primary Type
        String primary = primaryTypeComboBox.getValue();
        if (primary == null || primary.trim().isEmpty()) {
            if (primaryTypeTouched) {
                showTooltip(primaryTypeComboBox, primaryTypeTooltip, "Primary type must be selected.");
                primaryTypeComboBox.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            }
            isValid = false;
        } else {
            primaryTypeComboBox.setStyle("-fx-border-color: transparent;");
            primaryTypeTooltip.hide();
        }

        // Validate Secondary Type only if required
        if (hasSecondaryTypeCheckBox.isSelected()) {
            String secondary = secondaryTypeComboBox.getValue();
            if (secondary == null || secondary.trim().isEmpty()) {
                if (secondaryTypeTouched) {
                    showTooltip(secondaryTypeComboBox, secondaryTypeTooltip, "Secondary type must be selected.");
                    secondaryTypeComboBox.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
                }
                isValid = false;
            } else {
                secondaryTypeComboBox.setStyle("-fx-border-color: transparent;");
                secondaryTypeTooltip.hide();
            }
        } else {
            // If secondary not required, clear any red border or tooltip
            secondaryTypeComboBox.setStyle("-fx-border-color: transparent;");
            secondaryTypeTooltip.hide();
        }

        return isValid;
    }

    private void validateAndUpdateForm() {
        boolean isPokemonNumberValid = validatePokemonNumberSpinner();
        boolean isPokemonNameValid = validatePokemonNameField();
        boolean areTypesValid = validateTypeComboBoxes();
        boolean isEvolvesFromValid = validateEvolvesFromSpinner();
        boolean isEvolvesToAndLevelValid = validateEvolvesToAndLevel();

        updateFormState(isPokemonNumberValid, isPokemonNameValid, areTypesValid, isEvolvesFromValid, isEvolvesToAndLevelValid);
    }

    private void updateFormState(
            boolean isPokemonNumberValid,
            boolean isPokemonNameValid,
            boolean areTypesValid,
            boolean isEvolvesFromValid,
            boolean isEvolvesToAndLevelValid
    ) {
        boolean allValid = isPokemonNumberValid &&
                isPokemonNameValid &&
                areTypesValid &&
                isEvolvesFromValid &&
                isEvolvesToAndLevelValid;
        addButton.setDisable(!allValid);
    }

    private void showTooltip(Control control, Tooltip tooltip, String message) {
        tooltip.setText(message);
        if (control.isVisible() && control.getScene() != null && control.getScene().getWindow().isShowing()) {
            javafx.geometry.Bounds bounds = control.localToScreen(control.getBoundsInLocal());
            if (bounds != null && !tooltip.isShowing()) {
                tooltip.show(control, bounds.getMinX(), bounds.getMaxY());

                // Hide the tooltip automatically after 3 seconds
                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(event -> tooltip.hide());
                delay.play();
            }
        }
    }

    private void initializeTypeLists() {
        allTypes = FXCollections.observableArrayList(
                Arrays.stream(TypeUtils.getValidTypes())
                        .map(type -> type.substring(0, 1).toUpperCase() + type.substring(1))
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
    private void setupTypeInputComboBox(JFXComboBox < String > comboBox) {
        comboBox.setItems(FXCollections.observableArrayList(allTypes)); // assign a fresh copy!
        comboBox.setPromptText("Select Type");

        Callback < ListView < String > , ListCell < String >> cellFactory = lv -> new ListCell < > () {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        };
        comboBox.setCellFactory(cellFactory);
        comboBox.setButtonCell(cellFactory.call(null));
    }
    private void setupTypeFilterComboBox(JFXComboBox < String > comboBox) {
        // Get and capitalize all valid types
        String[] types = TypeUtils.getValidTypes();
        ObservableList < String > capitalizedTypes = FXCollections.observableArrayList();

        capitalizedTypes.add("Any"); // Add "Any" at the top

        for (String type: types) {
            capitalizedTypes.add(type.substring(0, 1).toUpperCase() + type.substring(1));
        }

        comboBox.setItems(capitalizedTypes);

        Callback < ListView < String > , ListCell < String >> cellFactory = lv -> new ListCell < > () {
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

            // If selectedType is "Any" (or null/empty), accept all types
            if (selectedType == null || selectedType.isEmpty() || selectedType.equalsIgnoreCase("Any")) {
                return matchesSearch; // no filtering by type
            }

            // Otherwise, match primary or secondary type
            boolean matchesType = pokemon.getPrimaryType().equalsIgnoreCase(selectedType) ||
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
            ObservableList < String > filtered = allTypes.filtered(type -> !type.equalsIgnoreCase(newType));

            secondaryTypeComboBox.setItems(FXCollections.observableArrayList(filtered));

            // Clear secondary if now invalid
            String secondaryValue = secondaryTypeComboBox.getValue();
            if (secondaryValue != null && secondaryValue.equalsIgnoreCase(newType)) {
                secondaryTypeComboBox.setValue(null);
            }
        });
    }

    private void addEvolutionValidationListeners() {
        evolvesFromSpinner.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                evolvesFromTouched = true;
                validateAndUpdateForm();
            }
        });

        evolvesFromSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (evolvesFromTouched) {
                validateAndUpdateForm();
            }
        });

        evolvesToSpinner.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                evolvesToTouched = true;
                validateAndUpdateForm();
            }
        });

        evolvesToSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (evolvesToTouched) {
                validateAndUpdateForm();
            }
        });

        evolutionLevelSpinner.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                evolutionLevelTouched = true;
                validateAndUpdateForm();
            }
        });

        evolutionLevelSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (evolutionLevelTouched) {
                validateAndUpdateForm();
            }
        });

        hasEvolvesFromCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            evolvesFromSpinner.setDisable(!newVal);
            validateAndUpdateForm();
        });

        hasEvolvesToCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            evolvesToSpinner.setDisable(!newVal);
            evolutionLevelSpinner.setDisable(!newVal);
            validateAndUpdateForm();
        });

        // Optionally initialize the spinner enabled/disabled state here
        evolvesFromSpinner.setDisable(!hasEvolvesFromCheckBox.isSelected());
        evolvesToSpinner.setDisable(!hasEvolvesToCheckBox.isSelected());
        evolutionLevelSpinner.setDisable(!hasEvolvesToCheckBox.isSelected());
    }

    private boolean validateEvolvesToAndLevel() {
        if (!hasEvolvesToCheckBox.isSelected()) {
            // Clear styles and tooltips
            evolvesToSpinner.setStyle("-fx-border-color: transparent;");
            evolutionLevelSpinner.setStyle("-fx-border-color: transparent;");
            evolvesToTooltip.hide();
            evolutionLevelTooltip.hide();
            return true;
        }

        Integer pokemonNumber = pokemonNumberSpinner.getValue();
        Integer evolvesToNumber = evolvesToSpinner.getValue();
        Integer evolutionLevel = evolutionLevelSpinner.getValue();

        boolean valid = true;

        // Validate evolvesToSpinner
        if (evolvesToNumber == null || evolvesToNumber.equals(pokemonNumber)) {
            evolvesToSpinner.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            evolvesToTooltip.setText("Cannot evolve to itself.");
            if (evolvesToSpinner.getScene() != null && evolvesToSpinner.getScene().getWindow().isShowing()) {
                var bounds = evolvesToSpinner.localToScreen(evolvesToSpinner.getBoundsInLocal());
                evolvesToTooltip.show(evolvesToSpinner, bounds.getMinX(), bounds.getMaxY());
            }
            valid = false;
        } else {
            evolvesToSpinner.setStyle("-fx-border-color: transparent;");
            evolvesToTooltip.hide();
        }

        // Validate evolutionLevelSpinner
        if (evolutionLevel == null || evolutionLevel < 2) {
            evolutionLevelSpinner.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            evolutionLevelTooltip.setText("Evolution level must be 2 or higher.");
            if (evolutionLevelSpinner.getScene() != null && evolutionLevelSpinner.getScene().getWindow().isShowing()) {
                var bounds = evolutionLevelSpinner.localToScreen(evolutionLevelSpinner.getBoundsInLocal());
                evolutionLevelTooltip.show(evolutionLevelSpinner, bounds.getMinX(), bounds.getMaxY());
            }
            valid = false;
        } else {
            evolutionLevelSpinner.setStyle("-fx-border-color: transparent;");
            evolutionLevelTooltip.hide();
        }

        // Also validate against evolvesFrom == evolvesTo conflict
        return valid && validateEvolvesFromToConflict();
    }


    private boolean validateEvolvesFromSpinner() {
        if (!hasEvolvesFromCheckBox.isSelected()) {
            evolvesFromSpinner.setStyle("-fx-border-color: transparent;");
            evolvesFromTooltip.hide();
            return true;
        }

        Integer input = evolvesFromSpinner.getValue();
        Integer pokemonNumber = pokemonNumberSpinner.getValue();
        boolean valid = true;

        if (input == null || input.equals(pokemonNumber)) {
            evolvesFromSpinner.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            evolvesFromTooltip.setText("Cannot evolve from itself.");
            if (evolvesFromSpinner.getScene() != null && evolvesFromSpinner.getScene().getWindow().isShowing()) {
                var bounds = evolvesFromSpinner.localToScreen(evolvesFromSpinner.getBoundsInLocal());
                evolvesFromTooltip.show(evolvesFromSpinner, bounds.getMinX(), bounds.getMaxY());
            }
            valid = false;
        } else {
            evolvesFromSpinner.setStyle("-fx-border-color: transparent;");
            evolvesFromTooltip.hide();
        }

        // Also validate against evolvesFrom == evolvesTo conflict
        return valid && validateEvolvesFromToConflict();
    }


    private boolean validateEvolvesFromToConflict() {
        if (!hasEvolvesFromCheckBox.isSelected() || !hasEvolvesToCheckBox.isSelected()) return true;

        Integer from = evolvesFromSpinner.getValue();
        Integer to = evolvesToSpinner.getValue();

        if (from != null && from.equals(to)) {
            String msg = "Cannot evolve from and to the same Pokémon.";

            evolvesFromSpinner.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            evolvesFromTooltip.setText(msg);
            if (evolvesFromSpinner.getScene() != null && evolvesFromSpinner.getScene().getWindow().isShowing()) {
                var bounds = evolvesFromSpinner.localToScreen(evolvesFromSpinner.getBoundsInLocal());
                evolvesFromTooltip.show(evolvesFromSpinner, bounds.getMinX(), bounds.getMaxY());
            }

            evolvesToSpinner.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            evolvesToTooltip.setText(msg);
            if (evolvesToSpinner.getScene() != null && evolvesToSpinner.getScene().getWindow().isShowing()) {
                var bounds = evolvesToSpinner.localToScreen(evolvesToSpinner.getBoundsInLocal());
                evolvesToTooltip.show(evolvesToSpinner, bounds.getMinX(), bounds.getMaxY());
            }

            return false;
        }

        return true;
    }


    private void setActiveTab(Button activeTab) {
        // Update button styles
        for (Button tab: tabButtons) {
            tab.getStyleClass().remove("active-tab");
        }
        if (!activeTab.getStyleClass().contains("active-tab")) {
            activeTab.getStyleClass().add("active-tab");
        }

        // Hide all content panes first
        dashboardContentPane.setVisible(false);
        addPokemonContentPane.setVisible(false);
        viewPokemonTab.setVisible(false);

        // Hide add Pokémon tooltips if we're switching away from addPokemonTab
        if (activeTab != addPokemonTab) {
            hideAddPokemonTooltips();
        }

        // Show corresponding content pane
        if (activeTab == dashboardTab) {
            dashboardContentPane.setVisible(true);
        } else if (activeTab == addPokemonTab) {
            addPokemonContentPane.setVisible(true);
        } else if (activeTab == viewAllTab) {
            viewPokemonTab.setVisible(true);
        }
    }

    private void hideAddPokemonTooltips() {
        pokemonNumberTooltip.hide();
        pokemonNameTooltip.hide();
        primaryTypeTooltip.hide();
        secondaryTypeTooltip.hide();
        evolvesFromTooltip.hide();
        evolvesToTooltip.hide();
        evolutionLevelTooltip.hide();
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
                    hp, attack, defense, specialAttack, specialDefense, speed
            );

            Pokemon.PokemonEvolutionInfo evolution = new Pokemon.PokemonEvolutionInfo(
                    evolvesFrom, evolvesTo, evolutionLevel, false
            );

            Pokemon pokemon = new Pokemon(
                    pokedexNumber, name, primaryType, secondaryType, stats, evolution, null
            );

            pokemonManager.addPokemon(pokemon);

            JFXSnackbarLayout layout = new JFXSnackbarLayout("Pokémon added successfully: " + name);
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(layout, Duration.seconds(3)));

            resetFormFields();

        } catch (Exception e) {
            e.printStackTrace();


            JFXSnackbarLayout errorLayout = new JFXSnackbarLayout("Failed to add Pokémon. Please try again.");
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(errorLayout, Duration.seconds(3)));
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


        hideAddPokemonTooltips();
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

    public void updateDashboardStats() {
        int total = pokemonManager.getTotalPokemonCount();
        Map.Entry < String, Long > topTypeEntry = pokemonManager.getTopType();
        Pokemon highestStatPokemon = pokemonManager.getHighestBaseStatPokemon();

        totalPokemonLabel.setText(String.valueOf(total));

        if (topTypeEntry != null) {
            topTypeLabel.setText(topTypeEntry.getKey() + " (" + topTypeEntry.getValue() + ")");
        } else {
            topTypeLabel.setText("N/A");
        }

        if (highestStatPokemon != null) {
            int totalStats = highestStatPokemon.getPokemonStats().getHp() +
                    highestStatPokemon.getPokemonStats().getAttack() +
                    highestStatPokemon.getPokemonStats().getDefense() +
                    highestStatPokemon.getPokemonStats().getSpAttack() +
                    highestStatPokemon.getPokemonStats().getSpDefense() +
                    highestStatPokemon.getPokemonStats().getSpeed();
            highestBaseStatLabel.setText(highestStatPokemon.getName() + " (" + totalStats + ")");
        } else {
            highestBaseStatLabel.setText("N/A");
        }
    }

    private void updateTypeDistributionChart() {
        Map < String, Integer > typeCountMap = new HashMap < > ();

        for (Pokemon p: pokemonManager.getPokemonList()) {
            String type1 = p.getPrimaryType();
            String type2 = p.getSecondaryType();

            typeCountMap.put(type1, typeCountMap.getOrDefault(type1, 0) + 1);

            if (type2 != null && !type2.isEmpty() && !type2.equalsIgnoreCase(type1)) {
                typeCountMap.put(type2, typeCountMap.getOrDefault(type2, 0) + 1);
            }
        }

        ObservableList < PieChart.Data > pieChartData = FXCollections.observableArrayList();
        for (Map.Entry < String, Integer > entry: typeCountMap.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        typeDistributionChart.setData(pieChartData);
    }

    private void setupRecentAdditionsListView() {
        recentAdditionsListView.setItems(pokemonManager.getRecentAdditions());

        recentAdditionsListView.setCellFactory(list -> new ListCell < > () {
            @Override
            protected void updateItem(Pokemon pokemon, boolean empty) {
                super.updateItem(pokemon, empty);
                if (empty || pokemon == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(pokemon.getName() + " (#" + pokemon.getPokedexNumber() + ")");
                }
            }
        });
    }

    public void handleResetPokemonForm(ActionEvent actionEvent) {
        resetFormFields();
    }

    private void setupPokemonRowClick() {
        pokemonTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                Pokemon selectedPokemon = pokemonTableView.getSelectionModel().getSelectedItem();
                if (selectedPokemon != null) {
                    openPokemonDetailView(selectedPokemon);
                }
            }
        });
    }


    private void openPokemonDetailView(Pokemon selectedPokemon) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rbee/pokedexgui/view/module/pokemon/PokemonDetailPane.fxml"));
            Parent root = loader.load();

            PokemonDetailViewController controller = loader.getController();

            // Inject your PokemonManager instance here BEFORE calling setPokemon
            controller.setPokemonManager(this.pokemonManager);  // assuming 'this' has the PokemonManager instance

            controller.setPokemon(selectedPokemon); // pass data

            Stage stage = new Stage();
            stage.setTitle(selectedPokemon.getName() + " - Details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupPokemonTableContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addToActiveTrainerItem = new MenuItem("Add to Active Trainer");
        contextMenu.getItems().add(addToActiveTrainerItem);

        addToActiveTrainerItem.setOnAction(event -> {
            Pokemon selectedPokemon = pokemonTableView.getSelectionModel().getSelectedItem();
            if (selectedPokemon == null) {
                snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("No Pokémon selected."), Duration.seconds(3)));
                return;
            }

            Trainer activeTrainer = ActiveTrainerHolder.getActiveTrainer();
            if (activeTrainer == null) {
                snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("No active trainer selected."), Duration.seconds(3)));
                return;
            }

            // Prevent duplicate Pokémon (based on Pokédex number) in Lineup or Storage
            boolean alreadyExists = activeTrainer.getLineup().stream()
                    .anyMatch(p -> p.getPokedexNumber() == selectedPokemon.getPokedexNumber()) ||
                    activeTrainer.getStorage().stream()
                            .anyMatch(p -> p.getPokedexNumber() == selectedPokemon.getPokedexNumber());

            if (alreadyExists) {
                snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout(
                        selectedPokemon.getName() + " is already owned by " + activeTrainer.getName()), Duration.seconds(3)));
                return;
            }

            // 🧬 Clone the Pokémon (deep copy)
            Pokemon clonedPokemon = new Pokemon(selectedPokemon);

            // Check if Lineup is full
            if (activeTrainer.getLineup().size() < 6) {
                activeTrainer.getLineup().add(clonedPokemon);
                snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout(
                        "Added " + clonedPokemon.getName() + " to " + activeTrainer.getName() + "'s lineup."), Duration.seconds(3)));
            } else {
                activeTrainer.getStorage().add(clonedPokemon);
                snackbar.enqueue(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout(
                        activeTrainer.getName() + "'s lineup is full. " + clonedPokemon.getName() + " added to storage."), Duration.seconds(3)));
            }

            // ✅ Debug print to verify cloned Pokémon stats and identity
        });

        pokemonTableView.setRowFactory(tv -> {
            TableRow<Pokemon> row = new TableRow<>();
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return row;
        });
    }



}