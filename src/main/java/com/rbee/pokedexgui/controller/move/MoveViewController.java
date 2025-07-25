package com.rbee.pokedexgui.controller.move;

import com.jfoenix.controls.*;
import com.rbee.pokedexgui.manager.MoveManager;
import com.rbee.pokedexgui.model.move.Move;
import com.rbee.pokedexgui.util.TypeUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MoveViewController implements Initializable {
    @FXML
    private TextField moveNameField;

    @FXML
    private JFXComboBox<Move.Classification> comboClassification;


    @FXML
    private JFXComboBox<String> primaryTypeComboBox;

    @FXML
    private JFXComboBox<String> secondaryTypeComboBox;

    @FXML
    private JFXCheckBox hasSecondaryTypeCheckBox;

    @FXML
    private JFXTextArea moveDescription;

    @FXML
    private JFXButton clearButton;

    @FXML
    private JFXButton addButton;

    @FXML private JFXButton dashboardTab;
    @FXML private JFXButton addMoveTab;
    @FXML private JFXButton viewAllTab;
    private List<JFXButton> movesTabButtons;

    private ObservableList<String> allTypes;


    @FXML private Pane dashboardContentPane;
    @FXML private ScrollPane addMoveContentPane;
    @FXML private VBox viewAllContentPane;
    private final MoveManager moveManager = new MoveManager();

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboClassification.getItems().addAll(Move.Classification.values());
        comboClassification.setConverter(getClassificationConverter());

        comboClassification.setCellFactory(listView -> new JFXListCell<>() {
            @Override
            protected void updateItem(Move.Classification item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatClassification(item));
            }
        });



        initializeTypeLists();
        setupTypeInputComboBox(primaryTypeComboBox);
        setupTypeInputComboBox(secondaryTypeComboBox);
        setupPrimaryTypeListener();
        setupSecondaryTypeCheckbox();

        setupMovesTabButtons();


        addMoveNameFieldValidationListener();
        addMoveDescriptionValidationListener();
        addTypeSelectionValidationListener();
        addComboClassificationValidationListener();

    }

    // Initialize allTypes list with capitalized type names
    private void initializeTypeLists() {
        allTypes = FXCollections.observableArrayList(
                Arrays.stream(TypeUtils.getValidTypes())
                        .map(type -> type.substring(0,1).toUpperCase() + type.substring(1))
                        .collect(Collectors.toList())
        );
    }

    // Setup combo box with prompt and custom cells
    private void setupTypeInputComboBox(JFXComboBox<String> comboBox) {
        comboBox.setItems(FXCollections.observableArrayList(allTypes)); // fresh copy
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

    // Filter secondary types based on primary type selection
    private void setupPrimaryTypeListener() {
        primaryTypeComboBox.valueProperty().addListener((obs, oldType, newType) -> {
            if (newType == null) {
                secondaryTypeComboBox.setItems(FXCollections.observableArrayList(allTypes));
                return;
            }

            ObservableList<String> filtered = allTypes.filtered(type -> !type.equalsIgnoreCase(newType));
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

    private StringConverter<Move.Classification> getClassificationConverter() {
        return new StringConverter<>() {
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

        for (JFXButton tab : movesTabButtons) {
            tab.setOnAction(e -> setActiveMovesTab(tab));
        }

        // Default active tab
        setActiveMovesTab(dashboardTab);
    }

    private void setActiveMovesTab(JFXButton activeTab) {
        for (JFXButton tab : movesTabButtons) {
            tab.getStyleClass().remove("active-tab");
        }
        if (!activeTab.getStyleClass().contains("active-tab")) {
            activeTab.getStyleClass().add("active-tab");
        }

        dashboardContentPane.setVisible(false);
        addMoveContentPane.setVisible(false);
        viewAllContentPane.setVisible(false);

        if (activeTab == dashboardTab) {
            dashboardContentPane.setVisible(true);
        } else if (activeTab == addMoveTab) {
            addMoveContentPane.setVisible(true);
        } else if (activeTab == viewAllTab) {
            viewAllContentPane.setVisible(true);
        }
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
}
