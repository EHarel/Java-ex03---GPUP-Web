package gpupweb.servlets;


import gpupweb.utils.ServletUtils;
import gpupweb.utils.SessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import task.Execution;
import task.ExecutionManager;
import task.execution.ExecutionDTO;

import java.io.IOException;

import task.execution.WorkerExecutionReportDTO;
import users.User;
import users.UserManager;
import users.WorkerExecutionStanding;
import utilsharedall.ConstantsAll;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * This servlet sends the user an array of all the executions he participates in.
 * It also adds a progress measurement to the execution.
 */
public class WorkerParticipatingExecutionsReportServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            String qpExecutionMustBeRunning = request.getParameter(ConstantsAll.QP_FILTER_EXECUTION_MUST_BE_RUNNING);
            String qpUserMustBeActive = request.getParameter(ConstantsAll.QP_FILTER_USER_MUST_BE_ACTIVE);
            boolean filter_ExecutionMustBeRunning = Boolean.parseBoolean(qpExecutionMustBeRunning);
            boolean filter_UserMustBeActive = Boolean.parseBoolean(qpUserMustBeActive);

            String username = SessionUtils.getUsername(request);
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            User user = userManager.getUser(username);

            ExecutionManager executionManager = ServletUtils.getExecutionManager(getServletContext());
            Collection<Execution> executionsUserParticipates = executionManager.getExecutionsUserParticipates(user, filter_ExecutionMustBeRunning, filter_UserMustBeActive);

            Collection<WorkerExecutionReportDTO> executionReportDTOS = new LinkedList<>();

            executionsUserParticipates.forEach(execution -> {

                String executionName = execution.getExecutionName();
//                float executionProgress = execution.getProgress(); // todo: IMPLEMENT!
                float executionProgress = 0.666f;


                int targetsUserProcessedWithAnyResult = user.getNumberOfProcessedTargetsOfExecution(executionName);
                int totalCreditsEarned = user.getCreditsEarnedForExecution(executionName);
                boolean isUserActive = user.isActiveInExecution(executionName);

                WorkerExecutionReportDTO workerExecutionReportDTO = new WorkerExecutionReportDTO(
                        executionName,
                        execution.getTotalWorkers(),
                        executionProgress,
                        targetsUserProcessedWithAnyResult,
                        totalCreditsEarned,
                        isUserActive,
                        execution.getExecutionStatus());

                executionReportDTOS.add(workerExecutionReportDTO);
            });

            String json = ConstantsAll.GSON_INSTANCE.toJson(executionReportDTOS);
            out.println(json);
            out.flush();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
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