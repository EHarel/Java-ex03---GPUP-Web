package gpupweb.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import gpupweb.utils.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import task.Execution;
import task.ExecutionManager;
import task.execution.ExecutionDTO;
import utilsharedall.ConstantsAll;

import java.io.*;
import java.util.Properties;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class ExecutionUploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[ExecutionUploadServlet] - starting");
        response.setContentType("text/plain");
        Properties prop = new Properties();
        prop.load(request.getInputStream());
        String executionDTOStr = prop.getProperty(ConstantsAll.BP_EXECUTION_DTO);
        try {
//            Gson gson = new GsonBuilder().serializeNulls().create();
            ExecutionDTO executionDTO = new Gson().fromJson(executionDTOStr, ExecutionDTO.class);

            synchronized (this) {
                if (existingExecutionName(executionDTO.getExecutionName())) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getOutputStream().print("Execution name already exists.");
                } else {
                    Execution execution = new Execution(executionDTO);
                    ExecutionManager executionManager = ServletUtils.getExecutionManager(getServletContext());
                    boolean isAdded = executionManager.addExecution(execution);
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (JsonSyntaxException jsonSyntaxException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getOutputStream().print("Json syntax error.");
        } catch (Exception exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getOutputStream().print("Something went wrong with the server, we're sorry :(");
        }

        System.out.println("[ExecutionUploadServlet] - end");
    }

    private boolean existingExecutionName(String executionName) {
        return ServletUtils.getExecutionManager(getServletContext()).isExistingExecutionName(executionName);
    }
}