package com.BYD.service;

import com.BYD.dto.dealer.DealerInfoDTO;
import com.BYD.dto.dealer.ConciseDealerInfoDTO;
import com.BYD.enums.ErrorEnum;
import com.BYD.exception.DBException;
import com.BYD.mapper.DealerMapper;
import com.BYD.mapper.StockMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DealerService {
    private static final Logger logger = LoggerFactory.getLogger(DealerService.class);

    private final DealerMapper dealerMapper;
    private final StockMapper stockMapper;

    public DealerService(DealerMapper dealerMapper, StockMapper stockMapper) {
        this.dealerMapper = dealerMapper;
        this.stockMapper = stockMapper;
    }

    @Transactional(readOnly = true)
    public DealerInfoDTO getDealerInfo(String userId, String dealerCode) {
        try {
            logger.debug("Processing dealer info request for userId: {} and dealerCode: {}", userId, dealerCode);

            if (userId == null || userId.trim().isEmpty()) {
                throw new DBException(ErrorEnum.UNAUTHORIZED_ACCESS,
                        new RuntimeException("User ID is required"));
            }

            if (dealerCode == null || dealerCode.trim().isEmpty()) {
                throw new DBException(ErrorEnum.EMPTY_REQUEST_BODY,
                        new RuntimeException("Dealer code is required"));
            }

            // Get user's position ID
            Integer positionId = stockMapper.getUserPosition(userId);
            logger.debug("Retrieved position ID: {} for user: {}", positionId, userId);

            if (positionId == null) {
                throw new DBException(ErrorEnum.UNAUTHORIZED_ACCESS,
                        new RuntimeException("User position not found"));
            }

            // Check if user has access to this dealer
            boolean hasAccess = dealerMapper.checkDealerAccess(userId, dealerCode, positionId);
            if (!hasAccess) {
                logger.warn("User {} does not have access to dealer {}", userId, dealerCode);
                throw new SecurityException("User does not have access to this dealer's information");
            }

            // Get dealer information
            DealerInfoDTO dealerInfo = dealerMapper.getDealerInfo(dealerCode);

            if (dealerInfo == null) {
                logger.info("No dealer information found for dealer code: {}", dealerCode);
            }

            return dealerInfo;

        } catch (SecurityException e) {
            logger.error("Security violation: {}", e.getMessage());
            throw e;
        } catch (DBException e) {
            logger.error("Database error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            throw new DBException(ErrorEnum.GENERAL_INTERNAL_ERROR, e);
        }
    }

    @Transactional(readOnly = true)
    public List<String> getUserAccessibleDealerCodes(String userId) {
        Integer positionId = stockMapper.getUserPosition(userId);
        if (positionId == null) {
            throw new RuntimeException("User position not found");
        }
        return dealerMapper.getUserAccessibleDealers(userId, positionId);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDealerNamesByCode(List<String> accessibleDealerCodes) {
        if (accessibleDealerCodes == null || accessibleDealerCodes.isEmpty()) {
            return new ArrayList<>();
        }
        return dealerMapper.getDealerNamesByCode(accessibleDealerCodes);
    }

    @Transactional(readOnly = true)
    public List<ConciseDealerInfoDTO> getUserAccessibleStoresSummary(String userId) {
        Integer positionId = stockMapper.getUserPosition(userId);
        if (positionId == null) {
            throw new RuntimeException("User position not found");
        }
        return dealerMapper.getUserAccessibleDealersSummary(userId, positionId);
    }
}