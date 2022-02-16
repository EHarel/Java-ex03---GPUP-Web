package components.dashboard;

import components.app.AppMainController;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

public class DashboardController {

    @FXML
    private ScrollPane MainPane;

    @FXML
    private BorderPane borderPane;

    private AppMainController mainController;

    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
    }
}


