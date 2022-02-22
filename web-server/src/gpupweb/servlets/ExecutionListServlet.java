package gpupweb.servlets;


import gpupweb.utils.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import task.Execution;
import task.ExecutionManager;
import task.execution.ExecutionDTO;

import java.io.IOException;

import utilsharedall.ConstantsAll;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

public class ExecutionListServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
//            Collection<Execution> executions = Engine.getInstance().getExecutionManager().getExecutions();


            ExecutionManager executionManager = ServletUtils.getExecutionManager(getServletContext());
            Collection<Execution> executions = executionManager.getExecutions();

            Collection<ExecutionDTO> executionDTOS = new ArrayList<>();

            executions.forEach(execution -> {
                executionDTOS.add(execution.toDTO());
            });

            String json = ConstantsAll.GSON_INSTANCE.toJson(executionDTOS);
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
