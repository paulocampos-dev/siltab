package com.BYD.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private static final Map<String, String> EXTENSION_MIME_MAP = Map.of(

            //CAUTION: Security considerations: SVG can contain scripts,
            // so if the server processes or serves SVGs,
            // they should ensure they are properly sanitized.
            // Also, some formats may have vulnerabilities in
            // image processing libraries, so keeping software up-to-date is important.
            // Use an XML sanitizer if accepting user-uploaded SVGs
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "gif", "image/gif",
            "tiff", "image/tiff"
    );

    @Value("${dealer.images.base-path}")
    private String baseImagePath;

    public String storeFile(MultipartFile file, String dealerCode, String imageType) throws IOException {
//        String contentType = file.getContentType();
//        if (!EXTENSION_MIME_MAP.containsValue(contentType)) {
//            throw new IOException("Invalid file type: " + contentType);
//        }
        String dealerPath = createDealerDirectory(dealerCode, imageType);
//        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

        String uniqueFileName = UUID.randomUUID().toString() + extension;

        Path targetLocation = Paths.get(dealerPath, uniqueFileName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            Path basePath = Paths.get(baseImagePath);
            Path relativePath = basePath.relativize(targetLocation);

            return relativePath.toString().replace(File.separator, "/");
        } catch (IOException ex) {
            logger.error("Could not store file {}. Error: {}", uniqueFileName, ex.getMessage());
            throw new IOException("Could not store file " + uniqueFileName, ex);
        }
    }

    private String createDealerDirectory(String dealerCode, String imageType) throws IOException {
        Path dealerPath = Paths.get(baseImagePath, dealerCode, imageType);
        Files.createDirectories(dealerPath);
        return dealerPath.toString();
    }

    public void deleteFile(String filePath) throws IOException {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException ex) {
            logger.error("Could not delete file {}. Error: {}", filePath, ex.getMessage());
            throw new IOException("Could not delete file: " + filePath, ex);
        }
    }

//    public byte[] loadFile(String filePath) throws IOException {
//        try {
//            Path path = Paths.get(filePath);
//            return Files.readAllBytes(path);
//        } catch (IOException ex) {
//            logger.error("Could not read file {}. Error: {}", filePath, ex.getMessage());
//            throw new IOException("Could not read file: " + filePath, ex);
//        }
//    }

}