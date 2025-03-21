CREATE TABLE CAR_MODEL (
  CAR_MODEL_ID   NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  CAR_MODEL_NAME VARCHAR2(100)
);

CREATE TABLE CAR_INFO (
  CAR_ID            NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  VIN     VARCHAR2(50 CHAR) UNIQUE NOT NULL,
  DEALER_CODE       VARCHAR2(50 CHAR),
  CAR_MODEL_ID      NUMBER,
  IS_SOLD           NUMBER(1),
  SOLD_DATE         TIMESTAMP,

  CONSTRAINT FK_CAR_MODEL_ID FOREIGN KEY (CAR_MODEL_ID)
    REFERENCES CAR_MODEL (CAR_MODEL_ID)

);

CREATE TABLE PDI_INFO (
  PDI_ID                      NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  CREATE_BY_USER_ID           NUMBER,
  DEALER_CODE                 VARCHAR2(50 CHAR),
  CAR_ID                      NUMBER,
  LAST_MODIFIED_BY_USER_ID    NUMBER,
  CREATED_DATE                TIMESTAMP,
  CREATE_BY                   VARCHAR2(30 CHAR),
  SOC_PERCENTAGE              BINARY_FLOAT, -- ou FLOAT ou NUMBER(5,2), conforme sua necessidade
  BATTERY12V_VOLTAGE          NUMBER,
  FIVE_MINUTES_HYBRID_CHECK   NUMBER(1),
  TIRE_PRESSURE_DD            BINARY_FLOAT,
  TIRE_PRESSURE_TD            BINARY_FLOAT,
  TIRE_PRESSURE_DE            BINARY_FLOAT,
  TIRE_PRESSURE_TE            BINARY_FLOAT,
  USER_COMMENTS               VARCHAR2(4000 CHAR),
  PENDING                     NUMBER(1),
  RESOLVED_DATE               TIMESTAMP,

  CONSTRAINT FK_CREATE_BY_USER_ID FOREIGN KEY (CREATE_BY_USER_ID)
    REFERENCES USER_ACCESS (USER_ID),

  CONSTRAINT FK_CAR_ID FOREIGN KEY (CAR_ID)
    REFERENCES CAR_INFO (CAR_ID)
);

CREATE TABLE PDI_IMAGE_TYPE (
  PDI_IMAGE_TYPE_ID   NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  PDI_IMAGE_TYPE_NAME VARCHAR2(100)
);

CREATE TABLE PDI_IMAGE (
  PDI_ID            NUMBER NOT NULL,
  IMAGE_ID          NUMBER NOT NULL,
  PDI_IMAGE_TYPE_ID NUMBER,
  CONSTRAINT PK_PDI_LINK_TO_IMAGE PRIMARY KEY (PDI_ID, IMAGE_ID),
  CONSTRAINT FK_PDI__IMAGE FOREIGN KEY (PDI_ID)
    REFERENCES PDI_INFO(PDI_ID),
  CONSTRAINT FK_PDI_LINK_TO_IM FOREIGN KEY (IMAGE_ID)
    REFERENCES IMAGE_INFO (IMAGE_ID)
);