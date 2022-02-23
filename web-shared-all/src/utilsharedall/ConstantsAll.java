package utilsharedall;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.MediaType;

import java.time.format.DateTimeFormatter;

public class ConstantsAll {
    public final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    // Query Parameter Constants
    public final static String QP_USERTYPE = "usertype";
    public final static String QP_USERNAME = "username";
    public final static String QP_EXECUTION_NAME = "executionName";
    public final static String QP_EXECUTION_STATUS = "executionStatus";
    public final static String QP_TARGET_COUNT = "targetCount";

    public final static String QP_TARGET_NAME = "targetName";
    public final static String QP_TASK_RESULT = "taskResult";

    public static final String QP_FILTER_EXECUTION_MUST_BE_RUNNING = "filter_ExecutionMustBeRunning";
    public static final String QP_FILTER_USER_MUST_BE_ACTIVE = "filter_UserMustBeActive";

    public static final String QP_IS_ACTIVE_IN_EXECUTION = "isActiveInExecution";


    public final static String BP_EXECUTION_DTO = "executionDTO";
    public final static String BP_EXECUTION_NAME = QP_EXECUTION_NAME;
    public final static String BP_TARGET_NAME = "targetName";
    public final static String BP_TASK_RESULT = "taskResult";
    public final static String BP_TARGET_LOG = "targetLog";


    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/web_server_Web_exploded";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/user/list";
    public final static String GRAPH_LIST = FULL_SERVER_PATH + "/graphlist";
    public final static String EXECUTION_LIST = FULL_SERVER_PATH + "/executionlist";

    public final static String EXECUTION_TARGET_FETCH = FULL_SERVER_PATH + "/execution/targets";
    public final static String EXECUTION_TARGET_UPDATE = FULL_SERVER_PATH + "/execution/targets/update";

    public final static String USERS_PARTICIPATING_EXECUTIONS = FULL_SERVER_PATH + "/user/participatingexecutions";
    public static final String UPDATE_USER_EXECUTION_ACTIVITY = FULL_SERVER_PATH + "/user/activeinexecution";



    public final static String FILE_UPLOAD = FULL_SERVER_PATH + "/file-upload";

    public final static String EXECUTION_UPLOAD = FULL_SERVER_PATH + "/execution/upload";
    public final static String EXECUTION_SUBSCRIBE = FULL_SERVER_PATH + "/execution/subscribe";
    public final static String EXECUTION_CANCEL_SUBSCRIBE = FULL_SERVER_PATH + "/execution/cancelsubscribe";

    public final static String EXECUTION_STATUS_UPDATE = FULL_SERVER_PATH + "/execution/status";



    public final static String WORKING_DIR = "C:/gpup-working-dir";
    public final static String WORKING_DIR_WORKER = "C:/gpup-working-dir-worker";

    // GSON instance
//    public final static Gson GSON_INSTANCE = new Gson();
    public final static Gson GSON_INSTANCE = new GsonBuilder().serializeNulls().create();

    public final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss");
}
