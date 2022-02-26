package gpupweb.servlets;

import file.FileManager;
import gpupweb.utils.ServletUtils;
import gpupweb.utils.SessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import task.AffectedTargetsData;
import task.ExecutionManager;
import task.enums.TaskResult;
import users.UserManager;
import utilsharedall.ConstantsAll;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Scanner;

@WebServlet("/execution/targets/update")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
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
        response.setContentType("text/plain");

        Collection<Part> parts = request.getParts();
        String targetLog = null;

        for (Part part : parts) {
            targetLog = readFromInputStream(part.getInputStream());
        }

        String username = SessionUtils.getUsername(request);

        try {
            String executionName = request.getParameter(ConstantsAll.QP_EXECUTION_NAME);
            String targetName = request.getParameter(ConstantsAll.QP_TARGET_NAME);
            String taskResultStr = request.getParameter(ConstantsAll.QP_TASK_RESULT);
            TaskResult taskResult = TaskResult.valueOf(taskResultStr);

            ExecutionManager executionManager = ServletUtils.getExecutionManager(getServletContext());
            AffectedTargetsData affectedTargetsData = executionManager.updateTargetTaskResult(executionName, targetName, taskResult);
            targetLog = updateLogWithAffectedTargets(targetLog, affectedTargetsData, username);
            FileManager fileManager = ServletUtils.getFileManager(getServletContext());
            fileManager.saveTargetLog(targetName, targetLog, executionName);

            executionManager.addTargetLog(executionName, targetName, targetLog);


            int payment = handleUserPayment(taskResult, executionName, username);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(payment);
        } catch (IllegalArgumentException iae) {

        }
    }

    private int handleUserPayment(TaskResult taskResult, String executionName, String username) {
        int payment = 0;

        if (taskResult != TaskResult.UNPROCESSED) {
            ExecutionManager executionManager = ServletUtils.getExecutionManager(getServletContext());
            payment = executionManager.getExecutionPaymentPerTarget(executionName);

            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            userManager.updateUserProcessedTarget(username, executionName, payment);
        }

        return payment;
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    private String updateLogWithAffectedTargets(String targetLog, AffectedTargetsData affectedTargetsData, String username) {
        String newLog = targetLog +
                "\n\t\tTargets that have opened as a result: " + affectedTargetsData.getOpenedTargetsNames() +
                "\n\t\tTargets that are skipped as a result: " + affectedTargetsData.getSkippedTargetsNames() +
                "\nPerforming worker: " + username;

        return newLog;
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
