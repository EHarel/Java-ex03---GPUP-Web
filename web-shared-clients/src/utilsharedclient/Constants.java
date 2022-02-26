package utilsharedclient;

import com.google.gson.Gson;

public class Constants {
    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE_USERS = 2000;
    public final static int REFRESH_RATE_GRAPHS = 2000;
    public final static int REFRESH_RATE_EXECUTIONS = 2000;
    public final static int REFRESH_RATE_TARGETS = 2000;
    public static final long REFRESH_RATE_PARTICIPATING_EXECUTIONS = 2000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
//    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/main/chat-app-main.fxml";
//    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/login/login.fxml";
//    public final static String CHAT_ROOM_FXML_RESOURCE_LOCATION = "/chat/client/component/chatroom/chat-room-main.fxml";
}
