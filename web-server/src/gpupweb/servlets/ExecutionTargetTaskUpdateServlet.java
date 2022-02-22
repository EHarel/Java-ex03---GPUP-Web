package gpupweb.servlets;

import gpupweb.utils.ServletUtils;
import gpupweb.utils.SessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import task.ExecutionManager;
import task.enums.TaskResult;
import users.UserDTO;
import users.UserManager;
import utilsharedall.ConstantsAll;
import utilsharedall.UserType;

import java.io.IOException;
import java.util.Properties;

public class ExecutionTargetTaskUpdateServlet extends HttpServlet {
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("[ExecutionTargetTaskUpdateServlet] - starting");
        response.setContentType("text/plain;charset=UTF-8");
        response.setContentType("text/plain");

        Properties prop = new Properties();
        prop.load(request.getInputStream());


        try {
            String executionName = prop.getProperty(ConstantsAll.BP_EXECUTION_NAME);
            String targetName = prop.getProperty(ConstantsAll.BP_TARGET_NAME);
            String taskResultStr = prop.getProperty(ConstantsAll.BP_TASK_RESULT);
            TaskResult taskResult = TaskResult.valueOf(taskResultStr);
            // Logs? TODO

            ExecutionManager executionManager = ServletUtils.getExecutionManager(getServletContext());
            executionManager.updateTargetTaskResult(executionName, targetName, taskResult);


        } catch (IllegalArgumentException iae) {

        }

        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
//
//        if (usernameFromSession == null) { //user is not logged in yet
//            try {
//                String usernameFromParameter = request.getParameter(ConstantsAll.QP_USERNAME);
//                String userTypeStr = request.getParameter(ConstantsAll.QP_USERTYPE);
//                UserType usertypeFromParameter = UserType.valueOf(userTypeStr);
//
//                if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
//                    //no username in session and no username in parameter - not standard situation. it's a conflict
//
//                    // stands for conflict in server state
//                    response.setStatus(409);
//
//                    // returns answer to the browser to go back to the sign up URL page
//                    response.getOutputStream().print(SIGN_UP_URL);
//                } else {
//                    //normalize the username value
//                    usernameFromParameter = usernameFromParameter.trim();
//
//                    UserDTO userDTO = new UserDTO(usernameFromParameter, usertypeFromParameter);
//
//                /*
//                One can ask why not enclose all the synchronizations inside the userManager object ?
//                Well, the atomic action we need to perform here includes both the question (isUserExists) and (potentially) the insertion
//                of a new user (addUser). These two actions needs to be considered atomic, and synchronizing only each one of them, solely, is not enough.
//                (of course there are other more sophisticated and performable means for that (atomic objects etc) but these are not in our scope)
//
//                The synchronized is on this instance (the servlet).
//                As the servlet is singleton - it is promised that all threads will be synchronized on the very same instance (crucial here)
//
//                A better code would be to perform only as little and as necessary things we need here inside the synchronized block and avoid
//                do here other not related actions (such as request dispatcher\redirection etc. this is shown here in that manner just to stress this issue
//                 */
//                    synchronized (this) {
//                        if (userManager.isUserExists(usernameFromParameter)) {
//                            String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
//
//                            // stands for unauthorized as there is already such user with this name
////                            response.setStatus(401);
//                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//                            response.getOutputStream().print(errorMessage);
//                        } else {
//                            //add the new user to the users list
//                            userManager.addUser(userDTO);
//                            //set the username in a session so it will be available on each request
//                            //the true parameter means that if a session object does not exists yet
//                            //create a new one
//                            request.getSession(true).setAttribute(ConstantsAll.QP_USERNAME, usernameFromParameter);
//
//                            //redirect the request to the chat room - in order to actually change the URL
//                            System.out.println("On login, request URI is: " + request.getRequestURI());
//                            response.setStatus(HttpServletResponse.SC_OK);
//                            response.getOutputStream().print(CHAT_ROOM_URL);
//                        }
//                    }
//                }
//            } catch (IllegalArgumentException exception) {
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                response.getOutputStream().print("Invalid user type parameter");
//            }
//        } else {
//            //user is already logged in
//            response.setStatus(200);
//            response.getOutputStream().print(CHAT_ROOM_URL);
//        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
