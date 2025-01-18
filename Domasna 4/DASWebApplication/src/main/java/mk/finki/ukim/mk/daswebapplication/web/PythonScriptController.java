package mk.finki.ukim.mk.daswebapplication.web;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Controller
@RequestMapping("/api")
public class PythonScriptController {
    @GetMapping("/run-python")
    public String runPythonScript() {
        try {
            String pythonScriptPath = new ClassPathResource("static/dataDownload.py").getFile().getAbsolutePath();
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
            Process process = processBuilder.start();

            System.out.println("Python script started.");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Python script executed successfully.");
                return "Python script executed successfully:\n" + output;
            } else {
                System.out.println("Python script failed with exit code: " + exitCode);
                return "Python script failed with exit code: " + exitCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while running Python script.";
        }
    }
}
