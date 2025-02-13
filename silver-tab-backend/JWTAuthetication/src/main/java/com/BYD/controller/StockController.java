package com.BYD.controller;

import com.BYD.dto.stock.StockInventoryDTO;
import com.BYD.enums.ErrorEnum;
import com.BYD.model.*;
import com.BYD.service.StockService;
import com.BYD.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/stock")
public class StockController {

    private static final String TRUE = "true";
    private static final String ENCRYPT = "encrypt";
    private final StockService stockService;
    private final JwtService jwtService;

    @Autowired
    public StockController(StockService stockService, JwtService jwtService) {
        this.stockService = stockService;
        this.jwtService = jwtService;
    }

    @GetMapping("/allStocks")
    public ResponseEntity<List<StockInventoryDTO>> getAllStock(
            @RequestHeader(value = "Authorization", required = true) String authHeader) throws Exception {

            // Extract the JWT token from the Authorization header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix

            // Validate the token
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Get userId from the validated token
            Long userId = jwtService.getUserIdFromToken(token);

             if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Call the service method with the verified userId
            List<StockInventoryDTO> stockInventories = stockService.getStockInventoryByUserId(userId.toString());
            return ResponseEntity.ok(stockInventories);
    }
}