package gpupweb.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gpupweb.utils.ServletUtils;
import gpupweb.utils.SessionUtils;
import graph.Target;
import graph.TargetDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import task.ExecutionManager;
import users.User;
import users.UserManager;
import utilsharedall.ConstantsAll;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ExecutionTargetFetchServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(request);

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().print("User not logged in");
        } else {

            try {
                String targetCountParameterStr = request.getParameter(ConstantsAll.QP_TARGET_COUNT);
                Integer targetCount = Integer.parseInt(targetCountParameterStr);

                if (targetCount > 0) {
                    try (PrintWriter out = response.getWriter()) {
                        ExecutionManager executionManager = ServletUtils.getExecutionManager(getServletContext());
                        UserManager userManager = ServletUtils.getUserManager(getServletContext());
                        User user = userManager.getUser(usernameFromSession);

                        System.out.println("[ExecutionTargetFetchServlet] - Pre-Invoking getTargetsForUser() from executionManager.");
                        Collection<Target> chosenTargets = executionManager.getTargetsForUser(user, targetCount);
                        System.out.println("[ExecutionTargetFetchServlet] - Post-Invoking getTargetsForUser() from executionManager.");

                        Set<TargetDTO> chosenTargetsDTOs = new HashSet<>();
                        chosenTargets.forEach(target -> {
                            chosenTargetsDTOs.add(target.toDTO());
                        });

                        Gson gson = new GsonBuilder().serializeNulls().create();

                        System.out.println("[ExecutionTargetFetchServlet] - Pre-toJson on targets.");
                        String jsonTargets = gson.toJson(chosenTargetsDTOs);
                        System.out.println("[ExecutionTargetFetchServlet] - Post-toJson on targets.");

                        if (chosenTargetsDTOs.size() > 0) {
                            System.out.println("Debugging line");
                        }

                        out.println(jsonTargets);
                        out.flush();
                    }
//
//                    if (Engine.getInstance().addUserToConfiguration(executionNameFromParameter, usernameFromSession)) {
//                        response.setStatus(HttpServletResponse.SC_OK);
//                        response.getOutputStream().print("User added!");
//                    } else {
//                        response.setStatus(HttpServletResponse.SC_CONFLICT);
//                        response.getOutputStream().print("User already part of execution work force.");
//                    }
                }
            } catch (NumberFormatException nfe) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getOutputStream().print("Error! The argument for the number of targets requested is not a number.");
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
