package gpupweb.servlets;

import exception.*;
import file.FileManager;
import gpupweb.utils.ServletUtils;
import gpupweb.utils.SessionUtils;
import graph.DependenciesGraph;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import logic.Engine;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;

@WebServlet("/file-upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileUploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        Collection<Part> parts = request.getParts();
        String usernameFromSession = SessionUtils.getUsername(request);

        StringBuilder fileContent = new StringBuilder();

        int servletResponse = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String resMsg = "";
        for (Part part : parts) {
            fileContent.append(readFromInputStream(part.getInputStream()));

            try {
                DependenciesGraph graph = FileManager.getGraphFromXMLInputStreamIfValid(part.getInputStream(), usernameFromSession);
                ServletUtils.getGraphManager(getServletContext()).addGraph(graph);
                servletResponse = HttpServletResponse.SC_OK;
                resMsg = "File uploaded!";
            } catch (JAXBException e) {
                e.printStackTrace();
            } catch (ExistingItemException e) {
                e.printStackTrace();
                servletResponse = HttpServletResponse.SC_BAD_REQUEST;
                resMsg = "Error! Existing item exception";
            } catch (DependencyOnNonexistentTargetException e) {
                servletResponse = HttpServletResponse.SC_BAD_REQUEST;
                resMsg = "Error! Dependency on non-existent target";
            } catch (ImmediateCircularDependencyException e) {
                servletResponse = HttpServletResponse.SC_BAD_REQUEST;
                resMsg = "Error! Immediate circular dependency!";
            } catch (NullOrEmptyStringException e) {
                servletResponse = HttpServletResponse.SC_BAD_REQUEST;
                resMsg = "Error! Null or empty string";
            } catch (InvalidInputRangeException e) {
                servletResponse = HttpServletResponse.SC_BAD_REQUEST;
                resMsg = "Error! Invalid input range";
            } catch (NonexistentTargetException e) {
                servletResponse = HttpServletResponse.SC_BAD_REQUEST;
                resMsg = "Error! Non-existent target";
            } catch (SerialSetNameRepetitionException e) {
                e.printStackTrace();
            }
        }

//        printFileContent(fileContent.toString(), out);

        response.setStatus(servletResponse);
//        response.getOutputStream().print(resMsg);
        response.getWriter().print(resMsg);
    }

    private void printPart(Part part, PrintWriter out) {
        StringBuilder sb = new StringBuilder();
        sb
                .append("Parameter Name: ").append(part.getName()).append("\n")
                .append("Content Type (of the file): ").append(part.getContentType()).append("\n")
                .append("Size (of the file): ").append(part.getSize()).append("\n")
                .append("Part Headers:").append("\n");

        for (String header : part.getHeaderNames()) {
            sb.append(header).append(" : ").append(part.getHeader(header)).append("\n");
        }

        out.println(sb.toString());
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    private void printFileContent(String content, PrintWriter out) {
        out.println("File content:");
        out.println(content);
    }
}
