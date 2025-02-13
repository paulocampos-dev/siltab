package com.BYD.dto.dealer;

import java.util.List;

public class DealerImageResponseDTO {
    private String dealerCode;
    private List<DealerImageDTO> images;
    private String message;
    private boolean success;

    public String getDealerCode() { return dealerCode; }
    public void setDealerCode(String dealerCode) { this.dealerCode = dealerCode; }

    public List<DealerImageDTO> getImages() { return images; }
    public void setImages(List<DealerImageDTO> images) { this.images = images; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}