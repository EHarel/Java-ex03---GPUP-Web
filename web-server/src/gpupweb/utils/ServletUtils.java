package gpupweb.utils;

//import engine.chat.ChatManager;
//import engine.users.UserManager;
import file.FileManager;
import graph.GraphManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import logic.Engine;
import task.ExecutionManager;
import users.UserManager;

import static gpupweb.constants.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String GRAPH_MANAGER_ATTRIBUTE_NAME = "graphManager";
	private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";
	private static final String EXECUTION_MANAGER_ATTRIBUTE_NAME = "executionManager";
	private static final String FILE_MANAGER_ATTRIBUTE_NAME = "fileManager";


	/*
	Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
	the actual fetch of them is remained un-synchronized for performance POV
	 */
	private static final Object userManagerLock = new Object();
	private static final Object graphManagerLock = new Object();
	private static final Object chatManagerLock = new Object();
	private static final Object executionManagerLock = new Object();
	private static final Object fileManagerLock = new Object();


	public static UserManager getUserManager(ServletContext servletContext) {
		synchronized (userManagerLock) {
			if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
			}
		}

		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}

	public static ExecutionManager getExecutionManager(ServletContext servletContext) {
		synchronized (executionManagerLock) {
			if (servletContext.getAttribute(EXECUTION_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(EXECUTION_MANAGER_ATTRIBUTE_NAME, new ExecutionManager());
			}
		}

		return (ExecutionManager) servletContext.getAttribute(EXECUTION_MANAGER_ATTRIBUTE_NAME);
	}

	public static GraphManager getGraphManager(ServletContext servletContext) {
//		return Engine.getInstance().getGraphManager();

		synchronized (graphManagerLock) {
			if (servletContext.getAttribute(GRAPH_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(GRAPH_MANAGER_ATTRIBUTE_NAME, new GraphManager());
			}
		}

		return (GraphManager) servletContext.getAttribute(GRAPH_MANAGER_ATTRIBUTE_NAME);
	}

	public static FileManager getFileManager(ServletContext servletContext) {
		synchronized (fileManagerLock) {
			if (servletContext.getAttribute(FILE_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(FILE_MANAGER_ATTRIBUTE_NAME, new FileManager());
			}
		}

		return (FileManager) servletContext.getAttribute(FILE_MANAGER_ATTRIBUTE_NAME);
	}

//	public static ChatManager getChatManager(ServletContext servletContext) {
//		synchronized (chatManagerLock) {
//			if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
//				servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
//			}
//		}
//		return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
//	}

	public static int getIntParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
			}
		}

		return INT_PARAMETER_ERROR;
	}
}
