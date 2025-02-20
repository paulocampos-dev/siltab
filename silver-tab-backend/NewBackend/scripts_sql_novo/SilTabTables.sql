
CREATE TABLE Cars (
  Car_ID        NUMBER GENERATED ALWAYS AS IDENTITY 
                   START WITH 1 INCREMENT BY 1,
  Chassi_Number VARCHAR2(50) NOT NULL,
  Dealer_Code   VARCHAR2(50),
  Model         VARCHAR2(100),
  PDI_IDs      CLOB,
  CONSTRAINT pk_cars PRIMARY KEY (Car_ID)
);

CREATE TABLE PDI_LINK_TO_DEALER_CARS (
    PDI_ID               NUMBER GENERATED ALWAYS AS IDENTITY
                            START WITH 1 INCREMENT BY 1,
    Car_ID               NUMBER NOT NULL,
    User_ID              NUMBER,
    Dealer_code          VARCHAR2(50),
    Created_at           TIMESTAMP,        
    Chassi_Number        VARCHAR2(50),
    Soc_Percentage       BINARY_FLOAT, -- ou FLOAT ou NUMBER(5,2), conforme sua necessidade
    BatteryV12           NUMBER(10),   -- substituindo o int
    -- Em Oracle, "boolean" → Number(1) c/ constraint (0 ou 1)
    Five_minutes_hybrid  NUMBER(1) 
        CONSTRAINT chk_bool_five_minutes 
        CHECK (Five_minutes_hybrid IN (0, 1)),
    
    Tire_Pressure_DD     BINARY_FLOAT,
    Tire_Pressure_TD     BINARY_FLOAT,
    Tire_Pressure_DE     BINARY_FLOAT,
    Tire_Pressure_TE     BINARY_FLOAT,
    Extra_text           CLOB,         -- ou VARCHAR2(4000) se desejar
    
    CONSTRAINT pk_PDI_LINK_TO_DEALER_CARS PRIMARY KEY (PDI_ID),
    CONSTRAINT fk_car_id 
        FOREIGN KEY (Car_ID) REFERENCES Cars (Car_ID)
);

ALTER TABLE Cars 
  ADD CONSTRAINT uq_chassi_number UNIQUE (Chassi_Number);

CREATE TABLE Image_Info (
    Img_ID NUMBER GENERATED ALWAYS AS IDENTITY 
        START WITH 1 INCREMENT BY 1,
    Img_path VARCHAR2(600) NOT NULL,
    file_name VARCHAR2(255) NOT NULL,
    Uploaded_by_User_ID Number,
    Upload_date TIMESTAMP,
    CONSTRAINT pk_img_id PRIMARY KEY (Img_ID)
);

CREATE TABLE PDI_Link_to_Image (
    PDI_ID     NUMBER NOT NULL,
    Image_ID   NUMBER NOT NULL,
    Image_Type NUMBER(10),
    
    -- Definindo a chave primária composta
    CONSTRAINT pk_PDI_Link_to_Image PRIMARY KEY (PDI_ID, Image_ID),

    -- Chave estrangeira para PDI_ID referenciando PDI_History
    CONSTRAINT fk_PDI_Link_to_Image
        FOREIGN KEY (PDI_ID) REFERENCES PDI_LINK_TO_DEALER_CARS(PDI_ID)
        ON DELETE CASCADE,

    -- Chave estrangeira para Image_ID referenciando Image_Info_PDI
    CONSTRAINT fk_PDI_Link_to_Image_image_info
        FOREIGN KEY (Image_ID) REFERENCES Image_Info(Img_ID)
        ON DELETE CASCADE
);


CREATE TABLE PDI_image_Type (
    PDI_Type_ID    NUMBER(10) NOT NULL,
    PDI_Type_name  VARCHAR2(100) NOT NULL,

    CONSTRAINT pk_pdi_image_type PRIMARY KEY (PDI_Type_ID),

    -- Exemplo de enum com valores fixos (opcional)
    CONSTRAINT CHK_TYPE_ID_ENUM CHECK (PDI_Type_ID BETWEEN 1 AND 18)     
);