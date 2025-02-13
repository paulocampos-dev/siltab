-- First, create the cars table which contains basic vehicle information
CREATE TABLE cars (
    id RAW(16) PRIMARY KEY,  -- or VARCHAR2(36)
    model VARCHAR2(100) NOT NULL,
    year NUMBER(4) NOT NULL,
    vin VARCHAR2(17) NOT NULL UNIQUE
);

-- Create the users_byd table for BYD users
CREATE TABLE users_byd (
    id NUMBER PRIMARY KEY
);

-- Create the main PDI (Pre-Delivery Inspection) table
-- This table contains all inspection details and measurements
CREATE TABLE pdi (
    id NUMBER PRIMARY KEY,
    car_id RAW(16) REFERENCES cars (id),  -- or VARCHAR2(36)
    inspector_id NUMBER REFERENCES users_byd (id),
    inspection_date TIMESTAMP NOT NULL,
    chassi_number NUMBER,
    chassi_image_path VARCHAR2(255),
    soc_percentage NUMBER(3),
    soc_percentage_image_path VARCHAR2(255),
    battery_12v NUMBER(3),
    battery_12v_image_path VARCHAR2(255),
    tire_pressure_DD NUMBER(3),
    tire_pressure_DE NUMBER(3),
    tire_pressure_TD NUMBER(3),
    tire_pressure_TE NUMBER(3),
    tire_pressure_image_path VARCHAR2(255),
    five_minutes_hybrid NUMBER(1),
    extra_text CLOB,
    extra_image_1 VARCHAR2(255),
    extra_image_2 VARCHAR2(255),
    extra_image_3 VARCHAR2(255),
    
    -- Add constraints for valid ranges
    CONSTRAINT soc_percentage_check CHECK (soc_percentage BETWEEN 0 AND 100),
    CONSTRAINT tire_pressure_check CHECK (
        tire_pressure_DD BETWEEN 0 AND 100 AND
        tire_pressure_DE BETWEEN 0 AND 100 AND
        tire_pressure_TD BETWEEN 0 AND 100 AND
        tire_pressure_TE BETWEEN 0 AND 100
    )
);

-- Create indexes for better query performance
CREATE INDEX idx_pdi_car_id ON pdi(car_id);
CREATE INDEX idx_pdi_inspector_id ON pdi(inspector_id);
CREATE INDEX idx_pdi_inspection_date ON pdi(inspection_date);
