package com.BYD.dto.dealer;

import java.time.LocalDateTime;

public class DealerImageDTO {
    private Long imageId;
    private String dealerCode;
    private String imageTypeName;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadDate;
    private Long uploadedByUserId;
    private byte[] imageData;
    private String mimeType;

    // Getters and Setters
    public Long getImageId() { return imageId; }
    public void setImageId(Long imageId) { this.imageId = imageId; }

    public String getDealerCode() { return dealerCode; }
    public void setDealerCode(String dealerCode) { this.dealerCode = dealerCode; }

    public String getImageTypeName() { return imageTypeName; }
    public void setImageTypeName(String imageTypeName) { this.imageTypeName = imageTypeName; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }

    public Long getUploadedByUserId() { return uploadedByUserId; }
    public void setUploadedByUserId(Long uploadedByUserId) { this.uploadedByUserId = uploadedByUserId; }

    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
}