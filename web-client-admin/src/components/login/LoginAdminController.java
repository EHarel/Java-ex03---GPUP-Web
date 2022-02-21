package components.login;

import components.app.AppMainController;
import httpclient.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import utilsharedall.Constants;
import utilsharedall.UserType;

import java.io.IOException;

public class LoginAdminController {

    private int counter = 0;

    @FXML
    private ScrollPane ScrollPane_Main;

    @FXML
    private TextField textField_Username;

    @FXML
    private Button button_Login;

    @FXML
    private Label label_LoginResult;

    private final StringProperty resultMessageProperty = new SimpleStringProperty();
    private AppMainController appMainController;

    public Button getButton_Login() {
        return button_Login;
    }

    @FXML
    public void initialize() {
        label_LoginResult.textProperty().bind(resultMessageProperty);
        button_Login.setDefaultButton(true);
//        util.http.HttpClientUtil.setCookieManagerLoggingFacility(line ->
//                Platform.runLater(() ->
//                        updateHttpStatusLine(line)));
    }


    @FXML
    void onButton_LoginAction(ActionEvent event) {
        resultMessageProperty.set("Hey I'm clicked! Counter is " + ++counter);

        String userName = textField_Username.getText();
        if (userName.isEmpty()) {
            resultMessageProperty.set("User name is empty. You can't login with empty user name");

            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.QP_USERNAME, userName)
                .addQueryParameter(Constants.QP_USERTYPE, UserType.Admin.name())
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
                        appMainController.loginSuccessful(userName);
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

