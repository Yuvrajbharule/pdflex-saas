package com.pdflex.service;

import org.springframework.web.multipart.MultipartFile;

public interface PdfService {
    String compress(MultipartFile file) throws Exception;
    String merge(MultipartFile[] files) throws Exception;
}
