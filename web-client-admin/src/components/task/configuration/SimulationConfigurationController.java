package components.task.configuration;

import components.app.AppMainController;
import components.task.settings.TaskSettingsController;
import exception.InvalidInputRangeException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import task.enums.TaskType;
import task.configuration.ConfigurationDTOSimulation;
import task.configuration.ConfigurationSimulation;

import javax.naming.NameNotFoundException;
import java.util.function.UnaryOperator;

public class SimulationConfigurationController {

    private TaskSettingsController taskSettingsController;

    public void setTaskSettingsController(TaskSettingsController taskSettingsController) {
        this.taskSettingsController = taskSettingsController;

        taskSettingsController.getIsEditingNewConfigProperty().addListener(((observable, oldValue, newValue) -> {
            setControlsDisabled(!newValue);
        }));
    }

    @FXML
    private Label labelNameMsg;
    @FXML
    private Label labelProcessingTimeMsg;
    @FXML
    private Label labelWarningsProbabilityMsg;
    @FXML
    private Label labelSuccessProbabilityMsg;


    @FXML
    private TextField textFieldConfigName;
    @FXML
    private RadioButton randomRadioButton;
    @FXML
    private ToggleGroup RandomTimeToggleGroup;
    @FXML
    private RadioButton notRandomRadioButton;
    @FXML
    private TextField processingTimeTextField;
    @FXML
    private TextField warningsProbabilityTextField;
    @FXML
    private TextField successProbabilityTextField;

    @FXML
    public void initialize() {
        setNameListener();
        setProcessingTimeFormatter(labelProcessingTimeMsg);
        setProcessingTimeFormatterProbability(warningsProbabilityTextField, labelWarningsProbabilityMsg);
        setProcessingTimeFormatterProbability(successProbabilityTextField, labelSuccessProbabilityMsg);
    }

    private void setNameListener() {
        TaskSettingsController.SetNameListener(textFieldConfigName, labelNameMsg);
    }

    private void setProcessingTimeFormatterProbability(TextField probabilityTextField, Label label) {
        UnaryOperator<TextFormatter.Change> probabilityFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("0(\\.\\d+)?|1(\\.0+)?")) {
                label.setText("Valid input :)");
                label.setTextFill(Color.GREEN);
            } else {
                label.setText("Invalid input! Must be a number between 0 and 1");
                label.setTextFill(Color.RED);
            }

            return change;
        };

        probabilityTextField.setTextFormatter(
                new TextFormatter<Float>(new FloatStringConverter(), 0f, probabilityFilter));
    }

    private void setProcessingTimeFormatter(Label label) {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
//            if (newText.matches("([1-9][0-9]*)?")) {
            if (newText.matches("([0-9]*)") && isSecondsInMilliseconds()) {
                label.setText("Valid processing time :)");
                label.setTextFill(Color.GREEN);
            } else {
                label.setText("Invalid input! Must be seconds given in millisecond (e.g. 34000 good, 34500 bad)");
                label.setTextFill(Color.RED);
            }

            return change;
        };

        processingTimeTextField.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
    }

    private boolean isSecondsInMilliseconds() {
        boolean isValid;

        try {
            int num = Integer.parseInt(processingTimeTextField.getText());
            isValid = ConfigurationSimulation.isValidProcessingTime(num);
        } catch (NumberFormatException exc) {
            isValid = false;
        }

        return isValid;
    }

    public ConfigurationSimulation getConfig() {
        ConfigurationSimulation simConfig = null;

        try {
            String name = getName();
            int numberOfThreads = 1;
            double successProbability = getSuccessProbability();
            int processingTime = getProcessingTime();
            boolean randomProcessingTime = getIsRandomProcessingTime();
            double warningsProbability = getWarningsProbability();

            simConfig = new ConfigurationSimulation(name, numberOfThreads, successProbability, processingTime, randomProcessingTime, warningsProbability);
        } catch (InvalidInputRangeException ignore) {
        } catch (NumberFormatException exception) {
            showNumberError();
        } catch (NameNotFoundException e) {
            showNoNameError();
        }

        return simConfig;
    }

    private void showNoNameError() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Invalid name");
        errorAlert.setContentText("Configuration must have a name");
        errorAlert.showAndWait();
    }

    private void showNumberError() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Invalid number");
        errorAlert.setContentText("One of the number parameters is invalid");
        errorAlert.showAndWait();
    }

    private String getName() {
        return textFieldConfigName.getText();
    }

    private double getSuccessProbability() throws NullPointerException, NumberFormatException {
        String doubleStr = successProbabilityTextField.getText();

        return Double.parseDouble(doubleStr);
    }

    private double getWarningsProbability() {
        String doubleStr = warningsProbabilityTextField.getText();

        return Double.parseDouble(doubleStr);
    }

    private int getProcessingTime() throws NullPointerException, NumberFormatException {
        String processingTimeStr = processingTimeTextField.getText();

        return Integer.parseInt(processingTimeStr);
    }

    private boolean getIsRandomProcessingTime() {
        return randomRadioButton.isSelected();
    }

    public void loadConfig(String configName, AppMainController mainController) {
//        ConfigurationDataSimulation configData = (ConfigurationDataSimulation) Engine.getInstance().getConfigSpecific(TaskType.SIMULATION, configName);
        ConfigurationDTOSimulation configData = (ConfigurationDTOSimulation) mainController.getConfigDataSpecific(TaskType.SIMULATION, configName);

        if (configData == null)
            return;

        textFieldConfigName.setText(configData.getName());
        successProbabilityTextField.setText(String.valueOf(configData.getSuccessProbability()));
        warningsProbabilityTextField.setText(String.valueOf(configData.getWarningsProbability()));
        processingTimeTextField.setText(String.valueOf(configData.getProcessingTime()));

        if (RandomTimeToggleGroup.getSelectedToggle() != null) {
            RandomTimeToggleGroup.getSelectedToggle().setSelected(false);
        }

        if (configData.isRandomProcessingTime()) {
            randomRadioButton.setSelected(true);
        } else {
            notRandomRadioButton.setSelected(true);
        }
    }

    public void lockControls() {
        setControlsDisabled(true);
    }

    public void unlockControls() {
        setControlsDisabled(false);
    }

    private void setControlsDisabled(boolean isDisabled) {
        textFieldConfigName.setDisable(isDisabled);
        randomRadioButton.setDisable(isDisabled);
        notRandomRadioButton.setDisable(isDisabled);
        processingTimeTextField.setDisable(isDisabled);
        warningsProbabilityTextField.setDisable(isDisabled);
        successProbabilityTextField.setDisable(isDisabled);
    }

    public void openForNewConfig() {
        unlockControls();
        String currName = textFieldConfigName.getText();
        String newName = currName + " (New)";
        newName = newName.trim();

        textFieldConfigName.setText(newName);
    }

    public void clear() {
        textFieldConfigName.clear();
        randomRadioButton.setSelected(false);
        notRandomRadioButton.setSelected(false);
        processingTimeTextField.clear();
        warningsProbabilityTextField.clear();
        successProbabilityTextField.clear();
    }
}