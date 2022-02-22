package components.menu;

import components.app.AppMainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuWorkerController {

    @FXML
    private Button button_Dashboard;

    @FXML
    private Button button_ExecutionAndTaskPanel;



    private AppMainController mainController;

    @FXML
    private void button_DashboardActionListener(ActionEvent event) {
        mainController.event_DashboardButtonPressed();
    }

    @FXML
    private void button_ExecutionAndTaskPanelActionListener(ActionEvent event) {
        mainController.event_ExecutionAndTaskPanelButtonPressed();
    }

    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
    }
}


