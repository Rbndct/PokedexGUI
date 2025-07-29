package com.rbee.pokedexgui.controller.trainer;

import com.jfoenix.controls.*;
import com.rbee.pokedexgui.app.MainApp;
import com.rbee.pokedexgui.manager.ActiveTrainerHolder;
import com.rbee.pokedexgui.manager.TrainerManager;
import com.rbee.pokedexgui.model.trainer.Trainer;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class TrainerViewController implements Initializable {

    public Button btnSetActiveTrainer;
    private TrainerManager trainerManager;

    @FXML private TableView<Trainer> trainerTableView;
    @FXML private TableColumn<Trainer, Integer> idColumn;
    @FXML private TableColumn<Trainer, String> nameColumn;
    @FXML private TableColumn<Trainer, String> sexColumn;
    @FXML private TableColumn<Trainer, String> hometownColumn;
    @FXML private TableColumn<Trainer, String> descriptionColumn;
    @FXML private TableColumn<Trainer, String> moneyColumn;
    @FXML private TableColumn<Trainer, Void> manageColumn;

    /* Snackbar Containers */
    @FXML private StackPane contentStackPane;
    @FXML private HBox snackbarContainer;
    @FXML private JFXSnackbar snackbar;

    @FXML private TextField trainerNameField;

    @FXML private DatePicker birthDateField;

    @FXML private JFXComboBox<String> sexComboBox;

    @FXML private JFXComboBox<String> hometownComboBox;

    @FXML private JFXTextArea trainerDescription;

    @FXML private JFXButton clearButton;

    @FXML private JFXButton addButton;

    /* Navigation / Tab Buttons */
    @FXML private JFXButton trainerDashboardTab;
    @FXML private JFXButton addTrainerTab;
    @FXML private JFXButton viewTrainerTab;

    /* Layout Containers */
    @FXML private Pane trainerDashboardContentPane;
    @FXML private ScrollPane addTrainerContentPane;
    @FXML private VBox viewTrainerContentPane;

    private List<Button> trainerTabButtons;

    private FilteredList<Trainer> filteredList;

    @FXML
    private TextField searchTextField;

    @FXML
    private JFXComboBox<String> hometownFilterBox;

    @FXML
    private JFXButton resetFiltersButton;


    // Tooltips for validation feedback
    private final Tooltip trainerNameTooltip = new Tooltip();
    private final Tooltip sexComboBoxTooltip = new Tooltip();
    private final Tooltip trainerDescriptionTooltip = new Tooltip();
    private final Tooltip hometownComboBoxTooltip = new Tooltip();
    private final Tooltip birthDateTooltip = new Tooltip();


    // Flags to track if fields have been touched
    private boolean trainerNameTouched = false;
    private boolean sexComboBoxTouched = false;
    private boolean trainerDescriptionTouched = false;
    private boolean hometownComboBoxTouched = false;
    private boolean birthDateFieldTouched = false;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize manager and data structures
        this.trainerManager = new TrainerManager();
        trainerTableView.setFixedCellSize(-1);

        // Initialize observable filtered list wrapping the master list
        filteredList = new FilteredList<>(trainerManager.getTrainerList(), t -> true);
        trainerTableView.setItems(filteredList);

        setupTrainerTable();

        // Refresh the table so it redraws with proper wrapping and row heights
        trainerTableView.refresh();

        populateHometownComboBox();
        populateSexComboBox();
        restrictBirthDateRange(birthDateField);
        birthDateField.setEditable(false); // Prevent manual input
        setupTabButtons();
        setupFilters();

        // Disable add button initially
        addButton.setDisable(true);

        // Setup snackbar for user notifications
        snackbar = new JFXSnackbar(contentStackPane);
        snackbarContainer.setVisible(false);

        addTrainerNameFieldValidationListener();
        addSexComboBoxValidationListener();
        addTrainerDescriptionValidationListener();
        addHometownComboBoxValidationListener();
        addBirthDateValidationListener();
        setupActiveTrainerSelection();
    }


    private void addTrainerNameFieldValidationListener() {
        // Focus listener to mark "touched" for trainerNameField
        trainerNameField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // lost focus
                trainerNameTouched = true;
                validateAndUpdateForm();
            }
        });

        // Text change listener — only validate if already touched
        trainerNameField.textProperty().addListener((obs, oldText, newText) -> {
            if (!trainerNameTouched) return;
            validateAndUpdateForm();
        });
    }

    private boolean validateTrainerNameField() {
        // Don't show error if field not touched yet
        if (!trainerNameTouched) {
            trainerNameField.setStyle("-fx-border-color: transparent;");
            trainerNameTooltip.hide();
            return true;
        }

        String input = trainerNameField.getText() == null ? "" : trainerNameField.getText().trim();

        // Check if empty
        if (input.isEmpty()) {
            showTooltip(trainerNameField, trainerNameTooltip, "Name cannot be empty.");
            trainerNameField.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        // Length check
        if (input.length() < 3 || input.length() > 29) {
            showTooltip(trainerNameField, trainerNameTooltip, "Name must be 3-29 characters long.");
            trainerNameField.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        // Allowed characters check
        if (!input.matches("[a-zA-Z\\-\\' ]+")) {
            showTooltip(trainerNameField, trainerNameTooltip,
                    "Only letters, spaces, hyphens, and apostrophes allowed.");
            trainerNameField.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        // Corrected uniqueness check: if name is NOT unique, show error
        if (!trainerManager.isTrainerNameUnique(input)) {
            showTooltip(trainerNameField, trainerNameTooltip,
                    "This Trainer name already exists. Please choose a different name.");
            trainerNameField.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        // Valid input
        trainerNameField.setStyle("-fx-border-color: transparent;");
        trainerNameTooltip.hide();
        return true;
    }

    private void addSexComboBoxValidationListener() {
        // Focus listener to mark "touched"
        sexComboBox.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // lost focus
                sexComboBoxTouched = true;
                validateAndUpdateForm();
            }
        });

        // Value change listener — only validate if already touched
        sexComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!sexComboBoxTouched) return;
            validateAndUpdateForm();
        });
    }
    private boolean validateSexComboBox() {
        // Only validate if touched
        if (!sexComboBoxTouched) {
            sexComboBox.setStyle("-fx-border-color: transparent;");
            sexComboBoxTooltip.hide();
            return true;
        }

        String value = sexComboBox.getValue();
        if (value == null || value.trim().isEmpty()) {
            showTooltip(sexComboBox, sexComboBoxTooltip, "Please select a sex.");
            sexComboBox.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        sexComboBox.setStyle("-fx-border-color: transparent;");
        sexComboBoxTooltip.hide();
        return true;
    }
    private void addTrainerDescriptionValidationListener() {
        trainerDescription.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // lost focus
                trainerDescriptionTouched = true;
                validateAndUpdateForm();
            }
        });

        trainerDescription.textProperty().addListener((obs, oldText, newText) -> {
            if (!trainerDescriptionTouched) return;
            validateAndUpdateForm();
        });
    }
    private boolean validateAndStyleTrainerDescription() {
        // Don't validate unless touched
        if (!trainerDescriptionTouched) {
            trainerDescription.setStyle("-fx-border-color: transparent;");
            trainerDescriptionTooltip.hide();
            return true;
        }

        String input = trainerDescription.getText() == null ? "" : trainerDescription.getText().trim();
        String errorMessage = null;

        if (input.isEmpty()) {
            errorMessage = "Description cannot be empty.";
        } else if (input.length() < 10) {
            errorMessage = "Description is too short. Minimum 10 characters.";
        } else if (input.length() > 250) {
            errorMessage = "Description is too long. Keep it under 250 characters.";
        } else if (!input.matches("[A-Za-z0-9.,'\"\\-()!?%\\s]+")) {
            errorMessage = "Invalid characters used. Allowed: letters, numbers, spaces, and . , ' \" - ( ) ! ? %";
        } else if (!input.endsWith(".")) {
            errorMessage = "Description should end with a period.";
        }

        if (errorMessage == null) {
            trainerDescription.setStyle("-fx-border-color: transparent;");
            trainerDescriptionTooltip.hide();
            return true;
        } else {
            trainerDescription.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            showTooltip(trainerDescription, trainerDescriptionTooltip, errorMessage);
            return false;
        }
    }

    private void validateAndUpdateForm() {
        boolean isTrainerNameValid = validateTrainerNameField();
        boolean isSexValid = validateSexComboBox();
        boolean isTrainerDescriptionValid = validateAndStyleTrainerDescription();
        boolean isHometownValid = validateHometownComboBox();
        boolean isBirthDateValid = validateBirthDateField();

        updateFormState(isTrainerNameValid, isSexValid, isTrainerDescriptionValid, isHometownValid, isBirthDateValid);
    }

    private void updateFormState(boolean isTrainerNameValid, boolean isSexValid,
                                 boolean isTrainerDescriptionValid, boolean isHometownValid,
                                 boolean isBirthDateValid) {
        boolean formIsValid = isTrainerNameValid && isSexValid && isTrainerDescriptionValid
                && isHometownValid && isBirthDateValid;
        addButton.setDisable(!formIsValid);
    }

    private void populateSexComboBox() {
        sexComboBox.getItems().addAll("Male", "Female");
    }
    private void populateHometownComboBox() {
        hometownComboBox.getItems().addAll(
                // Gen I – Kanto
                "Pallet Town",

                // Gen II – Johto
                "New Bark Town",

                // Gen III – Hoenn
                "Littleroot Town",

                // Gen IV – Sinnoh
                "Twinleaf Town",

                // Gen V – Unova
                "Nuvema Town",

                // Gen VI – Kalos
                "Vaniville Town",

                // Gen VII – Alola
                "Iki Town",
                "Hau'oli City",

                // Gen VIII – Galar
                "Postwick",
                "Wedgehurst",

                // Gen IX – Paldea
                "Cabo Poco",
                "Mesagoza"
        );
    }
    private void addHometownComboBoxValidationListener() {
        // Focus listener to mark "touched"
        hometownComboBox.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // lost focus
                hometownComboBoxTouched = true;
                validateAndUpdateForm();
            }
        });

        // Value change listener — only validate if already touched
        hometownComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!hometownComboBoxTouched) return;
            validateAndUpdateForm();
        });
    }
    private boolean validateHometownComboBox() {
        if (!hometownComboBoxTouched) {
            hometownComboBox.setStyle("-fx-border-color: transparent;");
            hometownComboBoxTooltip.hide();
            return true;
        }

        String value = hometownComboBox.getValue();
        if (value == null || value.trim().isEmpty()) {
            showTooltip(hometownComboBox, hometownComboBoxTooltip, "Please select a hometown.");
            hometownComboBox.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        hometownComboBox.setStyle("-fx-border-color: transparent;");
        hometownComboBoxTooltip.hide();
        return true;
    }

    public void restrictBirthDateRange(DatePicker datePicker) {
        LocalDate minDate = LocalDate.of(1996, 1, 1);
        LocalDate maxDate = LocalDate.now();

        datePicker.setDayCellFactory(createDateLimiter(minDate, maxDate));
        datePicker.setPromptText("Select birthdate");
    }
    public void addBirthDateValidationListener() {
        birthDateField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) { // lost focus
                birthDateFieldTouched = true;
                validateAndUpdateForm();
            }
        });

        birthDateField.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!birthDateFieldTouched) return;
            validateAndUpdateForm();
        });
    }
    private boolean validateBirthDateField() {
        if (!birthDateFieldTouched) {
            birthDateField.setStyle("-fx-border-color: transparent;");
            birthDateTooltip.hide();
            return true;
        }

        LocalDate selectedDate = birthDateField.getValue();
        LocalDate minDate = LocalDate.of(1996, 1, 1);
        LocalDate maxDate = LocalDate.now();

        if (selectedDate == null) {
            showTooltip(birthDateField, birthDateTooltip, "Please select a birthdate.");
            birthDateField.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        if (selectedDate.isBefore(minDate) || selectedDate.isAfter(maxDate)) {
            showTooltip(birthDateField, birthDateTooltip,
                    String.format("Date must be between %s and %s.", minDate, maxDate));
            birthDateField.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
            return false;
        }

        birthDateField.setStyle("-fx-border-color: transparent;");
        birthDateTooltip.hide();
        return true;
    }
    private Callback<DatePicker, DateCell> createDateLimiter(LocalDate minDate, LocalDate maxDate) {
        return (DatePicker picker) -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(minDate) || item.isAfter(maxDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #dddddd; -fx-opacity: 0.6;");
                }
            }
        };
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
    private void setupTabButtons() {
        trainerTabButtons = List.of(trainerDashboardTab, addTrainerTab, viewTrainerTab);

        for (Button tab: trainerTabButtons) {
            tab.setOnAction(e -> setActiveTrainerTab(tab));
        }
        setActiveTrainerTab(trainerDashboardTab); // default active tab
    }

    private void setActiveTrainerTab(Button activeTab) {
        // Update button styles
        for (Button tab : trainerTabButtons) {
            tab.getStyleClass().remove("active-tab");
        }
        if (!activeTab.getStyleClass().contains("active-tab")) {
            activeTab.getStyleClass().add("active-tab");
        }

        // Hide all trainer content panes
        trainerDashboardContentPane.setVisible(false);
        addTrainerContentPane.setVisible(false);
        viewTrainerContentPane.setVisible(false);

        // Hide tooltips when switching away from Add Trainer
        if (activeTab != addTrainerTab) {
            hideAddTrainerTooltips();
        }

        // Show the correct content pane
        if (activeTab == trainerDashboardTab) {
            trainerDashboardContentPane.setVisible(true);
        } else if (activeTab == addTrainerTab) {
            addTrainerContentPane.setVisible(true);
        } else if (activeTab == viewTrainerTab) {
            viewTrainerContentPane.setVisible(true);
        }
    }
    private void hideAddTrainerTooltips() {
        trainerNameTooltip.hide();
        sexComboBoxTooltip.hide();
        trainerDescriptionTooltip.hide();
        hometownComboBoxTooltip.hide();
        birthDateTooltip.hide();
    }
    private  void resetFormFields() {
        trainerNameField.clear();
        birthDateField.setValue(null);
        sexComboBox.getSelectionModel().clearSelection();
        hometownComboBox.getSelectionModel().clearSelection();
        trainerDescription.clear();


        hideAddTrainerTooltips();
    }
    public  void handleResetTrainerForm(ActionEvent actionEvent) {
        resetFormFields();
    }
    public void handleAddTrainer(ActionEvent actionEvent) {
        try {
            String name = trainerNameField.getText().trim();
            LocalDate birthdate = birthDateField.getValue();
            String sexValue = sexComboBox.getValue();
            String hometown = hometownComboBox.getValue();
            String description = trainerDescription.getText().trim();

            // Convert sexValue string to Sex enum
            Trainer.Sex sex = Trainer.Sex.valueOf(sexValue.toUpperCase());

            // Create Trainer instance
            Trainer trainer = new Trainer(name, birthdate, sex, hometown, description);

            // Add trainer to your manager (assuming you have one)
            trainerManager.addTrainer(trainer);

            // Show success snackbar
            JFXSnackbarLayout layout = new JFXSnackbarLayout("Trainer added successfully: " + name);
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(layout, Duration.seconds(3)));

            // Reset form fields
            resetFormFields();

        } catch (IllegalArgumentException | NullPointerException ex) {
            // Handle invalid enum conversion or null values
            JFXSnackbarLayout errorLayout = new JFXSnackbarLayout("Invalid or incomplete data. Please check all fields.");
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(errorLayout, Duration.seconds(3)));
        } catch (Exception e) {
            e.printStackTrace();
            JFXSnackbarLayout errorLayout = new JFXSnackbarLayout("Failed to add Trainer. Please try again.");
            snackbar.enqueue(new JFXSnackbar.SnackbarEvent(errorLayout, Duration.seconds(3)));
        }
    }

    private void setupTrainerTable() {
        // ID column: display trainerId (int)
        idColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getTrainerId()).asObject());

        // Name column: display name (String)
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        // Sex column: display sex enum as String
        sexColumn.setCellValueFactory(cellData -> {
            String symbol = switch (cellData.getValue().getSex()) {
                case MALE -> "\u2642";  // ♂
                case FEMALE -> "\u2640"; // ♀
            };
            return new SimpleStringProperty(symbol);
        });

        // Hometown column: display hometown (String)
        hometownColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getHometown()));

        // Description column: display description (String) with wrapping
        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription())
        );
        setupWrappingTextColumn(descriptionColumn);


        moneyColumn.setCellValueFactory(cellData ->
                cellData.getValue().moneyProperty().asString("₽%,.2f")
        );

        manageColumn.setText(null);  // clear text

        // Set cell factory for manage column
        manageColumn.setCellFactory(createManageButtonCellFactory());
    }


    private Callback<TableColumn<Trainer, Void>, TableCell<Trainer, Void>> createManageButtonCellFactory() {
        return col -> new TableCell<Trainer, Void>() {
            private final JFXButton manageBtn = new JFXButton();

            {
                Image wrenchImage = new Image(getClass().getResourceAsStream("/com/rbee/pokedexgui/images/wrench-icon.png"),
                        16, 16, true, true);
                ImageView wrenchView = new ImageView(wrenchImage);
                manageBtn.setGraphic(wrenchView);
                manageBtn.getStyleClass().add("icon-button");
                manageBtn.setTooltip(new Tooltip("Manage Trainer"));

                manageBtn.setOnAction(e -> {
                    Trainer trainer = getTableView().getItems().get(getIndex());
                    if (trainer != null) {
                        openManageTrainer(trainer);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : manageBtn);
            }
        };
    }

    private void openManageTrainer(Trainer trainer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rbee/pokedexgui/view/module/trainer/ManageTrainer.fxml"));
            Parent root = loader.load();

            ManageTrainerController controller = loader.getController();

            // Inject any dependencies or managers you need here
            controller.setTrainerManager(this.trainerManager);  // assuming you have this field
            controller.setTrainer(trainer);  // pass the selected trainer

            Stage stage = new Stage();
            stage.setTitle(trainer.getName() + " - Manage Trainer");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Makes it a modal dialog
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setupWrappingTextColumn(TableColumn<Trainer, String> column) {
        column.setCellFactory(col -> new TableCell<Trainer, String>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(col.widthProperty().subtract(16));
                text.getStyleClass().add("wrapped-text");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // Extract substring until first period (including the period)
                    int periodIndex = item.indexOf('.');
                    String displayedText = (periodIndex != -1) ? item.substring(0, periodIndex + 1) : item;

                    text.setText(displayedText);
                    setGraphic(text);
                }
            }
        });
    }



    private void setupFilters() {
        // Populate filter box with "All" + hometowns
        hometownFilterBox.setItems(FXCollections.observableArrayList(
                "All",
                "Pallet Town",
                "New Bark Town",
                "Littleroot Town",
                "Twinleaf Town",
                "Nuvema Town",
                "Vaniville Town",
                "Iki Town",
                "Hau'oli City",
                "Postwick",
                "Wedgehurst",
                "Cabo Poco",
                "Mesagoza"
        ));
        hometownFilterBox.getSelectionModel().select("All");

        // Listen for changes
        searchTextField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        hometownFilterBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Reset button
        resetFiltersButton.setOnAction(e -> {
            searchTextField.clear();
            hometownFilterBox.getSelectionModel().select("All");
        });
    }
    private void applyFilters() {
        String keyword = searchTextField.getText().toLowerCase().trim();
        String selectedHometown = hometownFilterBox.getValue();

        filteredList.setPredicate(trainer -> {
            boolean matchesSearch = keyword.isEmpty()
                    || String.valueOf(trainer.getTrainerId()).contains(keyword)
                    || trainer.getName().toLowerCase().contains(keyword);

            boolean matchesHometown = selectedHometown == null || selectedHometown.equals("All")
                    || trainer.getHometown().equalsIgnoreCase(selectedHometown);

            return matchesSearch && matchesHometown;
        });
    }

    private void setupActiveTrainerSelection() {
        btnSetActiveTrainer.setDisable(true);

        // Enable the button only when a trainer is selected
        trainerTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            btnSetActiveTrainer.setDisable(newSel == null);
        });

        btnSetActiveTrainer.setOnAction(e -> {
            Trainer selectedTrainer = trainerTableView.getSelectionModel().getSelectedItem();
            if (selectedTrainer != null) {
                // 1. Set the active trainer in ActiveTrainerHolder singleton
                ActiveTrainerHolder.setActiveTrainer(selectedTrainer);

                // 2. Update the main app window title to show active trainer name
                MainApp.setActiveTrainerTitle(selectedTrainer.getName());

                // 3. Show success snackbar notification
                JFXSnackbarLayout layout = new JFXSnackbarLayout("Active trainer set: " + selectedTrainer.getName());
                snackbar.enqueue(new JFXSnackbar.SnackbarEvent(layout, Duration.seconds(3)));
            } else {
                // Show error snackbar if no trainer selected
                JFXSnackbarLayout layout = new JFXSnackbarLayout("No trainer selected.");
                snackbar.enqueue(new JFXSnackbar.SnackbarEvent(layout, Duration.seconds(3)));
            }
        });
    }




}
