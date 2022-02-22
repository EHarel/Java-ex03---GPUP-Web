package gpupweb.servlets;

import gpupweb.utils.ServletUtils;
import gpupweb.utils.SessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import task.enums.ExecutionStatus;
import utilsharedall.ConstantsAll;

import java.io.IOException;

public class ExecutionStatusUpdateServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("[ExecutionStatusUpdateServlet] Start");
        response.setContentType("text/plain;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().print("User not logged in");
        } else {
            String executionNameFromParameter = request.getParameter(ConstantsAll.QP_EXECUTION_NAME);

//            boolean isUploadingUser = Engine.getInstance().isExecutionCreator(executionNameFromParameter, usernameFromSession);

            boolean isUploadingUser = ServletUtils.getExecutionManager(getServletContext()).isExecutionCreator(executionNameFromParameter, usernameFromSession);
            isUploadingUser = true; // For debugging. TODO: remove this when done

            if (!isUploadingUser) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getOutputStream().print("User is not creator of given task.");
            } else {
                ExecutionStatus executionStatus = ExecutionStatus.valueOf(request.getParameter(ConstantsAll.QP_EXECUTION_STATUS));

                // TODO: code to check current status
                if (ServletUtils.getExecutionManager(getServletContext()).updateExecutionStatus(executionNameFromParameter, executionStatus)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getOutputStream().print("Status updated.");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getOutputStream().print("Something went wrong :(");
                }
            }

            System.out.println("[ExecutionStatusUpdateServlet] End");
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
