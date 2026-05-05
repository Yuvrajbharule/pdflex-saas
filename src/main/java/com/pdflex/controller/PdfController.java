package com.pdflex.controller;

import com.pdflex.constant.AppConstants;
import com.pdflex.model.dto.FileResponseDTO;
import com.pdflex.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/compress")
    public ResponseEntity<FileResponseDTO> compress(
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new FileResponseDTO(false, null, "File is required"));
        }

        if (file.getContentType() == null || !file.getContentType().contains("pdf")) {
            return ResponseEntity.badRequest()
                    .body(new FileResponseDTO(false, null, "Only PDF files allowed"));
        }

        try {
            String outputFile = pdfService.compress(file);

            return ResponseEntity.ok(
                    new FileResponseDTO(true, outputFile, "Compression successful")
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(new FileResponseDTO(false, null, "Compression failed"));
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> download(@PathVariable String fileName) throws Exception {

        File file = new File(AppConstants.OUTPUT_DIR + "/" + fileName);

        if (!file.exists()) {
            throw new RuntimeException("File not found");
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        // 🔥 Auto delete after response
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                file.delete();
                System.out.println("Deleted file: " + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @PostMapping("/merge")
    public ResponseEntity<FileResponseDTO> merge(
            @RequestParam("files") MultipartFile[] files) {

        System.out.println("merge api hit");
        try {
            String output = pdfService.merge(files);

            return ResponseEntity.ok(
                    new FileResponseDTO(true, output, "Merge successful")
            );



        } catch (Exception e) {


            return ResponseEntity.badRequest()
                    .body(new FileResponseDTO(false, null, e.getMessage()));
        }


    }
}