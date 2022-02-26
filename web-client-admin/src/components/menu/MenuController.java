package components.menu;

import components.app.AppMainController;
import components.css.Themes;
import events.ExecutionEndListener;
import events.ExecutionStartListener;
import events.FileLoadedListener;
import events.GraphChosenListener;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import task.Execution;
import task.configuration.Configuration;

public class MenuController implements ExecutionStartListener, ExecutionEndListener, FileLoadedListener, GraphChosenListener {
    private AppMainController mainController;

    @FXML private Button dashboardButton;
    @FXML private Button taskButton;
    @FXML private Button graphDetailsButton;
    @FXML private Button taskExecutionButton;
    @FXML private Button loadFileButton;
    @FXML private Label labelFileLoadMsg;
    @FXML private ComboBox<Themes> themesComboBox;
    @FXML private ToggleButton buttonDisableAnimations;

    private Button currentHighlightedButton;
    private Button newlyClickedButton;


    @FXML
    public void initialize() {
        themesComboBox.getItems().removeAll(themesComboBox.getItems());
        themesComboBox.getItems().addAll(Themes.Regular, Themes.Dark, Themes.Blue);
        themesComboBox.getSelectionModel().select(Themes.Regular);
    }



    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public ComboBox<Themes> getThemesComboBox() { return themesComboBox; }

    public ToggleButton getButtonDisableAnimations() { return buttonDisableAnimations; }

    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
        mainController.addEventListener_ExecutionStarted(this);
        mainController.addEventListener_ExecutionEnded(this);
        mainController.addEventListener_FileLoaded(this);
        mainController.addEventListener_GraphChosen(this);
    }

    @FXML
    void dashboardButtonActionListener(ActionEvent event) {
        mainController.dashboardButtonPressed();
    }

    @FXML
    void loadFileButtonActionListener(ActionEvent event) {
        mainController.loadFile();
        if (mainController.getAllowAnimations()) {
            rotateButton(taskButton);
        }
    }

    @FXML
    void taskButtonActionListener(ActionEvent event) {
        mainController.taskButtonPressed();
        if (mainController.getAllowAnimations()) {
            rotateButton(taskButton);
        }
    }

    @FXML
    void graphDetailsButtonActionListener(ActionEvent event) {
        this.mainController.graphDetailsButtonGeneralPressed();
        if (mainController.getAllowAnimations()) {
            rotateButton(graphDetailsButton);
        }
    }

    public void highlightTable() {

    }

    @FXML
    void taskExecutionButtonActionListener(ActionEvent event) {
        this.mainController.taskExecutionButtonPressed();
        if (mainController.getAllowAnimations()) {
            rotateButton(taskExecutionButton);
        }
    }

    private void rotateButton(Button taskExecutionButton) {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), taskExecutionButton);
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(360);
        rotateTransition.setCycleCount(1);

        rotateTransition.play();
    }

    @Override
    public void executionStarted(Configuration startingConfig) {
        loadFileButton.disableProperty().set(true);
    }

    @Override
    public void executedEnded(Execution execution) {
        loadFileButton.disableProperty().set(false);
    }

//    public void disableTaskSettingsButton() {
//        taskButton.disableProperty().set(true);
//    }

    public void enableTaskSettingsButton() {
        taskButton.disableProperty().set(false);
    }

    @Override
    public void graphChosen() {
        String msg = "Graph chosen!";
        Color color = Color.GREEN;
        AppMainController.AnimationFadeSingle(null, labelFileLoadMsg, msg, color);
        enableTaskSettingsButton();
    }

    @Override
    public void fileLoaded() {
        String msg = "File loaded!";
        Color color = Color.GREEN;
        AppMainController.AnimationFadeSingle(null, labelFileLoadMsg, msg, color);
    }

    public void failedToLoad() {
        String msg = "Failed to load :(";
        Color color = Color.RED;
        AppMainController.AnimationFadeSingle(null, labelFileLoadMsg, msg, color);
    }

    private void buttonClicked() {
    }
}
