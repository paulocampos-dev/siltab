package com.BYD.dto.stock;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class StockTransactionDTO {
    private String materialCode;
    private Integer isFreeStock;
    private String transactionDirection;
    private String transactionType;
    private Double incomingQuantity;
    private Double outputQuantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime occurDate;

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public Integer getIsFreeStock() {
        return isFreeStock;
    }

    public void setIsFreeStock(Integer isFreeStock) {
        this.isFreeStock = isFreeStock;
    }

    public String getTransactionDirection() {
        return transactionDirection;
    }

    public void setTransactionDirection(String transactionDirection) {
        this.transactionDirection = transactionDirection;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getIncomingQuantity() {
        return incomingQuantity;
    }

    public void setIncomingQuantity(Double incomingQuantity) {
        this.incomingQuantity = incomingQuantity;
    }

    public Double getOutputQuantity() {
        return outputQuantity;
    }

    public void setOutputQuantity(Double outputQuantity) {
        this.outputQuantity = outputQuantity;
    }

    public LocalDateTime getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(LocalDateTime occurDate) {
        this.occurDate = occurDate;
    }
}
