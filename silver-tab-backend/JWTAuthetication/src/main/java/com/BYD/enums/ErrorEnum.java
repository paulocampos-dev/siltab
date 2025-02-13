package com.BYD.enums;

public enum ErrorEnum {
    SUCCESS(200, "Success"),
    UNAUTHORIZED(401, "Illegal request for this dealer. Please, check credentials"),
    /* Errors in range 40000~40099, Bad Request */
    EMPTY_REQUEST_BODY(40001, "Empty Request Body"),
    INVALID_ENCRYPTION(40002, "The Repair Order was not accepted because JSON isn't encrypted correctly or doesn't match 'encrypt' flag value in header"),
    JSON_PROCESSING_ERROR(40003, "Invalid JSON structure"),
    FAILED_FIELD_VALIDATION(40004, "Failed field validation"),
    INVALID_MANDATORY_ENCRYPTION(40005, "The Repair Order was not accepted because JSON isn't encrypted correctly. Encryption mandatory"),
    IMAGE_UPLOAD_ERROR(40010, "Failed to upload image"),
    IMAGE_DELETE_ERROR(40011, "Failed to delete image"),
    IMAGE_TYPE_INVALID(40012, "Invalid image type"),
    IMAGE_NOT_FOUND(40013, "Image not found"),
    IMAGE_ACCESS_DENIED(40014, "Unauthorized access to dealer images"),

    /* Errors in range 50000~50099, Internal Server Error */
    GENERAL_INTERNAL_ERROR(50001, "An unexpected error has occurred. Please contact your API provider."),
    GENERAL_DMS_ERROR(50005, "An unexpected error has occurred. Please contact your API provider."),
    WRITE_FILE_ERROR(-4, "Failed to write JSON to file"),

    /* Errors in range 400 ~ 500  */
    UNAUTHORIZED_ACCESS(401, "Unauthorized Access");


    private final int code;
    private String message;

    ErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage (String msg) {
        this.message = msg;
    }
}