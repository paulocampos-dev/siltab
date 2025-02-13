package com.BYD.dto.dealer;

public class DealerInfoDTO {

    //Service Network Information

    private String dealerNumber;
    private String dealerCode;
    private String dealerName;
    private String corporateName;
    private String groupName;  // From DEALER_GROUP table
    private String region;     // From BYD_REGION table
    private String regionalManagerName;
    private String state;      // From BRAZIL_STATE table
    private String city;       // From BRAZIL_CITY table
    private String address;
    private String country;
    private Integer passedTraining;
    private Double longitude;
    private Double latitude;
    private String contactNumber;
    private String email;

    // Operation Status

    private String operationTypeName;
    private String operationStatusName;
    private String operationClassName;
    private String operationServiceScopeName;
    private String operationDate;
    private String operationAfterSale;
    private String operationAfterSaleNote;


    // System use
    private String dealerUseDms;
    private String dealerUseLms;
    private String dealerUseTis;
    private String dealerUseCsiPortal;
    private String dealerUseBydPortal;

    // Key Position Person Quantity
    private String afterSalesManagerQuantity;
    private String serviceAdvisorQuantity;
    private String warrantyClaimSpecialistQuantity;
    private String techniciansQuantity;
    private String workshopLeaderQuantity;
    private String sparePartsSpecialistQuantity;

    // Facilities and tools
    private String totalArea;
    private String workshopArea;
    private String totalBox;
    private String boxWithLift;
    private String boxWithoutLift;
    private String acChargerQuantity;
    private String dcChargerQuantity;
    private String statusVds;
    private String batteryPool;
    private String bodyRepairWorkshopArea;
    private String bodyRepairStationArea;
    private String warehouseArea;
    private String powerBatteryTurnoverRoomArea;
    private String receptionArea;
    private String customerLoungeArea;
    private String customerToiletArea;
    private String chargingArea;
    private String powertrainRepairRoomArea;
    private String mobileFireWaterPoolArea;
    private String serviceDirectorOfficeArea;
    private String businessDevelopmentCenterArea;
    private String meetingTrainingRoomArea;
    private String workshopOfficeArea;
    private String staffLoungeArea;
    private String toolRoomArea;
    private String wasteOilArea;
    private String electricityDistributionRoomArea;
    private String airCompressorRoomArea;
    private String workshopToiletArea;
    private String paintStorageBlendingRoomArea;
    private String carWashingArea;
    private String claimPartsStorageRoomArea;
    private String financeOfficeArea;
    private String insuranceClaimArea;
    private String disassembledPartsRoomArea;
    private String usedPartsStorageArea;


    // Getters and Setters
    public String getDealerNumber() {
        return dealerNumber;
    }

    public void setDealerNumber(String dealerNumber) {
        this.dealerNumber = dealerNumber;
    }

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionalManagerName() {
        return regionalManagerName;
    }

    public void setRegionalManagerName(String regionalManagerName) {
        this.regionalManagerName = regionalManagerName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getPassedTraining() {
        return passedTraining;
    }

    public void setPassedTraining(Integer passedTraining) {
        this.passedTraining = passedTraining;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
    }

    public String getOperationTypeName() {
        return operationTypeName;
    }

    public void setOperationTypeName(String operationTypeName) {
        this.operationTypeName = operationTypeName;
    }

    public String getOperationStatusName() {
        return operationStatusName;
    }

    public void setOperationStatusName(String operationStatusName) {
        this.operationStatusName = operationStatusName;
    }

    public String getOperationClassName() {
        return operationClassName;
    }

    public void setOperationClassName(String operationClassName) {
        this.operationClassName = operationClassName;
    }

    public String getOperationServiceScopeName() {
        return operationServiceScopeName;
    }

    public void setOperationServiceScopeName(String operationServiceScopeName) { this.operationServiceScopeName = operationServiceScopeName; }

    public String getOperationAfterSale() {
        return operationAfterSale;
    }

    public void setOperationAfterSale(String operationAfterSale) {
        this.operationAfterSale = operationAfterSale;
    }

    public String getOperationAfterSaleNote() {

        return operationAfterSaleNote;
    }

    public void setOperationAfterSaleNote(String operationAfterSaleNote) {
        this.operationAfterSaleNote = operationAfterSaleNote;
    }

    public String getDealerUseDms() {
        return dealerUseDms;
    }

    public void setDealerUseDms(String dealerUseDms) {
        this.dealerUseDms = dealerUseDms;
    }

    public String getDealerUseLms() {
        return dealerUseLms;
    }

    public void setDealerUseLms(String dealerUseLms) {
        this.dealerUseLms = dealerUseLms;
    }

    public String getDealerUseTis() {
        return dealerUseTis;
    }

    public void setDealerUseTis(String dealerUseTis) {
        this.dealerUseTis = dealerUseTis;
    }

    public String getDealerUseCsiPortal() {
        return dealerUseCsiPortal;
    }

    public void setDealerUseCsiPortal(String dealerUseCsiPortal) {
        this.dealerUseCsiPortal = dealerUseCsiPortal;
    }

    public String getDealerUseBydPortal() {
        return dealerUseBydPortal;
    }

    public void setDealerUseBydPortal(String dealerUseBydPortal) {
        this.dealerUseBydPortal = dealerUseBydPortal;
    }

    public String getAfterSalesManagerQuantity() {
        return afterSalesManagerQuantity;
    }

    public void setAfterSalesManagerQuantity(String afterSalesManagerQuantity) {
        this.afterSalesManagerQuantity = afterSalesManagerQuantity;
    }

    public String getServiceAdvisorQuantity() {
        return serviceAdvisorQuantity;
    }

    public void setServiceAdvisorQuantity(String serviceAdvisorQuantity) {
        this.serviceAdvisorQuantity = serviceAdvisorQuantity;
    }

    public String getWarrantyClaimSpecialistQuantity() {
        return warrantyClaimSpecialistQuantity;
    }

    public void setWarrantyClaimSpecialistQuantity(String warrantyClaimSpecialistQuantity) {
        this.warrantyClaimSpecialistQuantity = warrantyClaimSpecialistQuantity;
    }

    public String getTechniciansQuantity() {
        return techniciansQuantity;
    }

    public void setTechniciansQuantity(String techniciansQuantity) {
        this.techniciansQuantity = techniciansQuantity;
    }

    public String getWorkshopLeaderQuantity() {
        return workshopLeaderQuantity;
    }

    public void setWorkshopLeaderQuantity(String workshopLeaderQuantity) {
        this.workshopLeaderQuantity = workshopLeaderQuantity;
    }

    public String getSparePartsSpecialistQuantity() {
        return sparePartsSpecialistQuantity;
    }

    public void setSparePartsSpecialistQuantity(String sparePartsSpecialistQuantity) {
        this.sparePartsSpecialistQuantity = sparePartsSpecialistQuantity;
    }

    public String getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(String totalArea) {
        this.totalArea = totalArea;
    }

    public String getWorkshopArea() {
        return workshopArea;
    }

    public void setWorkshopArea(String workshopArea) {
        this.workshopArea = workshopArea;
    }

    public String getTotalBox() {
        return totalBox;
    }

    public void setTotalBox(String totalBox) {
        this.totalBox = totalBox;
    }

    public String getBoxWithLift() {
        return boxWithLift;
    }

    public void setBoxWithLift(String boxWithLift) {
        this.boxWithLift = boxWithLift;
    }

    public String getBoxWithoutLift() {
        return boxWithoutLift;
    }

    public void setBoxWithoutLift(String boxWithoutLift) {
        this.boxWithoutLift = boxWithoutLift;
    }

    public String getAcChargerQuantity() {
        return acChargerQuantity;
    }

    public void setAcChargerQuantity(String acChargerQuantity) {
        this.acChargerQuantity = acChargerQuantity;
    }

    public String getDcChargerQuantity() {
        return dcChargerQuantity;
    }

    public void setDcChargerQuantity(String dcChargerQuantity) {
        this.dcChargerQuantity = dcChargerQuantity;
    }

    public String getStatusVds() {
        return statusVds;
    }

    public void setStatusVds(String statusVds) {
        this.statusVds = statusVds;
    }

    public String getBatteryPool() {
        return batteryPool;
    }

    public void setBatteryPool(String batteryPool) {
        this.batteryPool = batteryPool;
    }

    public String getBodyRepairWorkshopArea() {
        return bodyRepairWorkshopArea;
    }

    public void setBodyRepairWorkshopArea(String bodyRepairWorkshopArea) {
        this.bodyRepairWorkshopArea = bodyRepairWorkshopArea;
    }

    public String getBodyRepairStationArea() {
        return bodyRepairStationArea;
    }

    public void setBodyRepairStationArea(String bodyRepairStationArea) {
        this.bodyRepairStationArea = bodyRepairStationArea;
    }

    public String getWarehouseArea() {
        return warehouseArea;
    }

    public void setWarehouseArea(String warehouseArea) {
        this.warehouseArea = warehouseArea;
    }

    public String getPowerBatteryTurnoverRoomArea() {
        return powerBatteryTurnoverRoomArea;
    }

    public void setPowerBatteryTurnoverRoomArea(String powerBatteryTurnoverRoomArea) {
        this.powerBatteryTurnoverRoomArea = powerBatteryTurnoverRoomArea;
    }

    public String getReceptionArea() {
        return receptionArea;
    }

    public void setReceptionArea(String receptionArea) {
        this.receptionArea = receptionArea;
    }

    public String getCustomerLoungeArea() {
        return customerLoungeArea;
    }

    public void setCustomerLoungeArea(String customerLoungeArea) {
        this.customerLoungeArea = customerLoungeArea;
    }

    public String getCustomerToiletArea() {
        return customerToiletArea;
    }

    public void setCustomerToiletArea(String customerToiletArea) {
        this.customerToiletArea = customerToiletArea;
    }

    public String getChargingArea() {
        return chargingArea;
    }

    public void setChargingArea(String chargingArea) {
        this.chargingArea = chargingArea;
    }

    public String getPowertrainRepairRoomArea() {
        return powertrainRepairRoomArea;
    }

    public void setPowertrainRepairRoomArea(String powertrainRepairRoomArea) {
        this.powertrainRepairRoomArea = powertrainRepairRoomArea;
    }

    public String getMobileFireWaterPoolArea() {
        return mobileFireWaterPoolArea;
    }

    public void setMobileFireWaterPoolArea(String mobileFireWaterPoolArea) {
        this.mobileFireWaterPoolArea = mobileFireWaterPoolArea;
    }

    public String getServiceDirectorOfficeArea() {
        return serviceDirectorOfficeArea;
    }

    public void setServiceDirectorOfficeArea(String serviceDirectorOfficeArea) {
        this.serviceDirectorOfficeArea = serviceDirectorOfficeArea;
    }

    public String getBusinessDevelopmentCenterArea() {
        return businessDevelopmentCenterArea;
    }

    public void setBusinessDevelopmentCenterArea(String businessDevelopmentCenterArea) {
        this.businessDevelopmentCenterArea = businessDevelopmentCenterArea;
    }

    public String getMeetingTrainingRoomArea() {
        return meetingTrainingRoomArea;
    }

    public void setMeetingTrainingRoomArea(String meetingTrainingRoomArea) {
        this.meetingTrainingRoomArea = meetingTrainingRoomArea;
    }

    public String getWorkshopOfficeArea() {
        return workshopOfficeArea;
    }

    public void setWorkshopOfficeArea(String workshopOfficeArea) {
        this.workshopOfficeArea = workshopOfficeArea;
    }

    public String getStaffLoungeArea() {
        return staffLoungeArea;
    }

    public void setStaffLoungeArea(String staffLoungeArea) {
        this.staffLoungeArea = staffLoungeArea;
    }

    public String getToolRoomArea() {
        return toolRoomArea;
    }

    public void setToolRoomArea(String toolRoomArea) {
        this.toolRoomArea = toolRoomArea;
    }

    public String getWasteOilArea() {
        return wasteOilArea;
    }

    public void setWasteOilArea(String wasteOilArea) {
        this.wasteOilArea = wasteOilArea;
    }
    public String getElectricityDistributionRoomArea() {
        return electricityDistributionRoomArea;
    }

    public void setElectricityDistributionRoomArea(String electricityDistributionRoomArea) {
        this.electricityDistributionRoomArea = electricityDistributionRoomArea;
    }

    public String getAirCompressorRoomArea() {
        return airCompressorRoomArea;
    }

    public void setAirCompressorRoomArea(String airCompressorRoomArea) {
        this.airCompressorRoomArea = airCompressorRoomArea;
    }

    public String getWorkshopToiletArea() {
        return workshopToiletArea;
    }

    public void setWorkshopToiletArea(String workshopToiletArea) {
        this.workshopToiletArea = workshopToiletArea;
    }

    public String getPaintStorageBlendingRoomArea() {
        return paintStorageBlendingRoomArea;
    }

    public void setPaintStorageBlendingRoomArea(String paintStorageBlendingRoomArea) {
        this.paintStorageBlendingRoomArea = paintStorageBlendingRoomArea;
    }

    public String getCarWashingArea() {
        return carWashingArea;
    }

    public void setCarWashingArea(String carWashingArea) {
        this.carWashingArea = carWashingArea;
    }

    public String getClaimPartsStorageRoomArea() {
        return claimPartsStorageRoomArea;
    }

    public void setClaimPartsStorageRoomArea(String claimPartsStorageRoomArea) {
        this.claimPartsStorageRoomArea = claimPartsStorageRoomArea;
    }

    public String getFinanceOfficeArea() {
        return financeOfficeArea;
    }

    public void setFinanceOfficeArea(String financeOfficeArea) {
        this.financeOfficeArea = financeOfficeArea;
    }

    public String getInsuranceClaimArea() {
        return insuranceClaimArea;
    }

    public void setInsuranceClaimArea(String insuranceClaimArea) {
        this.insuranceClaimArea = insuranceClaimArea;
    }

    public String getDisassembledPartsRoomArea() {
        return disassembledPartsRoomArea;
    }

    public void setDisassembledPartsRoomArea(String disassembledPartsRoomArea) {
        this.disassembledPartsRoomArea = disassembledPartsRoomArea;
    }

    public String getUsedPartsStorageArea() {
        return usedPartsStorageArea;
    }

    public void setUsedPartsStorageArea(String usedPartsStorageArea) {
        this.usedPartsStorageArea = usedPartsStorageArea;
    }

}
