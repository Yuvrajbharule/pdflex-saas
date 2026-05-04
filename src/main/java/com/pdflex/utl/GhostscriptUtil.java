package com.pdflex.util;

import com.pdflex.constant.AppConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class GhostscriptUtil {

    @Value("${ghostscript.path}")
    private String gsPath;

    public String compress(File inputFile) throws Exception {

        String outputFileName = "compressed_" + System.currentTimeMillis() + ".pdf";

        File outputDir = new File(AppConstants.OUTPUT_DIR);
        if (!outputDir.exists()) outputDir.mkdirs();

        String outputPath = outputDir.getAbsolutePath() + "/" + outputFileName;

        ProcessBuilder pb = new ProcessBuilder(
                gsPath,
                "-sDEVICE=pdfwrite",
                "-dCompatibilityLevel=1.4",
                "-dPDFSETTINGS=/screen",
                "-dNOPAUSE",
                "-dQUIET",
                "-dBATCH",
                "-sOutputFile=" + outputPath,
                inputFile.getAbsolutePath()
        );

        pb.redirectErrorStream(true);

        Process process = pb.start();

        // 🔥 Logs read karo (debugging ke liye)
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("GS LOG: " + line);
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Ghostscript compression failed");
        }

        return outputFileName;
    }
}