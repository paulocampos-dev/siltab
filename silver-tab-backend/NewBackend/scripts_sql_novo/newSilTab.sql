CREATE TABLE Image_Info_PDI (
    Img_ID NUMBER GENERATED ALWAYS AS IDENTITY 
        START WITH 1 INCREMENT BY 1,
    Img_path VARCHAR2(255) NOT NULL,
    Uploaded_by_User_ID VARCHAR2(255),
    Upload_date DATE,
    PDI_ID NUMBER,
    CONSTRAINT pk_img_id PRIMARY KEY (Img_ID)
);


CREATE TABLE Cars (
  Car_ID        NUMBER GENERATED ALWAYS AS IDENTITY 
                   START WITH 1 INCREMENT BY 1,
  Chassi_Number VARCHAR2(50) NOT NULL,
  Dealer_Code   VARCHAR2(50),
  Model         VARCHAR2(100),
  PDI_IDs      CLOB,
  CONSTRAINT pk_cars PRIMARY KEY (Car_ID)
);

CREATE TABLE PDI_History (
    PDI_ID               NUMBER GENERATED ALWAYS AS IDENTITY
                            START WITH 1 INCREMENT BY 1,
    Car_ID               NUMBER NOT NULL,
    User_ID              NUMBER,
    Uploaded_by_User_ID  VARCHAR2(100),
    Dealer_code          VARCHAR2(50),
    Created_at           DATE,         -- ou TIMESTAMP caso precise de hora/min/seg
    Chassi_Number        VARCHAR2(50),
    Chassi_Image_ID      NUMBER,       -- Bigint pode ser mapeado como NUMBER sem escala
    Soc_Percentage       BINARY_FLOAT, -- ou FLOAT ou NUMBER(5,2), conforme sua necessidade
    Soc_Image_ID         NUMBER,
    BatteryV12           NUMBER(10),   -- substituindo o int
    BatteryV12_Image_ID  NUMBER,
    
    -- Em Oracle, "boolean" → Number(1) c/ constraint (0 ou 1)
    Five_minutes_hybrid  NUMBER(1) 
        CONSTRAINT chk_bool_five_minutes 
        CHECK (Five_minutes_hybrid IN (0, 1)),
    
    Tire_Pressure_DD     BINARY_FLOAT,
    Tire_Pressure_TD     BINARY_FLOAT,
    Tire_Pressure_DE     BINARY_FLOAT,
    Tire_Pressure_TE     BINARY_FLOAT,
    Tire_Pressure_Image_ID NUMBER,
    
    Extra_Image_ID       NUMBER,
    Extra2_Image_ID      NUMBER,
    Extra_text           CLOB,         -- ou VARCHAR2(4000) se desejar
    
    CONSTRAINT pk_pdi_history PRIMARY KEY (PDI_ID),
    CONSTRAINT fk_car_id 
        FOREIGN KEY (Car_ID) REFERENCES Cars (Car_ID)
);

ALTER TABLE Cars 
  ADD CONSTRAINT uq_chassi_number UNIQUE (Chassi_Number);

CREATE TABLE PDI_Image (
    PDI_ID     NUMBER NOT NULL,
    Image_ID   NUMBER NOT NULL,
    Image_Type NUMBER(10),
    
    -- Definindo a chave primária composta
    CONSTRAINT pk_pdi_image PRIMARY KEY (PDI_ID, Image_ID),

    -- Chave estrangeira para PDI_ID referenciando PDI_History
    CONSTRAINT fk_pdi_image_pdi
        FOREIGN KEY (PDI_ID) REFERENCES PDI_History(PDI_ID)
        ON DELETE CASCADE,

    -- Chave estrangeira para Image_ID referenciando Image_Info_PDI
    CONSTRAINT fk_pdi_image_image
        FOREIGN KEY (Image_ID) REFERENCES Image_Info_PDI(Img_ID)
        ON DELETE CASCADE
);


CREATE TABLE PDI_image_Type (
    Type_ID    NUMBER(10) NOT NULL,
    Type_name  VARCHAR2(100) NOT NULL,

    CONSTRAINT pk_pdi_image_type PRIMARY KEY (Type_ID),

    -- Exemplo de enum com valores fixos (opcional)
    CONSTRAINT chk_type_id_enum CHECK (Type_ID IN (1, 2, 3, 4, 5))
);


