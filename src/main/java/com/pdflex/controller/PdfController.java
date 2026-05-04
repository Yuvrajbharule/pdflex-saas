package com.pdflex.controller;

import com.pdflex.model.dto.FileResponseDTO;
import com.pdflex.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

        System.out.println("download api hit ");
        File file = new File("F:/PDFlex/output/" + fileName);

        if (!file.exists()) {
            throw new RuntimeException("File not found");
        }

        Resource resource = new UrlResource(file.toURI());


        System.out.println("download api return ");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + file.getName())
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