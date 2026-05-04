package com.pdflex.model.dto;

public class FileResponseDTO {

    private boolean success;
    private String fileName;
    private String message;

    public FileResponseDTO(boolean success, String fileName, String message) {
        this.success = success;
        this.fileName = fileName;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMessage() {
        return message;
    }
}