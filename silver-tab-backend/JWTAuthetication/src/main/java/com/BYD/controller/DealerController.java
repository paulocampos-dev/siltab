package com.BYD.controller;

import com.BYD.dto.dealer.DealerImageDTO;
import com.BYD.dto.dealer.DealerInfoDTO;
import com.BYD.dto.dealer.ConciseDealerInfoDTO;
import com.BYD.service.DealerService;
import com.BYD.service.JwtService;
import com.BYD.service.DealerImageService;
import com.BYD.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dealer")
public class DealerController {

    private static final Logger logger = LoggerFactory.getLogger(DealerController.class);
    private final DealerService dealerService;
    private final JwtService jwtService;
    private final DealerImageService dealerImageService;


    public DealerController(DealerService dealerService, JwtService jwtService, DealerImageService dealerImageService) {
        this.dealerService = dealerService;
        this.jwtService = jwtService;
        this.dealerImageService = dealerImageService;
    }

    @GetMapping("/{dealerCode}")
    public ResponseEntity<?> getDealerInfo(
            @RequestHeader(value = "Authorization", required = true) String authHeader,
            @PathVariable("dealerCode") String dealerCode) {

        try {
            logger.debug("Received request for dealer info. Dealer code: {}", dealerCode);

            // Add dealerCode validation first
            if (dealerCode == null || dealerCode.trim().isEmpty() || "null".equalsIgnoreCase(dealerCode)) {
                logger.warn("Invalid dealer code received: {}", dealerCode);
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Dealer code cannot be null or empty"));
            }

            // Validate Authorization header format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid Authorization header format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid authorization format"));
            }

            String token = authHeader.substring(7);

            // Validate token
            if (!jwtService.validateToken(token)) {
                logger.warn("Invalid JWT token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Get user ID from token
            Long userId = jwtService.getUserIdFromToken(token);
            if (userId == null) {
                logger.warn("Unable to extract user ID from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token: user ID not found"));
            }

            // Get dealer information with authorization check
            DealerInfoDTO dealerInfo = dealerService.getDealerInfo(userId.toString(), dealerCode);

            if (dealerInfo == null) {
                logger.info("No dealer information found for dealer code: {}", dealerCode);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(dealerInfo);

        } catch (SecurityException e) {
            logger.error("Security violation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You don't have permission to access this dealer's information"));
        } catch (Exception e) {
            logger.error("Error processing dealer info request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing request: " + e.getMessage()));
        }
    }

    @GetMapping("/user/dealer-code")
    public ResponseEntity<?> getUserAccessibleStores(
            @RequestHeader(value = "Authorization", required = true) String authHeader) {
        try {
            logger.debug("Received request for user accessible stores");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid Authorization header format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid authorization format"));
            }

            String token = authHeader.substring(7);
            if (!jwtService.validateToken(token)) {
                logger.warn("Invalid JWT token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            Long userId = jwtService.getUserIdFromToken(token);

            List<String> dealerCodes = dealerService.getUserAccessibleDealerCodes(userId.toString());

            logger.debug("Retrieved {} accessible stores for user",
                    dealerCodes != null ? dealerCodes.size() : 0);

            return ResponseEntity.ok(dealerCodes);

        } catch (Exception e) {
            logger.error("Error getting user accessible stores: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing request: " + e.getMessage()));
        }
    }

    @GetMapping("/user/dealer-summary")
    public ResponseEntity<?> getUserAccessibleStoresSummary(
            @RequestHeader(value = "Authorization", required = true) String authHeader) {
        try {
            logger.debug("Received request for user accessible stores");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid Authorization header format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid authorization format"));
            }

            String token = authHeader.substring(7);
            if (!jwtService.validateToken(token)) {
                logger.warn("Invalid JWT token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            Long userId = jwtService.getUserIdFromToken(token);

            List<ConciseDealerInfoDTO> dealerSummary = dealerService.getUserAccessibleStoresSummary(userId.toString());

            logger.debug("Retrieved {} accessible stores for user",
                    dealerSummary != null ? dealerSummary.size() : 0);

            return ResponseEntity.ok(dealerSummary);

        } catch (Exception e) {
            logger.error("Error getting user accessible stores: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing request: " + e.getMessage()));
        }
    }

    @GetMapping("/image/{dealerCode}")
    public ResponseEntity<?> getDealerImages(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String dealerCode,
            @RequestParam(required = false) String imageType) {

        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Invalid token");
            }

            String token = authHeader.substring(7);
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }

            Long userId = jwtService.getUserIdFromToken(token);
            List<DealerImageDTO> images = dealerImageService.getDealerImages(dealerCode, imageType, userId);

            if (images.isEmpty()) {
                return ResponseEntity.ok().body(Collections.emptyList());
            }
            return ResponseEntity.ok(images);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/image/{dealerCode}")
    public ResponseEntity<?> uploadDealerImage(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String dealerCode,
            @RequestParam("imageType") String imageType,
            @RequestParam("file") MultipartFile file) {

        try {
            // Token validation - keeping the same pattern
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Invalid token");
            }

            String token = authHeader.substring(7);
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }

            Long userId = jwtService.getUserIdFromToken(token);


            logger.debug("Starting upload process");
            // Upload image and get the DTO response
          //DealerImageDTO uploadedImage = dealerImageService.uploadDealerImage(dealerCode, imageType, file, userId);

            DealerImageDTO imageInfo =  dealerImageService.uploadDealerImage(dealerCode, imageType, file, userId);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(imageInfo);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error uploading image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading image");
        }
    }

        @DeleteMapping("/image/{imageId}")
    public ResponseEntity<?> deleteImage(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long imageId) {

        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Invalid token");
            }

            String token = authHeader.substring(7);
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }

            Long userId = jwtService.getUserIdFromToken(token);

            dealerImageService.deleteImage(imageId,userId);

            return ResponseEntity.ok("Image deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PostMapping(value = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> uploadImage(
//            @RequestHeader("Authorization") String authHeader,
//            @RequestParam(value = "file", required = true) MultipartFile file,
//            @RequestParam("dealerCode") String dealerCode,
//            @RequestParam("imageType") String imageType) {
//
//        try {
//            // Add debug logging
//            logger.debug("Received file upload request:");
//            logger.debug("File name: " + (file != null ? file.getOriginalFilename() : "null"));
//            logger.debug("File size: " + (file != null ? file.getSize() : "null"));
//            logger.debug("Dealer code: " + dealerCode);
//            logger.debug("Image type: " + imageType);
//
//            if (file != null) {
//                logger.debug("file name: {}", file.getOriginalFilename());
//                logger.debug("file size: {}", file.getSize());
//            }
//
//            if (!authHeader.startsWith("Bearer ")) {
//                return ResponseEntity.status(401).body("Invalid token");
//            }
//
//            String token = authHeader.substring(7);
//            if (!jwtService.validateToken(token)) {
//                return ResponseEntity.status(401).body("Invalid token");
//            }
//
//            String username = jwtService.getUsernameFromToken(token);
//            dealerImageService.uploadImage(dealerCode, imageType, file, username);
//
//            return ResponseEntity.ok("Image uploaded successfully");
//        } catch (Exception e) {
//            logger.error("Error in upload:", e);
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }


}