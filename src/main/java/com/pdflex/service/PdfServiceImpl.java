package com.pdflex.service;

import com.pdflex.constant.AppConstants;
import com.pdflex.util.GhostscriptUtil;
import com.pdflex.utl.FileUtil;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfServiceImpl implements PdfService {

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private GhostscriptUtil ghostscriptUtil;

    @Override
    public String compress(MultipartFile file) throws Exception {

        System.out.println("Hit compress method");

        File savedFile = fileUtil.saveFile(file);

        try {
            String outputFile = ghostscriptUtil.compress(savedFile);

            System.out.println("Compression done");
            return outputFile;

        } finally {
            // ✅ Always delete input file
            savedFile.delete();
        }
    }

    @Override
    public String merge(MultipartFile[] files) throws Exception {

        if (files == null || files.length < 2) {
            throw new RuntimeException("Minimum 2 files required");
        }

        File outputDir = new File(AppConstants.OUTPUT_DIR);
        if (!outputDir.exists()) outputDir.mkdirs();

        String outputFileName = "merged_" + System.currentTimeMillis() + ".pdf";
        String outputPath = outputDir.getAbsolutePath() + "/" + outputFileName;

        PDFMergerUtility merger = new PDFMergerUtility();

        List<File> tempFiles = new ArrayList<>();

        try {

            for (MultipartFile file : files) {

                if (!file.getContentType().contains("pdf")) {
                    throw new RuntimeException("Only PDF files allowed");
                }

                File tempFile = fileUtil.saveFile(file);
                tempFiles.add(tempFile);
                merger.addSource(tempFile);
            }

            merger.setDestinationFileName(outputPath);
            merger.mergeDocuments(null);

            return outputFileName;

        } finally {
            // ✅ Cleanup all temp files
            for (File f : tempFiles) {
                f.delete();
            }
        }
    }
}