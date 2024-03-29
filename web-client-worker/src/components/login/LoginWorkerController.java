package components.login;

import components.app.AppMainController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import utilsharedall.ConstantsAll;
import httpclient.HttpClientUtil;
import utilsharedall.UserType;

import java.io.IOException;

public class LoginWorkerController {
    @FXML
    private ScrollPane ScrollPane_Main;

    @FXML
    private TextField textField_Username;

    @FXML
    private Button button_Login;

    @FXML
    private Label label_LoginResult;

    @FXML
    private ComboBox<Integer> comboBox_ThreadChoice;

    private final StringProperty resultMessageProperty = new SimpleStringProperty();
    private AppMainController appMainController;

    public Button getButton_Login() {
        return button_Login;
    }

    @FXML
    public void initialize() {
        label_LoginResult.textProperty().bind(resultMessageProperty);
//        util.http.HttpClientUtil.setCookieManagerLoggingFacility(line ->
//                Platform.runLater(() ->
//                        updateHttpStatusLine(line)));

        for(int i = 1 ; i <= util.Constants.MAX_THREADS ; i++) {
            comboBox_ThreadChoice.getItems().add(i);
        }
    }


    @FXML
    void onButton_LoginAction(ActionEvent event) {
        String userName = textField_Username.getText();
        if (userName.isEmpty()) {
            resultMessageProperty.set("User name is empty. You can't login with empty user name");

            return;
        }

        Integer threadCount = comboBox_ThreadChoice.getSelectionModel().getSelectedItem();
        if (threadCount == null) {
            resultMessageProperty.set("No thread count chosen. You can't login without threads to dedicate.");

            return;
        }




        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(ConstantsAll.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter(ConstantsAll.QP_USERNAME, userName)
                .addQueryParameter(ConstantsAll.QP_USERTYPE, UserType.Worker.name())
                .build()
                .toString();

//        updateHttpStatusLine("New request is launched for: " + finalUrl); // Aviad Code

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        resultMessageProperty.set("Something went wrong: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() != 200) {
                    Platform.runLater(() ->
                            resultMessageProperty.set("Something went wrong: " + responseBody));
                } else {
                    Platform.runLater(() -> {
                        appMainController.loginSuccessful(userName, threadCount);
//                        chatAppMainController.updateUserName(userName);   // Aviad code
//                        chatAppMainController.switchToChatRoom();         // Aviad code
                    });
                }
            }
        });

    }

    public void setMainController(AppMainController mainController) {
        this.appMainController = mainController;
    }
}

