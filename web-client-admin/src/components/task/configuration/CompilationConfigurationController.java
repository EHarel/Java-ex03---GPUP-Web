package components.task.configuration;

import components.app.GPUPAdmin;
import components.task.settings.TaskSettingsController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import logic.Engine;
import task.TaskType;
import task.configuration.*;

import javax.naming.NameNotFoundException;
import java.io.File;

public class CompilationConfigurationController {

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
    private TextField textFieldConfigName;
    @FXML
    private TextField sourcePathTextField;
    @FXML
    private TextField outputPathTextField;
    @FXML
    private Button chooseFolderSourceButton;
    @FXML
    private Button chooseFolderOutputButton;

    @FXML
    public void initialize() {
        setNameListener();
    }

    private void setNameListener() {
        TaskSettingsController.SetNameListener(textFieldConfigName, labelNameMsg);
    }

    @FXML
    void chooseFolderOutputButtonActionListener(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedFile = directoryChooser.showDialog(GPUPAdmin.getStage());
        System.out.println("[chooseFolderOutputButtonActionListener] Loading file.");

        if (selectedFile != null) {
            outputPathTextField.setText(selectedFile.getPath());
        }
    }

    @FXML
    void chooseFolderSourceButtonActionListener(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedFile = directoryChooser.showDialog(GPUPAdmin.getStage());
        System.out.println("[chooseFolderSourceButtonActionListener] Loading file.");

        if (selectedFile != null) {
            sourcePathTextField.setText(selectedFile.getPath());
        }
    }

    @FXML
    void sou(ActionEvent event) {

    }

    public ConfigurationCompilation getConfig(Integer threadNum) {
        ConfigurationCompilation compConfig = null;

        if (threadNum != null) {
            try {
                String name = getName();
                int numberOfThreads = threadNum;
                String sourceCodePath = getSourceCodePath();
                String outPath = getOutPath();

                compConfig = new ConfigurationCompilation(name, numberOfThreads, sourceCodePath, outPath);
            } catch (NameNotFoundException noName) {
                showNoNameError();
            } catch (Exception ignore) {
            }
        }

        return compConfig;
    }

    private void showNoNameError() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Invalid name");
        errorAlert.setContentText("Configuration must have a name");
        errorAlert.showAndWait();
    }

    private String getOutPath() {
        return outputPathTextField.getText();
    }

    private String getSourceCodePath() {
        return sourcePathTextField.getText();
    }

    private String getName() {
        return textFieldConfigName.getText();
    }

    public void loadConfig(String configName) {
        ConfigurationDataCompilation configData = (ConfigurationDataCompilation) Engine.getInstance().getConfigSpecific(TaskType.COMPILATION, configName);

        if (configData == null)
            return;

        textFieldConfigName.setText(configData.getName());
        sourcePathTextField.setText(configData.getSourceCodePath());
        outputPathTextField.setText(configData.getOutPath());
    }

    public void lockControls() {
        setControlsDisabled(true);
    }
    
    public void unlockControls() {
        setControlsDisabled(false);
    }
    
    private void setControlsDisabled(boolean isDisabled) {
        textFieldConfigName.setDisable(isDisabled);
        chooseFolderOutputButton.setDisable(isDisabled);
        chooseFolderSourceButton.setDisable(isDisabled);
    }

    public void openForNewConfig() {
        unlockControls();
        String currName = textFieldConfigName.getText();
        textFieldConfigName.setText(currName + " (New)");
    }

    public void clear() {
        textFieldConfigName.clear();
        sourcePathTextField.clear();
        outputPathTextField.clear();
    }
}