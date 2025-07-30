package com.rbee.pokedexgui.controller.move;

import com.jfoenix.controls.*;
import com.rbee.pokedexgui.cells.MoveTypeIconCell;
import com.rbee.pokedexgui.manager.MoveManager;
import com.rbee.pokedexgui.model.move.Move;
import com.rbee.pokedexgui.model.pokemon.Pokemon;
import com.rbee.pokedexgui.util.TypeUtils;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MoveViewController implements Initializable {

    /* Manager */
    private MoveManager moveManager;

    /* Form Fields for Adding a Move (Input Fields) */
    @FXML private TextField moveNameField;
    @FXML private JFXTextArea moveDescription;
    @FXML private JFXComboBox<Move.Classification> comboClassification;
    @FXML private JFXCheckBox hasSecondaryTypeCheckBox;
    @FXML private JFXComboBox<String> primaryTypeComboBox;
    @FXML private JFXComboBox<String> secondaryTypeComboBox;

    /* Action Buttons */
    @FXML private JFXButton clearButton;
    @FXML private JFXButton addButton;

    /* Tabs and Navigation Buttons */
    @FXML private JFXButton dashboardTab;
    @FXML private JFXButton addMoveTab;
    @FXML private JFXButton viewAllTab;
    private List<JFXButton> movesTabButtons;

    /* Content Panes (Layout Containers) */
    @FXML private Pane dashboardContentPane;
    @FXML private ScrollPane addMoveContentPane;
    @FXML private VBox viewAllContentPane;

    /* TableView and Columns (Table Components) */
    @FXML private TableView<Move> moveTableView;
    @FXML private TableColumn<Move, String> nameColumn;
    @FXML private TableColumn<Move, String> descriptionColumn;
    @FXML private TableColumn<Move, Move.Classification> classificationColumn;
    @FXML private TableColumn<Move, List<String>> typeColumn;

    /* Data and State Management (Other) */
    private ObservableList<String> allTypes;
    private final ObservableList<Move> moveList = FXCollections.observableArrayList();
    private FilteredList<Move> filteredMoveList;

    /* Validation & Tooltip State Flags (Other) */
    private boolean moveDescriptionTouched = false;
    private final Tooltip moveDescriptionTooltip = new Tooltip();

    private boolean moveNameFieldTouched = false;
    private final Tooltip moveNameFieldTooltip = new Tooltip();

    private boolean primaryTypeTouched = false;
    private final Tooltip primaryTypeTooltip = new Tooltip();

    private boolean secondaryTypeTouched = false;
    private final Tooltip secondaryTypeTooltip = new Tooltip();

    private boolean hasSecondaryTouched = false;

    private boolean classificationTouched = false;
    private final Tooltip classificationTooltip = new Tooltip();

    /* Filter/Search Components */
    @FXML private TextField searchTextField;
    @FXML private JFXComboBox<String> typeFilterBox;
    @FXML private Button resetFiltersButton;

    /* Snackbar Containers */
    @FXML private StackPane contentStackPane;
    @FXML private HBox snackbarContainer;
    private JFXSnackbar snackbar;


    private FilteredList<Move> filteredList;

    @FXML
    private Text totalMoveLabel;

    @FXML
    private Text totalTMLabel;

    @FXML
    private Text totalHMLabel;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize move manager and filtered move list
        this.moveManager = MoveManager.getInstance();
        filteredMoveList = new FilteredList<>(moveManager.getMoveList(), move -> true);

        // Setup Classification combo box with formatted display
        comboClassification.getItems().addAll(Move.Classification.values());
        comboClassification.setConverter(getClassificationConverter());
        comboClassification.setCellFactory(listView -> new JFXListCell<>() {
            @Override
            protected void updateItem(Move.Classification item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatClassification(item));
            }
        });

        // Disable add button initially
        addButton.setDisable(true);

        // Initialize type lists and setup type input combo boxes with listeners
        initializeTypeLists();
        setupTypeInputComboBox(primaryTypeComboBox);
        setupTypeInputComboBox(secondaryTypeComboBox);
        setupPrimaryTypeListener();
        setupSecondaryTypeCheckbox();

        // Setup buttons in the Moves tab (add, edit, delete, etc.)
        setupMovesTabButtons();

        // Setup type filter combo box for searching moves by type
        setupTypeFilterComboBox(typeFilterBox);

        // Setup search filtering and reset filter button
        setupSearchFilter();
        setupResetFiltersButton();

        // Add validation listeners for move form inputs
        addMoveNameFieldValidationListener();
        addMoveDescriptionValidationListener();
        addTypeSelectionValidationListener();
        addComboClassificationValidationListener();

        // Setup table columns (including your custom cell factories)
        setupColumns();

        // Bind filtered move list to table view
        moveTableView.setItems(filteredMoveList);

        // Setup snackbar for user notifications
        snackbar = new JFXSnackbar(contentStackPane);
        snackbarContainer.setVisible(false);

        // Update move statistics dashboard info
        updateMoveStats();

        // Add listener to move list changes for dynamic UI update
        moveManager.getMoveList().addListener((ListChangeListener<Move>) c -> updateMoveStats());
    }


    // Initialize allTypes list with capitalized type names
    private void initializeTypeLists() {
        allTypes = FXCollections.observableArrayList(
                Arrays.stream(TypeUtils.getValidTypes())
                        .map(type -> type.substring(0, 1).toUpperCase() + type.substring(1))
                        .collect(Collectors.toList())
        );
    }

    // Setup combo box with prompt and custom cells
    private void setupTypeInputComboBox(JFXComboBox < String > comboBox) {
        comboBox.setItems(FXCollections.observableArrayList(allTypes)); // fresh copy
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

    // Filter secondary types based on primary type selection
    private void setupPrimaryTypeListener() {
        primaryTypeComboBox.valueProperty().addListener((obs, oldType, newType) -> {
            if (newType == null) {
                secondaryTypeComboBox.setItems(FXCollections.observableArrayList(allTypes));
                return;
            }

            ObservableList < String > filtered = allTypes.filtered(type -> !type.equalsIgnoreCase(newType));
            secondaryTypeComboBox.setItems(FXCollections.observableArrayList(filtered));

            String secondaryValue = secondaryTypeComboBox.getValue();
            if (secondaryValue != null && secondaryValue.equalsIgnoreCase(newType)) {
                secondaryTypeComboBox.setValue(null);
            }
        });
    }

    // Enable/disable secondary combo based on checkbox, clear selection when disabled
    private void setupSecondaryTypeCheckbox() {
        hasSecondaryTypeCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            secondaryTypeComboBox.setDisable(!isSelected);
            if (!isSelected) {
                secondaryTypeComboBox.getSelectionModel().clearSelection();
            }
        });

        // Initial state (in case checkbox default is unchecked)
        secondaryTypeComboBox.setDisable(!hasSecondaryTypeCheckBox.isSelected());
    }

    private String formatClassification(Move.Classification classification) {
        return switch (classification) {
            case HM -> "H.M.";
            case TM -> "T.M.";
        };
    }

    private StringConverter < Move.Classification > getClassificationConverter() {
        return new StringConverter < > () {
            @Override
            public String toString(Move.Classification classification) {
                return classification == null ? "" : formatClassification(classification);
            }

            @Override
            public Move.Classification fromString(String string) {
                if (string == null) return null;
                return switch (string.toUpperCase().replace(".", "")) {
                    case "HM" -> Move.Classification.HM;
                    case "TM" -> Move.Classification.TM;
                    default -> null;
                };
            }
        };
    }

    private void setupMovesTabButtons() {
        movesTabButtons = List.of(dashboardTab, addMoveTab, viewAllTab);

        for (JFXButton tab: movesTabButtons) {
            tab.setOnAction(e -> setActiveMovesTab(tab));
        }

        // Default active tab
        setActiveMovesTab(dashboardTab);
    }

    private void setActiveMovesTab(JFXButton activeTab) {
        // Update button styles for all move tabs
        for (JFXButton tab : movesTabButtons) {
            tab.getStyleClass().remove("active-tab");
        }
        if (!activeTab.getStyleClass().contains("active-tab")) {
            activeTab.getStyleClass().add("active-tab");
        }

        // Hide all move-related content panes first
        dashboardContentPane.setVisible(false);
        addMoveContentPane.setVisible(false);
        viewAllContentPane.setVisible(false);

        // Hide move-related tooltips if switching away from Add Move tab
        if (activeTab != addMoveTab) {
            hideAddMoveTooltips();
        }

        // Show the content pane corresponding to the active tab
        if (activeTab == dashboardTab) {
            dashboardContentPane.setVisible(true);
        } else if (activeTab == addMoveTab) {
            addMoveContentPane.setVisible(true);
        } else if (activeTab == viewAllTab) {
            viewAllContentPane.setVisible(true);
        }
    }


    private void hideAddMoveTooltips() {
        moveDescriptionTooltip.hide();
        moveNameFieldTooltip.hide();
        primaryTypeTooltip.hide();
        secondaryTypeTooltip.hide();
        classificationTooltip.hide();
    }

    private void addMoveDescriptionValidationListener() {
        moveDescription.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // lost focus
                moveDescriptionTouched = true;
                validateAndUpdateForm();
            }
        });

        moveDescription.textProperty().addListener((obs, oldText, newText) -> {
            if (!moveDescriptionTouched) return;
            validateAndUpdateForm();
        });
    }
    private boolean validateAndStyleMoveDescription() {
        String input = moveDescription.getText() == null ? "" : moveDescription.getText().trim();
        String errorMessage = null;

        if (input.isEmpty()) {
            errorMessage = "Description cannot be empty.";
        } else if (input.length() > 120) {
            errorMessage = "Description is too long. Keep it under 120 characters.";
        } else if (!input.matches("[A-Za-z0-9.,'\\-()/\\%\\s]+")) {
            errorMessage = "Invalid characters used. Allowed: letters, numbers, spaces, and . , - ' ( ) / %";
        } else if (!input.endsWith(".")) {
            errorMessage = "Description should end with a period.";
        }

        if (errorMessage == null) {
            moveDescription.setStyle("");
            moveDescriptionTooltip.hide();
            return true;
        } else {
            moveDescription.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            showTooltip(moveDescription, moveDescriptionTooltip, errorMessage);
            return false;
        }
    }

    private void addMoveNameFieldValidationListener() {
        moveNameField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // lost focus = touched
                moveNameFieldTouched = true;
                validateAndUpdateForm();
            }
        });

        moveNameField.textProperty().addListener((obs, oldText, newText) -> {
            if (!moveNameFieldTouched) return;
            validateAndUpdateForm();
        });
    }
    private boolean validateMoveNameField() {
        String input = moveNameField.getText() == null ? "" : moveNameField.getText().trim();

        if (input.isEmpty()) {
            showTooltip(moveNameField, moveNameFieldTooltip, "Name cannot be empty.");
            moveNameField.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }
        if (input.length() > 20) {
            showTooltip(moveNameField, moveNameFieldTooltip, "Name is too long. Maximum length is 20 characters.");
            moveNameField.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }
        if (!input.matches("[A-Za-z0-9.\\-\\s'()]+")) {
            showTooltip(moveNameField, moveNameFieldTooltip,
                    "Invalid characters in name. Only letters, numbers, dashes, apostrophes, periods, parentheses, and spaces are allowed.");
            moveNameField.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }
        if (moveManager.isMoveNameTaken(input)) {
            showTooltip(moveNameField, moveNameFieldTooltip, "This move name already exists. Please enter a different one.");
            moveNameField.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        moveNameField.setStyle("-fx-border-color: transparent;");
        moveNameFieldTooltip.hide();
        return true;
    }
    public void addTypeSelectionValidationListener() {
        // Primary ComboBox listeners
        primaryTypeComboBox.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) {
                primaryTypeTouched = true;
                validateAndUpdateForm();
            }
        });
        primaryTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!primaryTypeTouched) return;
            validateAndUpdateForm();
        });

        // Secondary ComboBox listeners
        secondaryTypeComboBox.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) {
                secondaryTypeTouched = true;
                validateAndUpdateForm();
            }
        });
        secondaryTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!secondaryTypeTouched) return;
            validateAndUpdateForm();
        });

        // Checkbox listener
        hasSecondaryTypeCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            hasSecondaryTouched = true;
            validateAndUpdateForm();
        });
    }
    private boolean validateTypeSelection() {
        boolean valid = true;

        // Validate primaryTypeComboBox
        if (primaryTypeTouched && (primaryTypeComboBox.getValue() == null || primaryTypeComboBox.getValue().isEmpty())) {
            primaryTypeComboBox.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            showTooltip(primaryTypeComboBox, primaryTypeTooltip, "Primary type must be selected.");
            valid = false;
        } else {
            primaryTypeComboBox.setStyle("-fx-border-color: transparent;");
            primaryTypeTooltip.hide();
        }

        // Validate secondaryTypeComboBox only if checkbox is checked
        if (hasSecondaryTouched && hasSecondaryTypeCheckBox.isSelected()) {
            if (secondaryTypeTouched && (secondaryTypeComboBox.getValue() == null || secondaryTypeComboBox.getValue().isEmpty())) {
                secondaryTypeComboBox.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
                showTooltip(secondaryTypeComboBox, secondaryTypeTooltip, "Secondary type must be selected when enabled.");
                valid = false;
            } else {
                secondaryTypeComboBox.setStyle("-fx-border-color: transparent;");
                secondaryTypeTooltip.hide();
            }
        } else {
            // Clear secondary styles if checkbox not selected
            secondaryTypeComboBox.setStyle("-fx-border-color: transparent;");
            secondaryTypeTooltip.hide();
        }

        return valid;
    }

    public void addComboClassificationValidationListener() {
        comboClassification.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // focus lost
                classificationTouched = true;
                validateAndUpdateForm();
            }
        });

        comboClassification.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!classificationTouched) return;
            validateAndUpdateForm();
        });
    }

    private boolean validateComboClassification() {
        if (classificationTouched && comboClassification.getValue() == null) {
            comboClassification.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            showTooltip(comboClassification, classificationTooltip, "Please select a classification.");
            return false;
        } else {
            comboClassification.setStyle("-fx-border-color: transparent;");
            classificationTooltip.hide();
            return true;
        }
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
    private void validateAndUpdateForm() {
        boolean isMoveNameValid = validateMoveNameField();
        boolean isClassificationValid = validateComboClassification();
        boolean isTypeSelectionValid = validateTypeSelection();
        boolean isMoveDescriptionValid = validateAndStyleMoveDescription();

        updateFormState(isMoveNameValid, isClassificationValid, isTypeSelectionValid, isMoveDescriptionValid);
    }
    private void updateFormState(boolean isMoveNameValid, boolean isClassificationValid,
                                 boolean isTypeSelectionValid, boolean isMoveDescriptionValid) {
        boolean formIsValid = isMoveNameValid && isClassificationValid &&
                isTypeSelectionValid && isMoveDescriptionValid;

        addButton.setDisable(!formIsValid);
    }

    public void setupTypeColumnCellFactory(TableColumn<Move, List<String>> typeColumn) {
        typeColumn.setCellFactory(col -> new TableCell<>() {
            private final VBox badgeContainer = new VBox(4);

            {
                badgeContainer.setPrefHeight(40);
                badgeContainer.setMaxHeight(40);
                badgeContainer.setMinHeight(40);
                badgeContainer.setFillWidth(true);
                badgeContainer.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(List<String> types, boolean empty) {
                super.updateItem(types, empty);
                if (empty || types == null || types.isEmpty()) {
                    setGraphic(null);
                } else {
                    badgeContainer.getChildren().clear();

                    for (String type : types) {
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
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }


    private ObservableValue<List<String>> getCombinedTypes(Move move) {
        ObservableList<String> types = FXCollections.observableArrayList();

        if (move.getPrimaryType() != null && !move.getPrimaryType().isEmpty()) {
            types.add(move.getPrimaryType());
        }
        if (move.getSecondaryType() != null && !move.getSecondaryType().isEmpty()) {
            types.add(move.getSecondaryType());
        }

        return new SimpleObjectProperty<>(types);
    }


    private void setupColumns() {
        // Set the cell value to the move's name
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        // Use the custom cell factory for showing move name + type icons
        nameColumn.setCellFactory(column -> new MoveTypeIconCell());

        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));
        setupWrappingTextColumn(descriptionColumn);

        classificationColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getClassification()));

        typeColumn.setCellValueFactory(cellData -> getCombinedTypes(cellData.getValue()));
        setupTypeColumnCellFactory(typeColumn);
    }


    private void setupWrappingTextColumn(TableColumn<Move, String> column) {
        column.setCellFactory(col -> new TableCell<>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(col.widthProperty().subtract(16)); // Adjust for padding
                text.getStyleClass().add("wrapped-text");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    setGraphic(text);
                }
            }
        });
    }

    public  void handleAddMoveButton(ActionEvent actionEvent)
    {
        try {
            String name = moveNameField.getText().trim();
            String description = moveDescription.getText().trim();
            Move.Classification classification = comboClassification.getValue();
            String primaryType = primaryTypeComboBox.getValue();
            String secondaryType = hasSecondaryTypeCheckBox.isSelected() ? secondaryTypeComboBox.getValue() : null;

            Move move = new Move(name, description, classification, primaryType, secondaryType);

            moveManager.addMove(move);
            JFXSnackbarLayout layout = new JFXSnackbarLayout("Move added: " + name);
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(layout, Duration.seconds(3)));

            // ♻️ Reset the form
            resetMoveFormFields();

        } catch (Exception e) {
            e.printStackTrace();

            JFXSnackbarLayout errorLayout = new JFXSnackbarLayout("Failed to add move. Please try again.");
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(errorLayout, Duration.seconds(3)));
        }

    }

    private void resetMoveFormFields() {
        moveNameField.clear();
        moveDescription.clear();

        comboClassification.getSelectionModel().clearSelection();

        primaryTypeComboBox.getSelectionModel().clearSelection();
        secondaryTypeComboBox.getSelectionModel().clearSelection();

        hasSecondaryTypeCheckBox.setSelected(false);

        // Hide all tooltips related to move inputs, if you have them
        moveNameFieldTooltip.hide();
        moveDescriptionTooltip.hide();
        primaryTypeTooltip.hide();
        secondaryTypeTooltip.hide();
        classificationTooltip.hide();

        // Disable add button until inputs are valid again (if applicable)
        addButton.setDisable(true);
    }


    public void handleResetMoveForm(ActionEvent actionEvent) {
        resetMoveFormFields();
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

    private void applyCombinedFilter() {
        String searchQuery = searchTextField.getText() == null ? "" : searchTextField.getText().toLowerCase().trim();
        String selectedType = typeFilterBox.getValue();

        filteredMoveList.setPredicate(move -> {
            // Match name or description
            boolean matchesSearch = move.getName().toLowerCase().contains(searchQuery)
                    || move.getDescription().toLowerCase().contains(searchQuery);

            // If no type is selected or "Any", ignore type filtering
            if (selectedType == null || selectedType.isEmpty() || selectedType.equalsIgnoreCase("Any")) {
                return matchesSearch;
            }

            // Check if selectedType matches type1 or type2
            boolean matchesType = move.getPrimaryType().equalsIgnoreCase(selectedType)
                    || (move.getSecondaryType() != null && move.getSecondaryType().equalsIgnoreCase(selectedType));

            return matchesSearch && matchesType;
        });
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

    public void updateMoveStats() {
        totalMoveLabel.setText("Total Moves: " + moveManager.getTotalMoveCount());
        totalTMLabel.setText("Total TMs: " + moveManager.getTotalTMCount());
        totalHMLabel.setText("Total HMs: " + moveManager.getTotalHMCount());
    }


}