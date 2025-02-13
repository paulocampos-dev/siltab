package com.BYD.controller;

import com.BYD.dto.RegionDTO;
import com.BYD.mapper.UserAccessRegionMapper;
import com.BYD.service.ComercialPolicyService;
import com.BYD.service.DealerService;
import com.BYD.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
import java.time.YearMonth;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/comercial-policy")
public class ComercialPolicyController {

    private static final Logger logger = LoggerFactory.getLogger(ComercialPolicyController.class);
    private final JwtService jwtService;
    private final DealerService dealerService;
    private final UserAccessRegionMapper userAccessRegionMapper;

    @Autowired
    private ComercialPolicyService comercialPolicyService;

    public ComercialPolicyController(ComercialPolicyService comercialPolicyService, JwtService jwtService, DealerService dealerService, UserAccessRegionMapper userAccessRegionMapper) {
        this.comercialPolicyService = comercialPolicyService;
        this.jwtService = jwtService;
        this.dealerService = dealerService;
        this.userAccessRegionMapper = userAccessRegionMapper;
    }

    // Helper method to validate period parameter
    private boolean isValidPeriod(String period) {
        return period != null && (
                period.equals("monthly") ||
                        period.equals("quarterly") ||
                        period.equals("weekly")
        );
    }

    @GetMapping(value = "/national", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<?> getNationalReport(
            @RequestHeader(value = "Authorization", required = true) String authHeader,
            @RequestParam(required = true) @DateTimeFormat(pattern = "MM-yyyy") YearMonth from,
            @RequestParam(required = true) @DateTimeFormat(pattern = "MM-yyyy") YearMonth to,
            @RequestParam(required = true) String period) {

        try {
            logger.debug("Received request for national commercial policy report from {} to {} with period {}", from, to, period);

            // Validate period
            if (!isValidPeriod(period)) {
                logger.warn("Invalid period parameter: {}", period);
                return ResponseEntity.badRequest()
                        .body("Invalid period. Allowed values are: 'monthly', 'quarterly', 'weekly'");
            }

            // Validate Authorization header format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid Authorization header format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid authorization format");
            }

            String token = authHeader.substring(7);

            // Validate token
            if (!jwtService.validateToken(token)) {
                logger.warn("Invalid JWT token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            // Get user ID from token
            Long userId = jwtService.getUserIdFromToken(token);
            Long position = jwtService.getPositionFromToken(token);

            logger.debug("User position: {}", position);


            if (userId == null) {
                logger.warn("Unable to extract user ID from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token: user ID not found");
            }

            // Verify user position
            if (position == null || position != 4) {
                logger.warn("User {} attempted to access national report without proper position", userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied: You don't have permission to view the national report");
            }

            // Validate date range
            if (from.isAfter(to)) {
                logger.warn("Invalid date range: from date is after to date");
                return ResponseEntity.badRequest()
                        .body("Invalid date range: 'from' date must be before or equal to 'to' date");
            }

            // Get the HTML content with month-year range
            String htmlContent = comercialPolicyService.getNationalReport(from, to, period);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(htmlContent);

        } catch (Exception e) {
            logger.error("Error processing national report request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Error processing request: " + e.getMessage()));
        }
    }

    @GetMapping(value = "/regional", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRegionalReport(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(pattern = "MM-yyyy") YearMonth from,
            @RequestParam @DateTimeFormat(pattern = "MM-yyyy") YearMonth to,
            @RequestParam(required = true) String period) {

        try {
            logger.debug("Received request for regional report from {} to {} with period {}", from, to, period);

            // Validate period
            if (!isValidPeriod(period)) {
                logger.warn("Invalid period parameter: {}", period);
                return ResponseEntity.badRequest()
                        .body("Invalid period. Allowed values are: 'monthly', 'quarterly', 'weekly'");
            }

            // Validate and extract token
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid authorization format");
            }
            String token = authHeader.substring(7);

            // Validate token
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            // Get user details from token
            Long userId = jwtService.getUserIdFromToken(token);
            Long position = jwtService.getPositionFromToken(token);

            // Get authorized regions through service
            List<Integer> authorizedRegions = comercialPolicyService.getAuthorizedRegions(userId, position);
            logger.debug("Authorized regions: {}", authorizedRegions);
//
//            // Get region details
//            List<RegionDTO> regions = userAccessRegionMapper.findRegionsByIds(authorizedRegions);

            // Declare regions variable outside try block
            List<RegionDTO> regions;

            try {
                regions = userAccessRegionMapper.findRegionsByIds(authorizedRegions);
                logger.debug("Found regions: {}", regions);
            } catch (Exception e) {
                logger.error("Error finding regions by ids: {}", e.getMessage(), e);
                throw e;
            }

            // Generate report
            Map<Integer, String> reports = comercialPolicyService.getRegionalReport(from, to, authorizedRegions, period);

            // Transform to use region names
            Map<String, String> reportsByName = new HashMap<>();
            for (RegionDTO region : regions) {
                String report = reports.get(region.getRegionId());
                if (report != null) {
                    reportsByName.put(region.getRegionName(), report);
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(reportsByName);

        } catch (SecurityException e) {
            logger.warn("Security violation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid parameters: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing regional report request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing request: " + e.getMessage()));
        }
    }

    @GetMapping(value = "/dealer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDealerReport(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(pattern = "MM-yyyy") YearMonth from,
            @RequestParam @DateTimeFormat(pattern = "MM-yyyy") YearMonth to,
            @RequestParam(required = true) String period) {

        try {
            logger.debug("Received request for dealer report from {} to {} with period {}", from, to,period);

            // Validate period
            if (!isValidPeriod(period)) {
                logger.warn("Invalid period parameter: {}", period);
                return ResponseEntity.badRequest()
                        .body("Invalid period. Allowed values are: 'monthly', 'quarterly', 'weekly'");
            }

            // Validate and extract token
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid authorization format");
            }
            String token = authHeader.substring(7);

            // Validate token
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            // Get user details from token
            Long userId = jwtService.getUserIdFromToken(token);
            Long position = jwtService.getPositionFromToken(token);

            if (userId == null) {
                logger.warn("Unable to extract user ID from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token: user ID not found");
            }

            // Validate date range
            if (from.isAfter(to)) {
                logger.warn("Invalid date range: from date is after to date");
                return ResponseEntity.badRequest()
                        .body("Invalid date range: 'from' date must be before or equal to 'to' date");
            }

            // Check dealer access
            //List<String> accessibleDealerCodes = dealerService.getUserAccessibleDealerCodes(userId.toString());

            List<String> accessibleDealerCodes = comercialPolicyService.getUserAccessibleCommercialPolicyDealerCodes(userId.toString());


            logger.debug("Acessible Dealer Codes: {}",accessibleDealerCodes);

            // Create a list of dealer codes to exclude
            List<String> excludedDealers = Arrays.asList(
                    "BYDEUSE0001W",
                    "TEST00D10", "TEST00D11", "TEST00D12", "TEST00D13", "TEST00D14",
                    "TEST00D15", "TEST00D16", "TEST00D3", "TEST00D4", "TEST00D5",
                    "TEST00D6", "TEST00D7", "TEST00D8", "TEST00D9", "TEST01W"
            );

            List<Map<String, Object>> dealerResults = dealerService.getDealerNamesByCode(accessibleDealerCodes);

            // Create map of dealer code to dealer name
            Map<String, String> dealerNamesMap = new HashMap<>();
            for (Map<String, Object> result : dealerResults) {
                String dealerCode = (String) result.get("DEALER_CODE");
                String dealerName = (String) result.get("DEALER_NAME");
                dealerNamesMap.put(dealerCode, dealerName);
            }

            logger.debug("Dealer names map content:");
            dealerNamesMap.forEach((k, v) -> logger.debug("Key: {}, Value: {}", k, v));

            // Filter out the excluded dealers
            accessibleDealerCodes = accessibleDealerCodes.stream()
                    .filter(dealer -> !excludedDealers.contains(dealer))
                    .collect(Collectors.toList());

            if (accessibleDealerCodes.isEmpty()) {
                logger.warn("User {} has no accessible dealers", userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied: No dealers are assigned to your account");
            }

            // Generate report
            Map<String, String> dealerReports = comercialPolicyService.getDealerReport(from, to, accessibleDealerCodes, period);

            // Concatened Dealer code - Dealer name with Dealer Report

            // Then use properDealerMap in your forEach
            Map<String, String> enhancedDealerReports = new LinkedHashMap<>();
            dealerReports.forEach((dealerCode, value) -> {
                String dealerName = dealerNamesMap.getOrDefault(dealerCode, "Unknown Dealer");
                String enhancedKey = dealerCode + " - " + dealerName;
                enhancedDealerReports.put(enhancedKey, value);
            });

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(enhancedDealerReports);  // Return the Map directly instead of joining values

        } catch (SecurityException e) {
            logger.warn("Security violation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid parameters: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing dealer report request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing request: " + e.getMessage()));
        }
    }

    @GetMapping("/available-reports")
    public ResponseEntity<?> getAccessLevels(
            @RequestHeader(value = "Authorization", required = true) String authHeader) {

        try {
            logger.debug("Received request for access levels check");

            // Validate Authorization header format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Invalid Authorization header format");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid authorization format");
            }

            String token = authHeader.substring(7);

            // Validate token
            if (!jwtService.validateToken(token)) {
                logger.warn("Invalid JWT token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            // Get user ID from token
            Long userId = jwtService.getUserIdFromToken(token);
            Long position = jwtService.getPositionFromToken(token);

            if (userId == null) {
                logger.warn("Unable to extract user ID from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token: user ID not found");
            }

            // Check national access
            boolean hasNationalAccess = (position != null && position == 4);

            // Check regional access using existing service method
            List<Integer> accessibleRegions = comercialPolicyService.getAuthorizedRegions(userId, position);

            // Check dealer access
            List<String> accessibleDealers = dealerService.getUserAccessibleDealerCodes(userId.toString());

            // Construct response
            Map<String, Object> response = new HashMap<>();
            response.put("nationalAccess", hasNationalAccess);
            response.put("regionalAccess", accessibleRegions.isEmpty() ? false : accessibleRegions);
            response.put("dealerAccess", accessibleDealers.isEmpty() ? false : accessibleDealers);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing access levels request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing request: " + e.getMessage()));
        }
    }
}