package com.BYD.service;

import com.BYD.mapper.DealerMapper;
import com.BYD.mapper.StockMapper;
import com.BYD.mapper.UserAccessRegionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class ComercialPolicyService {
    private static final Logger logger = LoggerFactory.getLogger(ComercialPolicyService.class);

    private final UserAccessRegionMapper userAccessRegionMapper;
    private final RestTemplate restTemplate;
    private final DealerMapper dealerMapper;
    private final StockMapper stockMapper;

    @Value("${python.service.url}")
    private String pythonServiceUrl;

    @Autowired
    public ComercialPolicyService(UserAccessRegionMapper userAccessRegionMapper, RestTemplate restTemplate, DealerMapper dealerMapper,StockMapper stockMapper) {
        this.userAccessRegionMapper = userAccessRegionMapper;
        this.restTemplate = restTemplate;
        this.dealerMapper = dealerMapper;
        this.stockMapper = stockMapper;

    }

    public List<Integer> getAuthorizedRegions(Long userId, Long position) {
        logger.debug("Checking region access for user {} with position {}", userId, position);

        if (position == null) {
            logger.error("Position is null for user {}", userId);
            throw new SecurityException("User position not found");
        }

        // Only positions 2 and 4 can access regional reports
        if (position != 2 && position != 4) {
            logger.warn("User {} with position {} attempted to access regional report", userId, position);
            throw new SecurityException("Insufficient permissions to access regional reports");
        }

        // National level (position 4) has access to all regions
        if (position == 4) {
            return userAccessRegionMapper.findAllRegionIds();
        }

        // Regional supervisor (position 2) has access to assigned regions
        List<Integer> authorizedRegions = userAccessRegionMapper.findRegionIdsByUserId(userId);
        if (authorizedRegions == null || authorizedRegions.isEmpty()) {
            logger.warn("No regions assigned to user {} with position {}", userId, position);
            throw new SecurityException("No regions assigned to your account");
        }

        logger.debug("User {} has access to regions: {}", userId, authorizedRegions);
        return authorizedRegions;
    }

    public String getNationalReport(YearMonth from, YearMonth to, String period) throws IOException {
        try {
            // Format the dates as required (MM-yyyy)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
            String startDate = from.format(formatter);
            String endDate = to.format(formatter);

            // Build the URL with query parameters
            String url = UriComponentsBuilder.fromHttpUrl(pythonServiceUrl + "/generate_report")
                    .queryParam("level", "national")
                    .queryParam("start_date", startDate)
                    .queryParam("end_date", endDate)
                    .queryParam("period", period)
                    .build()
                    .toUriString();

            logger.debug("Requesting National report from Python service: {}", url);

            // Make the HTTP request
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                logger.error("Failed to get report from Python service. Status: {}", response.getStatusCode());
                throw new IOException("Failed to get report from Python service");
            }

        } catch (Exception e) {
            logger.error("Error getting national report: {}", e.getMessage());
            throw new IOException("Error getting national report", e);
        }
    }

    public Map<Integer, String> getRegionalReport(YearMonth from, YearMonth to, List<Integer> regions, String period) throws IOException {
        // Validate date range first
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'From' date must be before or equal to 'to' date");
        }


        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
            String startDate = from.format(formatter);
            String endDate = to.format(formatter);

            // Map to store region -> report mapping
            Map<Integer, String> regionReports = new HashMap<>();
            List<Integer> failedRegions = new ArrayList<>();

            // Create and execute futures for each region
            Map<Integer, CompletableFuture<String>> futures = regions.stream()
                    .collect(Collectors.toMap(
                            region -> region,
                            region -> CompletableFuture.supplyAsync(() -> {
                                try {
                                    String url = UriComponentsBuilder.fromHttpUrl(pythonServiceUrl + "/generate_report")
                                            .queryParam("level", "regional")
                                            .queryParam("start_date", startDate)
                                            .queryParam("end_date", endDate)
                                            .queryParam("dealer_region", region)
                                            .queryParam("period", period)
                                            .build()
                                            .toUriString();

                                    logger.debug("Requesting report for region {}: {}", region, url);

                                    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                                        return response.getBody();
                                    }
                                    throw new RuntimeException("Failed to get report for region " + region);
                                } catch (Exception e) {
                                    logger.error("Error getting report for region {}: {}", region, e.getMessage());
                                    failedRegions.add(region);
                                    return ""; // Return empty string for failed regions
                                }
                            })
                    ));

            // Wait for all futures to complete with timeout
            try {
                CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0]))
                        .get(1800, TimeUnit.SECONDS);

                // Collect results
                futures.forEach((region, future) -> {
                    try {
                        String result = future.get();
                        if (!result.isEmpty()) {
                            regionReports.put(region, result);
                        }
                    } catch (Exception e) {
                        logger.error("Error collecting result for region {}: {}", region, e.getMessage());
                    }
                });

                if (regionReports.isEmpty()) {
                    throw new IOException("Failed to generate any regional reports");
                }

                // If there were any failed regions, add an error message to the map
                if (!failedRegions.isEmpty()) {
                    String errorMessage = String.format(
                            "<div class='report-error'>Failed to generate reports for regions: %s</div>",
                            String.join(", ", failedRegions.stream().map(String::valueOf).collect(Collectors.toList()))
                    );
                    regionReports.put(-1, errorMessage); // Using -1 as a special key for error messages
                }

                return regionReports;

            } catch (TimeoutException e) {
                logger.error("Timeout while waiting for reports: {}", e.getMessage());
                throw new IOException("Timeout while generating reports. Please try again or request fewer regions.", e);
            }

        } catch (Exception e) {
            logger.error("Error getting regional reports: {}", e.getMessage());
            throw new IOException("Error getting regional reports: " + e.getMessage(), e);
        }
    }

    public Map<String, String> getDealerReport(YearMonth from, YearMonth to, List<String> dealerCodes, String period) throws IOException {
        // Validate date range first
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'From' date must be before or equal to 'to' date");
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
            String startDate = from.format(formatter);
            String endDate = to.format(formatter);

            // Map to store dealer -> report mapping
            Map<String, String> dealerReports = new HashMap<>();
            List<String> failedDealers = new ArrayList<>();

            // Create and execute futures for each dealer
            Map<String, CompletableFuture<String>> futures = dealerCodes.stream()
                    .collect(Collectors.toMap(
                            dealerCode -> dealerCode,
                            dealerCode -> CompletableFuture.supplyAsync(() -> {
                                try {
                                    String url = UriComponentsBuilder.fromHttpUrl(pythonServiceUrl + "/generate_report")
                                            .queryParam("level", "dealer")
                                            .queryParam("start_date", startDate)
                                            .queryParam("end_date", endDate)
                                            .queryParam("dealer_region", dealerCode)
                                            .queryParam("period", period)
                                            .build()
                                            .toUriString();

                                    logger.debug("Requesting report for dealer {}: {}", dealerCode, url);

                                    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                                        return response.getBody();
                                    }
                                    throw new RuntimeException("Failed to get report for dealer " + dealerCode);
                                } catch (Exception e) {
                                    logger.error("Error getting report for dealer {}: {}", dealerCode, e.getMessage());
                                    failedDealers.add(dealerCode);
                                    return ""; // Return empty string for failed dealers
                                }
                            })
                    ));

            // Wait for all futures to complete with timeout
            try {
                CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0]))
                        .get(1800, TimeUnit.SECONDS);

                // Collect results
                futures.forEach((dealerCode, future) -> {
                    try {
                        String result = future.get();
                        if (!result.isEmpty()) {
                            dealerReports.put(dealerCode, result);
                        }
                    } catch (Exception e) {
                        logger.error("Error collecting result for dealer {}: {}", dealerCode, e.getMessage());
                    }
                });

                if (dealerReports.isEmpty()) {
                    throw new IOException("Failed to generate any dealer reports");
                }

                // If there were any failed dealers, add an error message
                if (!failedDealers.isEmpty()) {
                    String errorMessage = String.format(
                            "<div class='report-error'>Failed to generate reports for dealers: %s</div>",
                            String.join(", ", failedDealers)
                    );
                    dealerReports.put("error", errorMessage);
                }

                return dealerReports;

            } catch (TimeoutException e) {
                logger.error("Timeout while waiting for reports: {}", e.getMessage());
                throw new IOException("Timeout while generating reports. Please try again or request fewer dealers.", e);
            }

        } catch (Exception e) {
            logger.error("Error getting dealer reports: {}", e.getMessage());
            throw new IOException("Error getting dealer reports: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<String> getUserAccessibleCommercialPolicyDealerCodes(String userId) {
        Integer positionId = stockMapper.getUserPosition(userId);
        if (positionId == null) {
            throw new RuntimeException("User position not found");
        }
        return dealerMapper.getUserAccessibleCommercialPolicyDealerCodes(userId, positionId);
    }

}