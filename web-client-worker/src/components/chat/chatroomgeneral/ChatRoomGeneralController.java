package components.chat.chatroomgeneral;


import components.app.AppMainController;
import components.chat.chatarea.ChatAreaController;
import components.login.LoginPerformedListenerWorker;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


public class ChatRoomGeneralController implements LoginPerformedListenerWorker
//        implements Closeable
//        HttpStatusUpdate, ChatCommands
{

//    @FXML private VBox usersListComponent;

//    @FXML private UsersListController usersListComponentController;

//    @FXML private VBox actionCommandsComponent;

//    @FXML private CommandsController actionCommandsComponentController;

//    @FXML private GridPane chatAreaComponent;


//    @FXML private ChatAreaController chatAreaComponentController;


//    private ChatAppMainController chatAppMainController;

    @FXML private Parent component_ChatArea;
    @FXML private ChatAreaController component_ChatAreaController;

    private AppMainController chatAppMainController;


    @FXML
    public void initialize() {
//        usersListComponentController.setHttpStatusUpdate(this);
//        actionCommandsComponentController.setChatCommands(this);
//        chatAreaComponentController.setHttpStatusUpdate(this);
//
//        chatAreaComponentController.autoUpdatesProperty().bind(actionCommandsComponentController.autoUpdatesProperty());
//        usersListComponentController.autoUpdatesProperty().bind(actionCommandsComponentController.autoUpdatesProperty());
    }

//    @Override
//    public void updateHttpLine(String line) {
//        chatAppMainController.updateHttpLine(line);
//    }

//    @Override
//    public void close() throws IOException {
////        usersListComponentController.close();
////        chatAreaComponentController.close();
//    }

    @Override
    public void loginPerformed(String username, int threadCount) {
        component_ChatAreaController.startListRefresher();
    }

//    public void setActive() {
//        usersListComponentController.startListRefresher();
//        chatAreaComponentController.startListRefresher();
//    }
//
//    public void setInActive() {
//        try {
//            usersListComponentController.close();
//            chatAreaComponentController.close();
//        } catch (Exception ignored) {}
//    }

    public void setMainController(AppMainController chatAppMainController) {
        this.chatAppMainController = chatAppMainController;
        this.chatAppMainController.addEventListener_LoginPerformed(this);
    }

//    public void setChatAppMainController(ChatAppMainController chatAppMainController) {
//        this.chatAppMainController = chatAppMainController;
//    }

//    @Override
//    public void logout() {
//        chatAppMainController.switchToLogin();
//    }


}
