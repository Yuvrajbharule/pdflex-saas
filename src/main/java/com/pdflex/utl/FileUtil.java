package com.pdflex.utl;

import com.pdflex.constant.AppConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileUtil {

    public File saveFile(MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID() + ".pdf";

        File dir = new File(AppConstants.INPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File savedFile = new File(dir, fileName);

        System.out.println("Saving file to: " + savedFile.getAbsolutePath());

        if (savedFile.exists()) {
            savedFile.delete();
        }

        file.transferTo(savedFile);

        return savedFile;
    }
}