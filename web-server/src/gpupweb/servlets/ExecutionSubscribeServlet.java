package gpupweb.servlets;

import gpupweb.utils.ServletUtils;
import gpupweb.utils.SessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import task.ExecutionManager;
import task.enums.ExecutionStatus;
import users.User;
import users.UserManager;
import utilsharedall.ConstantsAll;

import java.io.IOException;

public class ExecutionSubscribeServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().print("User not logged in");
        } else {
            String executionNameFromParameter = request.getParameter(ConstantsAll.QP_EXECUTION_NAME);
            ExecutionManager executionManager = ServletUtils.getExecutionManager(getServletContext());

            ExecutionStatus exeStatus = executionManager.getExecutionStatus(executionNameFromParameter);

            if (exeStatus == ExecutionStatus.ENDED || exeStatus == ExecutionStatus.COMPLETED || exeStatus == ExecutionStatus.STOPPED) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getOutputStream().print("Execution status is " + exeStatus.name());
            } else {
                if (executionManager.addUserToConfiguration(executionNameFromParameter, usernameFromSession)) {
                    UserManager userManager = ServletUtils.getUserManager(getServletContext());
                    User user = userManager.getUser(usernameFromSession);
                    user.addNewParticipatingExecution(executionNameFromParameter);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getOutputStream().print("User added!");
                } else {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getOutputStream().print("User already part of execution work force.");
                }
            }
        }
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
