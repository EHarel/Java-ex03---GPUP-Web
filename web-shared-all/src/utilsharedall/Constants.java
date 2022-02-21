package utilsharedall;

import com.google.gson.Gson;
import okhttp3.MediaType;

public class Constants {
    public final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    // Query Parameter Constants
    public final static String QP_USERTYPE = "usertype";
    public final static String QP_USERNAME = "username";
    public final static String QP_EXECUTION_NAME = "executionName";
    public final static String QP_EXECUTION_STATUS = "executionStatus";
    public final static String QP_TARGET_COUNT = "targetCount";


    public final static String BP_EXECUTION_DTO = "executionDTO";


    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/web_server_Web_exploded";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/userlist";
    public final static String GRAPH_LIST = FULL_SERVER_PATH + "/graphlist";
    public final static String EXECUTION_LIST = FULL_SERVER_PATH + "/executionlist";

    public final static String EXECUTION_TARGET_FETCH = FULL_SERVER_PATH + "/execution/targets";



    public final static String FILE_UPLOAD = FULL_SERVER_PATH + "/file-upload";

    public final static String EXECUTION_UPLOAD = FULL_SERVER_PATH + "/execution/upload";
    public final static String EXECUTION_SUBSCRIBE = FULL_SERVER_PATH + "/execution/subscribe";
    public final static String EXECUTION_STATUS_UPDATE = FULL_SERVER_PATH + "/execution/status";






    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
