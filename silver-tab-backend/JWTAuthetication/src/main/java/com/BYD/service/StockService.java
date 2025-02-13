package com.BYD.service;

import com.BYD.dto.stock.StockInventoryDTO;
import com.BYD.enums.ErrorEnum;
import com.BYD.exception.DBException;
import com.BYD.mapper.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class StockService {

    @Autowired StockMapper stockMapper;


    public StockService(StockMapper stockMapper) {
        this.stockMapper = stockMapper;
    }

    // Need to Rename method to better reflect its functionality


    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public List<StockInventoryDTO> getStockInventoryByUserId(String userId) throws Exception {
        try {
            System.out.println("Processing request for userId: " + userId);
            if (userId == null || userId.trim().isEmpty()) {
                throw new DBException(ErrorEnum.UNAUTHORIZED_ACCESS,
                        new RuntimeException("User ID is required"));
            }

            Integer positionId = stockMapper.getUserPosition(userId);
            System.out.println("Retrieved position ID: " + positionId);

            if (positionId == null) {
                throw new DBException(ErrorEnum.UNAUTHORIZED_ACCESS,
                        new RuntimeException("User position not found"));
            }

            try {
                return stockMapper.getStockInventoryByPosition(userId, positionId);
            } catch (Exception e) {
                System.err.println("Error executing getStockInventoryByPosition: " + e.getMessage());
                e.printStackTrace();
                throw new DBException(ErrorEnum.GENERAL_INTERNAL_ERROR, e);
            }

        } catch (DBException ex) {
            throw ex;
        } catch (Exception ex) {
            System.err.println("Unexpected error: " + ex.getMessage());
            ex.printStackTrace();
            throw new DBException(ErrorEnum.GENERAL_INTERNAL_ERROR, ex);
        }
    }







}
