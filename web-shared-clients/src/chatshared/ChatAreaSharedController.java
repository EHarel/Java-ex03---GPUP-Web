package chatshared;

import chatshared.model.ChatLinesWithVersion;
import httpclient.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextArea;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utilsharedall.ConstantsAll;
import utilsharedclient.ConstantsClient;

import java.io.IOException;
import java.util.Timer;
import java.util.stream.Collectors;

public class ChatAreaSharedController {
    private TextArea chatLineTextArea;
    private TextArea mainChatLinesTextArea;
    private ChatAreaRefresher chatAreaRefresher;
    private Timer timer;

    private final IntegerProperty chatVersion;
    private final BooleanProperty autoUpdate;

    private final BooleanProperty autoScroll;



    public ChatAreaSharedController(TextArea chatLineTextArea, TextArea mainChatLinesTextArea, IntegerProperty chatVersion) {
        this.chatLineTextArea = chatLineTextArea;
        this.autoUpdate = new SimpleBooleanProperty();
        this.chatVersion = chatVersion;
        this.mainChatLinesTextArea = mainChatLinesTextArea;
        this.autoScroll = new SimpleBooleanProperty();

    }

    public BooleanProperty autoScrollProperty() {
        return autoScroll;
    }

    public BooleanProperty autoUpdateProperty() {
        return autoUpdate;
    }


    public void startListRefresher() {
        chatAreaRefresher = new ChatAreaRefresher(
                chatVersion,
                autoUpdate,
                null,
                this::updateChatLines);
        timer = new Timer();
        timer.schedule(chatAreaRefresher, ConstantsClient.REFRESH_RATE_CHAT, ConstantsClient.REFRESH_RATE_CHAT);
    }

    private void updateChatLines(ChatLinesWithVersion chatLinesWithVersion) {
        if (chatLinesWithVersion.getVersion() != chatVersion.get()) {
            String deltaChatLines = chatLinesWithVersion
                    .getEntries()
                    .stream()
                    .map(singleChatLine -> {
                        long time = singleChatLine.getTime();
                        return String.format(ConstantsClient.CHAT_LINE_FORMATTING, time, time, time, singleChatLine.getUsername(), singleChatLine.getChatString());
                    }).collect(Collectors.joining());

            Platform.runLater(() -> {
                chatVersion.set(chatLinesWithVersion.getVersion());

                if (autoScroll.get()) {
                    mainChatLinesTextArea.appendText(deltaChatLines);
                    mainChatLinesTextArea.selectPositionCaret(mainChatLinesTextArea.getLength());
                    mainChatLinesTextArea.deselect();
                } else {
                    int originalCaretPosition = mainChatLinesTextArea.getCaretPosition();
                    mainChatLinesTextArea.appendText(deltaChatLines);
                    mainChatLinesTextArea.positionCaret(originalCaretPosition);
                }
            });
        }
    }


    public void sendButtonClicked() {
        String chatLine = chatLineTextArea.getText();
        String finalUrl = HttpUrl
                .parse(ConstantsAll.SEND_CHAT_LINE)
                .newBuilder()
                .addQueryParameter(ConstantsAll.QP_CHAT_PARAMETER, chatLine)
                .build()
                .toString();

//        httpStatusUpdate.updateHttpLine(finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure...:(");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
//                    httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure. Error code: " + response.code());
                }
            }
        });

        chatLineTextArea.clear();
    }

    public void setLineTextArea(TextArea chatLineTextArea) {
        this.chatLineTextArea = chatLineTextArea;
    }

    public void setMainChatLinesTextArea(TextArea mainChatLinesTextArea) {
        this.mainChatLinesTextArea = mainChatLinesTextArea;
    }
}
