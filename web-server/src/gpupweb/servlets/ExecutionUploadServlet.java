package gpupweb.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import gpupweb.utils.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.Engine;
import task.Execution;
import task.ExecutionManager;
import task.execution.ExecutionDTO;
import utilsharedall.Constants;

import java.io.*;
import java.util.Properties;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class ExecutionUploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[ExecutionUploadServlet] - starting");

        // Set content type
        // Check if available execution name

        response.setContentType("text/plain");


        Properties prop = new Properties();
        prop.load(request.getInputStream());

        String executionDTOStr = prop.getProperty(Constants.BP_EXECUTION_DTO);
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


// Old code

//                gson = new Gson();
//        ExecutionDTO executionDTO = gson.fromJson(executionDTOJson, ExecutionDTO.class);


// Old code:
/*
        PrintWriter out = response.getWriter();

        Collection<Part> parts = request.getParts();

        String usernameFromSession = SessionUtils.getUsername(request);

        StringBuilder fileContent = new StringBuilder();


        BufferedReader streamReader;
        StringBuilder responseStrBuilder;
        String executionDTOJson;
        Gson gson;

        for (Part part : parts) {

            // Aviad Code
//            printPart(part, out);

            //to write the content of the file to an actual file in the system (will be created at c:\samplefile)
//            part.write("samplefile");

            //to write the content of the file to a string

            InputStream in = part.getInputStream();
            streamReader = new BufferedReader((new InputStreamReader(in, StandardCharsets.UTF_8)));
            responseStrBuilder = new StringBuilder();
            String inputStr;

            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }

            executionDTOJson = responseStrBuilder.toString();

            gson = new Gson();
            ExecutionDTO executionDTO = gson.fromJson(executionDTOJson, ExecutionDTO.class);
*/


//            Engine engine = Engine.getInstance();
//
//            try {
//                engine.loadXMLFromInputStream(part.getInputStream(), usernameFromSession);
//            } catch (JAXBException e) {
//                e.printStackTrace();
//            } catch (ExistingItemException e) {
//                e.printStackTrace();
//            } catch (DependencyOnNonexistentTargetException e) {
//                e.printStackTrace();
//            } catch (ImmediateCircularDependencyException e) {
//                e.printStackTrace();
//            } catch (NullOrEmptyStringException e) {
//                e.printStackTrace();
//            } catch (InvalidInputRangeException e) {
//                e.printStackTrace();
//            } catch (NonexistentTargetException e) {
//                e.printStackTrace();
//            } catch (SerialSetNameRepetitionException e) {
//                e.printStackTrace();
//            }
