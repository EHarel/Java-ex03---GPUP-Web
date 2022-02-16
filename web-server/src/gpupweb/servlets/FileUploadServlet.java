package gpupweb.servlets;

import exception.*;
import gpupweb.utils.SessionUtils;
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

        out.println("[FileUploadServlet - doPost] Total parts : " + parts.size());

        StringBuilder fileContent = new StringBuilder();

        for (Part part : parts) {

            // Aviad Code
//            printPart(part, out);

            //to write the content of the file to an actual file in the system (will be created at c:\samplefile)
//            part.write("samplefile");

            //to write the content of the file to a string
            fileContent.append(readFromInputStream(part.getInputStream()));

            Engine engine = Engine.getInstance();

            try {
                engine.loadXMLFromInputStream(part.getInputStream(), usernameFromSession);
            } catch (JAXBException e) {
                e.printStackTrace();
            } catch (ExistingItemException e) {
                e.printStackTrace();
            } catch (DependencyOnNonexistentTargetException e) {
                e.printStackTrace();
            } catch (ImmediateCircularDependencyException e) {
                e.printStackTrace();
            } catch (NullOrEmptyStringException e) {
                e.printStackTrace();
            } catch (InvalidInputRangeException e) {
                e.printStackTrace();
            } catch (NonexistentTargetException e) {
                e.printStackTrace();
            } catch (SerialSetNameRepetitionException e) {
                e.printStackTrace();
            }
        }

        printFileContent(fileContent.toString(), out);
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
