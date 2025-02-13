package com.BYD.dto.stock;

import com.BYD.dto.dealer.DealerInfoDTO;

import java.util.ArrayList;
import java.util.List;

public class StockInventoryDTO {
    private String dealerCode;
    private String sparePartsCode;
    private String sapSparePartsName;
    private String storageLocation;
    private String vehicleSeries;
    private Double bookStock;
    private Double actualStock;
    private List<StockTransactionDTO> transactionDTOList = new ArrayList<>();
    private DealerInfoDTO dealerInfoDTO;

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public String getSparePartsCode() {
        return sparePartsCode;
    }

    public void setSparePartsCode(String sparePartsCode) {
        this.sparePartsCode = sparePartsCode;
    }

    public String getSapSparePartsName() {
        return sapSparePartsName;
    }

    public void setSapSparePartsName(String sapSparePartsName) {
        this.sapSparePartsName = sapSparePartsName;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getVehicleSeries() {
        return vehicleSeries;
    }

    public void setVehicleSeries(String vehicleSeries) {
        this.vehicleSeries = vehicleSeries;
    }

    public Double getBookStock() {
        return bookStock;
    }

    public void setBookStock(Double bookStock) {
        this.bookStock = bookStock;
    }

    public Double getActualStock() {
        return actualStock;
    }

    public void setActualStock(Double actualStock) {
        this.actualStock = actualStock;
    }

    public List<StockTransactionDTO> getTransactionDTOList() {
        return transactionDTOList;
    }

    public void setTransactionDTOList(List<StockTransactionDTO> transactionDTOList) {
        this.transactionDTOList = transactionDTOList;
    }

    public DealerInfoDTO getDealerInfoDTO() {
        return dealerInfoDTO;
    }

    public void setDealerInfoDTO(DealerInfoDTO dealerInfoDTO) {
        this.dealerInfoDTO = dealerInfoDTO;
    }
}
