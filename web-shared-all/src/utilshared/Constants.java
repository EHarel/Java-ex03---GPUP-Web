package utilshared;

import com.google.gson.Gson;

public class Constants {
    // Query Parameter Constants
    public final static String QP_USERTYPE = "usertype";
    public final static String QP_USERNAME = "username";


    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/web_server_Web_exploded";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/userlist";
    public final static String GRAPH_LIST = FULL_SERVER_PATH + "/graphlist";
    public final static String FILE_UPLOAD = FULL_SERVER_PATH + "/file-upload";


    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
