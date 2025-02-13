package com.BYD.service;

import com.BYD.dto.dealer.DealerImageDTO;
import com.BYD.service.FileStorageService;
import com.BYD.enums.ErrorEnum;
import com.BYD.exception.DBException;
import com.BYD.mapper.DealerImageMapper;
import com.BYD.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DealerImageService {
    private static final Logger logger = LoggerFactory.getLogger(DealerImageService.class);
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
//    private static final Map<String, String> EXTENSION_MIME_MAP = Map.of(
//            "jpg", "image/jpeg",
//            "jpeg", "image/jpeg",
//            "jfif", "image/jpeg",
//            "png", "image/png",
//            "gif", "image/gif",
//            "bmp", "image/bmp"
//    );

    @Value("${dealer.images.base-path}")
    private String baseImagePath;

    private final FileStorageService fileStorageService;
    private final DealerImageMapper dealerImageMapper;
    private final StockService stockService;
    private final DealerService dealerService;

    public DealerImageService(FileStorageService fileStorageService, DealerImageMapper dealerImageMapper, StockService stockService, DealerService dealerService) {
        this.fileStorageService = fileStorageService;
        this.dealerImageMapper = dealerImageMapper;
        this.stockService = stockService;
        this.dealerService = dealerService;
    }


    // Upload Images //

    // Add this method to your service
    private void validateImageFile(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        logger.debug("Validating image upload - Type: {}, Name: {}, Size: {}",
                contentType, fileName, file.getSize());

        // Case 1: Null checks
        if (contentType == null || fileName == null) {
            logger.warn("Content type or filename is null");
            throw new IOException("Invalid file upload - missing content type or filename");
        }

        // Case 2: Empty file
        if (file.isEmpty()) {
            logger.warn("Empty file uploaded");
            throw new IOException("File is empty");
        }

        // Case 3: File size
        if (file.getSize() > MAX_FILE_SIZE) {
            logger.warn("File too large: {} bytes", file.getSize());
            throw new IOException("File size exceeds limit of 5MB");
        }

        // Case 4-6: Content type validation
        boolean isValidContentType = contentType.startsWith("image/") ||
                (contentType.equals("application/octet-stream") || contentType.equals("binary/octet-stream"));

        // Case 7: Extension validation
        String extension;
        try {
            extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        } catch (IndexOutOfBoundsException e) {
            logger.warn("File has no extension");
            throw new IOException("File must have an extension");
        }
        if (!isValidContentType || !FileUtils.ALLOWED_EXTENSIONS.contains(extension)) {
            logger.warn("Invalid content type: {} for file: {}", contentType, fileName);
            throw new IOException("Invalid content type. Only images are allowed");
        }
    }

    private void validateDealerAccess(String dealerCode, Long userId) throws AccessDeniedException {
        try {
            List<String> dealerCodes = dealerService.getUserAccessibleDealerCodes(userId.toString());

            if (!dealerCodes.contains(dealerCode)) {
                logger.error("User {} not authorized for dealer: {}", userId, dealerCode);
                throw new AccessDeniedException("User not authorized for dealer: " + dealerCode);
            }
        } catch (Exception e) {
            logger.error("Error validating dealer access for user: {}", userId, e);
            throw new AccessDeniedException("Error validating dealer access");
        }
    }

    private void validateImageType(String imageType) {
        if (!dealerImageMapper.isValidImageType(imageType)) {
            throw new DBException(ErrorEnum.IMAGE_TYPE_INVALID,
                    new RuntimeException("Invalid image type: " + imageType));
        }
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
   // public DealerImageDTO uploadDealerImage(String dealerCode, String imageType, MultipartFile file, Long userId)
    public DealerImageDTO uploadDealerImage(String dealerCode, String imageType, MultipartFile file, Long userId)

    throws Exception {
        try {
            logger.debug("Starting image upload process for dealer: {}, imageType: {}", dealerCode, imageType);

            // Validate user has access to this dealer using existing logic
            List<String> dealerCodes = dealerService.getUserAccessibleDealerCodes(userId.toString());

            // Simple validation
            if (!dealerCodes.contains(dealerCode)) {
                throw new AccessDeniedException("User not authorized for dealer: " + dealerCode);
            }

            // 1. Business validations
            logger.debug("Validating image type: {}", imageType);
            validateImageType(imageType);
            logger.debug("Image type validated successfully");

            logger.debug("Validating image format");
            validateImageFile(file);

            logger.debug("Validating dealer access for userId: {} and dealer code: {}", userId,dealerCode);
            validateDealerAccess(dealerCode, userId);
            logger.debug("Dealer access validated successfully");

            // 2. Store file using dedicated service
            logger.debug("Storing file: {}", file.getOriginalFilename());
            String storedFilePath = fileStorageService.storeFile(file, dealerCode, imageType);
            logger.debug("File stored successfully at: {}", storedFilePath);

            // 3. Save metadata to database
            logger.debug("Getting image type ID for: {}", imageType);
            Long imageTypeId = dealerImageMapper.getImageTypeIdByName(imageType);
            logger.debug("Retrieved image type ID: {}", imageTypeId);

            DealerImageDTO imageInfo = new DealerImageDTO();
            imageInfo.setFilePath(storedFilePath);
            imageInfo.setFileName(file.getOriginalFilename());
            imageInfo.setUploadedByUserId(userId);

            dealerImageMapper.insertImage(imageInfo);

            logger.debug("Successfully added image with ID: {}", imageInfo.getImageId());

            dealerImageMapper.insertDealerImage(imageInfo.getImageId(), dealerCode, imageTypeId);

            imageInfo.setMimeType(file.getContentType());
            imageInfo.setDealerCode(dealerCode);
            imageInfo.setImageTypeName(imageType);
            imageInfo.setUploadDate(LocalDateTime.now());
            logger.debug("Image info: {}", imageInfo);
            imageInfo.setImageData(file.getBytes());
            return imageInfo;

        } catch (IOException e) {
            logger.error("Error uploading image: {}", e.getMessage());
            throw new DBException(ErrorEnum.IMAGE_UPLOAD_ERROR, e);
        }
    }


    // Get Images //

    private String detectMimeType(String fileName) {
        if (fileName == null) {
            return "image/jpeg";  // Default to JPEG if no filename
        }

        String mimeType = FileUtils.detectMimeType(fileName);
        return mimeType != null ? mimeType : "image/jpeg";
    }

    @Transactional(readOnly = true)
    public List<DealerImageDTO> getDealerImages(String dealerCode, String imageType, Long userId)
            throws Exception {
        try {
            // Validate user has access to this dealer using existing logic
            List<String> dealerCodes = dealerService.getUserAccessibleDealerCodes(userId.toString());

            // Simple validation
            if (!dealerCodes.contains(dealerCode)) {
                throw new AccessDeniedException("User not authorized for dealer: " + dealerCode);
            }


            // Get image metadata from database
            List<DealerImageDTO> images;
            if (imageType != null) {
                Long imageTypeId = dealerImageMapper.getImageTypeIdByName(imageType);
                if (imageTypeId == null) {
                    throw new IllegalArgumentException("Invalid image type: " + imageType);
                }
                images = dealerImageMapper.findByDealerCodeAndType(dealerCode, imageTypeId);
            } else {
                images = dealerImageMapper.findByDealerCode(dealerCode);
            }


            // Load actual image data for each image
            for (DealerImageDTO image : images) {
                try {
                    logger.debug("File path: {}", image.getFilePath());
                    logger.debug("File name: {}", image.getFileName());

                    Path fullPath = Paths.get(baseImagePath, image.getFilePath());
                    logger.debug("Constructed Full Path: {}", fullPath);

                    if (Files.exists(fullPath)) {
                        byte[] imageData = Files.readAllBytes(fullPath);
                        image.setImageData(imageData);

                        // Add MIME type detection here
                        image.setMimeType(detectMimeType(image.getFileName()));
                    }

                } catch (IOException e) {
                    logger.error("Failed to load image file: {}", image.getFilePath(), e);
                    // You might want to set some flag or remove this image from the list
                    // depending on your requirements
                }
            }

            return images;

        } catch (AccessDeniedException e) {
            throw new DBException(ErrorEnum.IMAGE_ACCESS_DENIED, e);
        } catch (Exception e) {
            throw new DBException(ErrorEnum.GENERAL_INTERNAL_ERROR, e);
        }
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void deleteImage(Long imageId, Long userId) throws Exception {
        try {

            // Validate user has access to this dealer using existing logic
            List<String> dealerCodes = dealerService.getUserAccessibleDealerCodes(userId.toString());

            // One dealer image is always assoaciated with only one dealer
            String imageDealerCode =  dealerImageMapper.findDealerCodeByImageId(imageId);

            // Simple validation
            if (!dealerCodes.contains(imageDealerCode)) {
                throw new AccessDeniedException("User not authorized for dealer: " + imageDealerCode);
            }

            DealerImageDTO image = dealerImageMapper.findById(imageId);
            if (image != null) {
                Path completePath = Paths.get(baseImagePath, image.getFilePath());
                Files.deleteIfExists(completePath);

                // First you need to delete image from DEALER_IMAGE table and
                // after that you can delete it from IMAGE_INFO

                dealerImageMapper.deleteImageFromDealerImage(imageId);
                dealerImageMapper.deleteImageFromImageInfo(imageId);
            }
        } catch (Exception e) {
            logger.error("Error deleting image: {}", e.getMessage());
            throw new DBException(ErrorEnum.GENERAL_INTERNAL_ERROR, e);
        }
    }

}




//    private String createDealerDirectory(String dealerCode, String imageType) throws Exception {
//        String dealerPath = baseImagePath + "/" + dealerCode + "/" + imageType;
//        Files.createDirectories(Paths.get(dealerPath));
//        return dealerPath;
//    }
//
//
//    private Path saveFile(MultipartFile file, String dealerPath, String fileName) throws Exception {
//        Path filePath = Paths.get(dealerPath, fileName);
//        Files.write(filePath, file.getBytes());
//        return filePath;
//    }
//
//    private void saveImageMetadata(String dealerCode, Long imageTypeId, String filePath,
//                                   String fileName, String username) {
//        dealerImageMapper.insertImage(dealerCode, imageTypeId, filePath, fileName,
//                LocalDateTime.now(), username);
//    }








//    private void validateDealerAccess(String dealerCode, List<StockInventoryDTO> stockAccess) {
//        boolean hasAccess = stockAccess.stream()
//                .anyMatch(stock -> stock.getDealerCode().equals(dealerCode));
//        if (!hasAccess) {
//            throw new DBException(ErrorEnum.UNAUTHORIZED_ACCESS,
//                    new RuntimeException("User not authorized for dealer: " + dealerCode));
//        }
//    }
