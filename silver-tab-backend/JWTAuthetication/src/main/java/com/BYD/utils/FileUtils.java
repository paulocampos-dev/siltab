package com.BYD.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FileUtils {

//    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
//            "jpg",
//            "jpeg",
//            "png",
//            "gif"
//    ));
//
//    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
//            "image/jpeg",
//            "image/png",
//            "image/gif"
//    ));

    public static final Map<String, String> EXTENSION_MIME_MAP = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "jfif", "image/jpeg",
            "png", "image/png",
            "gif", "image/gif",
            "bmp", "image/bmp"
    );

    public static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(EXTENSION_MIME_MAP.keySet());
    public static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(EXTENSION_MIME_MAP.values());

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public static void validateImage(MultipartFile file) throws IOException {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds limit");
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IOException("Invalid content type. Only images are allowed");
        }

        // Check file extension
        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IOException("File type not supported");
        }


//        // Optional: Validate actual file content
//        // Stronger validation, but could cost more computational usage.
//        try {
//            BufferedImage image = ImageIO.read(file.getInputStream());
//            if (image == null) {
//                throw new IOException("Invalid image content");
//            }
//        } catch (IOException e) {
//            throw new IOException("Failed to validate image content");
//        }
    }

    public static String detectMimeType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return EXTENSION_MIME_MAP.getOrDefault(extension, null);
    }

    public static String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) return "";
        return filename.substring(lastDotIndex + 1);
    }

    public static void createDirectoryIfNotExists(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    public static void deleteFileIfExists(String filePath) throws IOException {
        if (filePath != null) {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        }
    }

    public static String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}