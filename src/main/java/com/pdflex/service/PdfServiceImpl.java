package com.pdflex.service;


import com.pdflex.constant.AppConstants;
import com.pdflex.util.GhostscriptUtil;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class PdfServiceImpl implements PdfService {

    @Autowired
    private com.pdflex.utl.FileUtil fileUtil;

    @Autowired
    private GhostscriptUtil ghostscriptUtil;

    @Override
    public String compress(MultipartFile file) throws Exception {

        System.out.println("Hit compress method in service layer");
        File savedFile = fileUtil.saveFile(file);

        String outputFile = ghostscriptUtil.compress(savedFile);

        // delete input file after processing
        savedFile.delete();
        System.out.println("Compression done");
        return outputFile;
    }

    @Override
    public String merge(MultipartFile[] files) throws Exception {

        System.out.println("merge service hit");

        if (files == null || files.length < 2) {
            throw new RuntimeException("Minimum 2 files required");
        }

        String outputFileName = "merged_" + System.currentTimeMillis() + ".pdf";

        File outputDir = new File(AppConstants.OUTPUT_DIR);
        if (!outputDir.exists()) outputDir.mkdirs();

        String outputPath = outputDir.getAbsolutePath() + "/" + outputFileName;

        PDFMergerUtility merger = new PDFMergerUtility();

        for (MultipartFile file : files) {

            if (!file.getContentType().contains("pdf")) {
                throw new RuntimeException("Only PDF files allowed");
            }

            File tempFile = fileUtil.saveFile(file);
            merger.addSource(tempFile);
        }

        merger.setDestinationFileName(outputPath);
        merger.mergeDocuments(null);

        System.out.println("merge service return ");
        return outputFileName;
    }
}
