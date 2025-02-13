-- Clear existing data (optional, use with caution)
DELETE FROM pdi;
DELETE FROM cars;
DELETE FROM users_byd;

-- Insert 5 users into users_byd
INSERT INTO users_byd (id) VALUES (1);
INSERT INTO users_byd (id) VALUES (2);
INSERT INTO users_byd (id) VALUES (3);
INSERT INTO users_byd (id) VALUES (4);
INSERT INTO users_byd (id) VALUES (5);

-- Insert car models into cars table
DECLARE
    TYPE car_models IS TABLE OF VARCHAR2(100);
    v_car_models car_models := car_models(
        'BYD SHARK', 'BYD KING DM-i', 'BYD SONG PLUS DM-i', 'SONG PLUS PREMIUM DM-i', 'BYD SONG PRO DM-i',
        'BYD DOLPHIN MINI', 'BYD DOLPHIN', 'BYD DOLPHIN PLUS', 'BYD HAN', 'BYD SEAL', 'BYD TAN', 'BYD YUAN PLUS', 'BYD YUAN PRO'
    );
    v_car_id RAW(16);
    v_vin VARCHAR2(17);
BEGIN
    FOR i IN 1..v_car_models.COUNT LOOP
        -- Generate a random UUID (RAW(16))
        v_car_id := SYS_GUID();
        -- Generate a random VIN (17 characters)
        v_vin := DBMS_RANDOM.STRING('X', 17);
        -- Insert car into cars table
        INSERT INTO cars (id, model, year, vin)
        VALUES (v_car_id, v_car_models(i), 2020 + MOD(i, 5), v_vin);
    END LOOP;
END;
/

-- Insert random inspections into pdi table
DECLARE
    v_inspection_id NUMBER := 1;
    v_car_id RAW(16);
    v_inspector_id NUMBER;
    v_inspection_date TIMESTAMP;
    v_chassi_number NUMBER;
    v_soc_percentage NUMBER;
    v_battery_12v NUMBER;
    v_tire_pressure_DD NUMBER;
    v_tire_pressure_DE NUMBER;
    v_tire_pressure_TD NUMBER;
    v_tire_pressure_TE NUMBER;
    v_five_minutes_hybrid NUMBER(1);
    v_extra_text CLOB := 'Sample extra text for inspection.';
BEGIN
    -- Loop through each user
    FOR user_id IN 1..5 LOOP
        -- Generate between 10 and 50 inspections per user
        FOR inspection_count IN 1..(10 + DBMS_RANDOM.VALUE(0, 40)) LOOP
            -- Select a random car
            SELECT id INTO v_car_id
            FROM (SELECT id FROM cars ORDER BY DBMS_RANDOM.VALUE)
            WHERE ROWNUM = 1;

            -- Generate random inspection data
            v_inspector_id := user_id;
            v_inspection_date := SYSTIMESTAMP - DBMS_RANDOM.VALUE(0, 730); -- Random date within last 2 years
            v_chassi_number := ROUND(DBMS_RANDOM.VALUE(1000, 9999));
            v_soc_percentage := ROUND(DBMS_RANDOM.VALUE(0, 100));
            v_battery_12v := ROUND(DBMS_RANDOM.VALUE(0, 100));
            v_tire_pressure_DD := ROUND(DBMS_RANDOM.VALUE(0, 100));
            v_tire_pressure_DE := ROUND(DBMS_RANDOM.VALUE(0, 100));
            v_tire_pressure_TD := ROUND(DBMS_RANDOM.VALUE(0, 100));
            v_tire_pressure_TE := ROUND(DBMS_RANDOM.VALUE(0, 100));
            v_five_minutes_hybrid := ROUND(DBMS_RANDOM.VALUE(0, 1));

            -- Insert inspection into pdi table
            INSERT INTO pdi (
                id, car_id, inspector_id, inspection_date, chassi_number, chassi_image_path,
                soc_percentage, soc_percentage_image_path, battery_12v, battery_12v_image_path,
                tire_pressure_DD, tire_pressure_DE, tire_pressure_TD, tire_pressure_TE,
                tire_pressure_image_path, five_minutes_hybrid, extra_text, extra_image_1,
                extra_image_2, extra_image_3
            ) VALUES (
                v_inspection_id, v_car_id, v_inspector_id, v_inspection_date, v_chassi_number, 'path/to/chassi_image',
                v_soc_percentage, 'path/to/soc_image', v_battery_12v, 'path/to/battery_image',
                v_tire_pressure_DD, v_tire_pressure_DE, v_tire_pressure_TD, v_tire_pressure_TE,
                'path/to/tire_pressure_image', v_five_minutes_hybrid, v_extra_text,
                'path/to/extra_image_1', 'path/to/extra_image_2', 'path/to/extra_image_3'
            );

            -- Increment inspection ID
            v_inspection_id := v_inspection_id + 1;
        END LOOP;
    END LOOP;
END;
/
