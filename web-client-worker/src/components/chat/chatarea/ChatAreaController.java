package components.chat.chatarea;


import chatshared.ChatAreaSharedController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;

public class ChatAreaController
//        implements Closeable
{

    private final IntegerProperty chatVersion;
//    private HttpStatusUpdate httpStatusUpdate;

    private ChatAreaSharedController sharedController;

    @FXML private ToggleButton autoScrollButton;
    @FXML private TextArea chatLineTextArea;
    @FXML private TextArea mainChatLinesTextArea;
    @FXML private Label chatVersionLabel;
    @FXML private ToggleButton toggleButton_AutoUpdate;


    public ChatAreaController() {
        chatVersion = new SimpleIntegerProperty();

        sharedController = new ChatAreaSharedController(chatLineTextArea, mainChatLinesTextArea, chatVersion);
    }

    @FXML
    public void initialize() {
        chatVersionLabel.textProperty().bind(Bindings.concat("Chat Version: ", chatVersion.asString()));

        sharedController.setLineTextArea(chatLineTextArea);
        sharedController.setMainChatLinesTextArea(mainChatLinesTextArea);

//        autoUpdate.bind(toggleButton_AutoUpdate.selectedProperty());
        sharedController.autoUpdateProperty().bind(toggleButton_AutoUpdate.selectedProperty());

//        autoScroll.bind(autoScrollButton.selectedProperty());
        sharedController.autoScrollProperty().bind(autoScrollButton.selectedProperty());

        autoScrollButton.setSelected(true);
    }


    @FXML
    void sendButtonClicked(ActionEvent event) {
        sharedController.sendButtonClicked();
    }

    public void startListRefresher() {
        sharedController.startListRefresher();
    }

//    public void setHttpStatusUpdate(HttpStatusUpdate chatRoomMainController) {
//        this.httpStatusUpdate = chatRoomMainController;
//    }

//    @Override
//    public void close() throws IOException {
//        chatVersion.set(0);
//        chatLineTextArea.clear();
//        if (chatAreaRefresher != null && timer != null) {
//            chatAreaRefresher.cancel();
//            timer.cancel();
//        }
//    }
}




//public class ChatAreaController {
//
//    @FXML
//    private ToggleButton autoScrollButton;
//
//    @FXML
//    private Label chatVersionLabel;
//
//    @FXML
//    private TextArea mainChatLinesTextArea;
//
//    @FXML
//    private TextArea chatLineTextArea;
//
//    @FXML
//    void sendButtonClicked(ActionEvent event) {
//
//    }
//
//}