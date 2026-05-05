package com.pdflex.util;

import com.pdflex.constant.AppConstants;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class GhostscriptUtil {

    public String compress(File inputFile) throws Exception {

        // 🔥 OS-based GS path
        String gsPath = System.getProperty("os.name").toLowerCase().contains("win")
                ? "C:/Program Files/gs/gs10.07.0/bin/gswin64c.exe"
                : "/usr/bin/gs";

        String outputFileName = "compressed_" + System.currentTimeMillis() + ".pdf";

        File outputDir = new File(AppConstants.OUTPUT_DIR);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
            System.out.println("Created output dir: " + outputDir.getAbsolutePath());
        }

        String outputPath = outputDir.getAbsolutePath() + "/" + outputFileName;

        System.out.println("GS Path: " + gsPath);
        System.out.println("Input: " + inputFile.getAbsolutePath());
        System.out.println("Output: " + outputPath);

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