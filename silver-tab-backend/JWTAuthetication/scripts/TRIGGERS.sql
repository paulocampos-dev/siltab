
-- Triggers before INSERT --

-- USER_ACCESS -- 
create or replace TRIGGER trg_before_insert_user_access
BEFORE INSERT ON USER_ACCESS
FOR EACH ROW
BEGIN
    -- Fill CREATE_BY with current user
    :NEW.CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Fill CREATE_DATE with current date/time
    :NEW.CREATE_DATE := SYSTIMESTAMP;
END;
/

-- REPAIR_ORDER_MAIN -- 
create or replace TRIGGER trg_before_insert_repair_order_main
BEFORE INSERT ON REPAIR_ORDER_MAIN
FOR EACH ROW
DECLARE
    v_max_version NUMBER;
BEGIN
    SELECT NVL(MAX(DEALER_ORDER_VERSION), 0)
    INTO v_max_version
    FROM REPAIR_ORDER_MAIN
    WHERE EXTERNAL_DEALER_ORDER_NO = :NEW.EXTERNAL_DEALER_ORDER_NO
    AND DEALER_CODE = :NEW.DEALER_CODE; 
    
        -- If max_version = 0, then new repair order
        IF v_max_version = 0 THEN
            :NEW.DEALER_ORDER_VERSION := 1;
        ELSE
            -- Increase version by 1
            :NEW.DEALER_ORDER_VERSION := v_max_version + 1;
        END IF;
        
        -- Fill CREATE_BY with current user
        :NEW.MAIN_CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
        -- Fill CREATE_DATE with current date/time
        :NEW.MAIN_CREATION_DATE_BY_BRSYS := SYSTIMESTAMP;
END;
/

-- REPAIR_ORDER_lABOUR -- 
create or replace TRIGGER trg_before_insert_repair_order_labour
BEFORE INSERT ON REPAIR_ORDER_LABOUR
FOR EACH ROW
BEGIN
    -- Fill CREATE_BY with current user
    :NEW.LABOUR_CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Fill CREATE_DATE with current date/time
    :NEW.LABOUR_CREATE_DATE := SYSTIMESTAMP;
END;
/

-- REPAIR_ORDER_PARTS -- 
create or replace TRIGGER trg_before_insert_repair_order_parts
BEFORE INSERT ON REPAIR_ORDER_PARTS
FOR EACH ROW
BEGIN
    -- Fill CREATE_BY with current user
    :NEW.PART_CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Fill CREATE_DATE with current date/time
    :NEW.PART_CREATE_DATE := SYSTIMESTAMP;
END;
/

-- REPAIR_ORDER_SALE_PARTS -- 
create or replace TRIGGER trg_before_insert_repair_order_sale_parts
BEFORE INSERT ON REPAIR_ORDER_SALE_PARTS
FOR EACH ROW
BEGIN
    -- Fill CREATE_BY with current user
    :NEW.SP_CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Fill CREATE_DATE with current date/time
    :NEW.SP_CREATE_DATE := SYSTIMESTAMP;
END;
/

-- STOCK_INVENTORY -- 
create or replace TRIGGER trg_before_insert_stock_inventory
BEFORE INSERT ON STOCK_INVENTORY
FOR EACH ROW
BEGIN
    -- Fill CREATE_BY with current user
    :NEW.CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Fill CREATE_DATE with current date/time
    :NEW.CREATE_DATE := SYSTIMESTAMP;
END;
/

-- STOCK_TRANSACTION -- 
create or replace TRIGGER trg_before_insert_stock_transaction
BEFORE INSERT ON STOCK_TRANSACTION
FOR EACH ROW
BEGIN
    -- Fill CREATE_BY with current user
    :NEW.CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Fill CREATE_DATE with current date/time
    :NEW.CREATE_DATE := SYSTIMESTAMP;
END;
/

-- STOCK_ADJUSTMENT -- 
create or replace TRIGGER trg_before_insert_stock_adjustment
BEFORE INSERT ON STOCK_ADJUSTMENT
FOR EACH ROW
DECLARE
    v_book_stock  NUMBER;
    v_actual_stock NUMBER;
BEGIN
    -- Fill CREATE_BY with current user
    :NEW.CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Fill CREATE_DATE with current date/time
    :NEW.CREATE_DATE := SYSTIMESTAMP;
    
    -- Retrieve the current values of BOOK_STOCK and ACTUAL_STOCK from STOCK_INVENTORY
    SELECT BOOK_STOCK, ACTUAL_STOCK
    INTO v_book_stock, v_actual_stock
    FROM STOCK_INVENTORY
    WHERE DEALER_CODE = :NEW.DEALER_CODE
      AND SPARE_PARTS_CODE = :NEW.SPARE_PARTS_CODE
    FOR UPDATE;
    
    -- Update STOCK_INVENTORY based on the new adjustment
    UPDATE STOCK_INVENTORY
    SET BOOK_STOCK = :NEW.NEW_BOOK_STOCK,
        ACTUAL_STOCK = :NEW.NEW_ACTUAL_STOCK,
        UPDATE_BY = :NEW.ADJUSTED_BY,
        UPDATE_DATE = SYSTIMESTAMP
    WHERE DEALER_CODE = :NEW.DEALER_CODE
      AND SPARE_PARTS_CODE = :NEW.SPARE_PARTS_CODE;
    
   -- Set the PREVIOUS_BOOK_STOCK and PREVIOUS_ACTUAL_STOCK in the new row of STOCK_ADJUSTMENT
   :NEW.PREVIOUS_BOOK_STOCK := v_book_stock;
   :NEW.PREVIOUS_ACTUAL_STOCK := v_actual_stock;
END;
/

-- TRANSMISSION_DMS --
create or replace TRIGGER trg_before_insert_transmission_dms
BEFORE INSERT ON TRANSMISSION_DMS
FOR EACH ROW
BEGIN
    -- Fill CREATE_BY with current user
    :NEW.CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Fill CREATE_DATE with current date/time
    :NEW.CREATE_DATE := SYSTIMESTAMP;
END;
/

-- DEALER_CREDENTIAL --
create or replace TRIGGER trg_before_insert_dealer_credential
BEFORE INSERT ON DEALER_CREDENTIAL
FOR EACH ROW
BEGIN
    -- Fill CREATE_BY with current dealer
    :NEW.CREDENTIAL_CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Fill CREATE_DATE with current date/time
    :NEW.CREDENTIAL_CREATE_DATE := SYSTIMESTAMP;

    -- Fill UPDATE_BY with current dealer
    :NEW.CREDENTIAL_UPDATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Fill UPDATE_DATE with current date/time
    :NEW.CREDENTIAL_UPDATE_DATE := SYSTIMESTAMP;
END;
/

-- Triggers for USER_ACCESS_DEALER
CREATE OR REPLACE TRIGGER trg_before_insert_user_access_dealer
BEFORE INSERT ON USER_ACCESS_DEALER
FOR EACH ROW
BEGIN
    :NEW.CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    :NEW.CREATE_DATE := SYSTIMESTAMP;
END;
/

-- Triggers for USER_ACCESS_GROUP
CREATE OR REPLACE TRIGGER trg_before_insert_user_access_group
BEFORE INSERT ON USER_ACCESS_GROUP
FOR EACH ROW
BEGIN
    :NEW.CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    :NEW.CREATE_DATE := SYSTIMESTAMP;
END;
/

-- Triggers for USER_ACCESS_REGION
CREATE OR REPLACE TRIGGER trg_before_insert_user_access_region
BEFORE INSERT ON USER_ACCESS_REGION
FOR EACH ROW
BEGIN
    :NEW.CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    :NEW.CREATE_DATE := SYSTIMESTAMP;
END;
/

-- Triggers for USER_ACCESS_STATE
CREATE OR REPLACE TRIGGER trg_before_insert_user_access_state
BEFORE INSERT ON USER_ACCESS_STATE
FOR EACH ROW
BEGIN
    :NEW.CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    :NEW.CREATE_DATE := SYSTIMESTAMP;
END;
/

-- Triggers for USER_ACCESS_CITY
CREATE OR REPLACE TRIGGER trg_before_insert_user_access_city
BEFORE INSERT ON USER_ACCESS_CITY
FOR EACH ROW
BEGIN
    :NEW.CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    :NEW.CREATE_DATE := SYSTIMESTAMP;
END;
/

-- Triggers for USER_SESSION
CREATE OR REPLACE TRIGGER trg_before_insert_user_session
BEFORE INSERT ON USER_SESSION
FOR EACH ROW
BEGIN
    :NEW.CREATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    :NEW.CREATE_DATE := SYSTIMESTAMP;
END;
/


--------------------------------------------------------------------------------
-- Triggers before UPDATE --

-- USER_ACCESS --
create or replace TRIGGER trg_before_update_user_access
BEFORE UPDATE ON USER_ACCESS
FOR EACH ROW
BEGIN
   :NEW.LAST_MODIFIED_DATE := SYSTIMESTAMP;
END;
/

-- DEALER_CREDENTIAL --
create or replace TRIGGER trg_before_update_dealer_credential
BEFORE UPDATE ON DEALER_CREDENTIAL
FOR EACH ROW
BEGIN
    -- Fill UPDATE_BY with current dealer
    :NEW.CREDENTIAL_UPDATE_BY := SYS_CONTEXT('USERENV', 'SESSION_USER');
    -- Fill UPDATE_DATE with current date/time
    :NEW.CREDENTIAL_UPDATE_DATE := SYSTIMESTAMP;
END;
/

-- USER_ACCESS_DEALER --

CREATE OR REPLACE TRIGGER trg_before_update_user_access_dealer
BEFORE UPDATE ON USER_ACCESS_DEALER
FOR EACH ROW
BEGIN
    :NEW.LAST_MODIFIED_DATE := SYSTIMESTAMP;
END;
/

-- USER_ACCESS_GROUP --

CREATE OR REPLACE TRIGGER trg_before_update_user_access_group
BEFORE UPDATE ON USER_ACCESS_GROUP
FOR EACH ROW
BEGIN
    :NEW.LAST_MODIFIED_DATE := SYSTIMESTAMP;
END;
/

-- USER_ACCESS_REGION --

CREATE OR REPLACE TRIGGER trg_before_update_user_access_region
BEFORE UPDATE ON USER_ACCESS_REGION
FOR EACH ROW
BEGIN
    :NEW.LAST_MODIFIED_DATE := SYSTIMESTAMP;
END;
/

-- USER_ACCESS_STATE --

CREATE OR REPLACE TRIGGER trg_before_update_user_access_state
BEFORE UPDATE ON USER_ACCESS_STATE
FOR EACH ROW
BEGIN
    :NEW.LAST_MODIFIED_DATE := SYSTIMESTAMP;
END;
/

-- USER_ACCESS_CITY --

CREATE OR REPLACE TRIGGER trg_before_update_user_access_city
BEFORE UPDATE ON USER_ACCESS_CITY
FOR EACH ROW
BEGIN
    :NEW.LAST_MODIFIED_DATE := SYSTIMESTAMP;
END;
/

-- USER_SESSION -- 
CREATE OR REPLACE TRIGGER trg_before_update_user_session
BEFORE UPDATE ON USER_SESSION
FOR EACH ROW
BEGIN
    :NEW.LAST_MODIFIED_DATE := SYSTIMESTAMP;
END;
/

--------------------------------------------------------------------------------
-- Triggers AFTER INSERT --

-- AFTER STOCK_TRANSACTION TRIGGER --
CREATE OR REPLACE TRIGGER trg_update_stock_inventory
AFTER INSERT ON STOCK_TRANSACTION
FOR EACH ROW
BEGIN
    -- Verifica a dire��o da transa��o para atualizar o estoque
    IF :NEW.TRANSACTION_DIRECTION = 'Inbound' THEN
        -- Atualiza o estoque com a quantidade de entrada
        UPDATE STOCK_INVENTORY
        SET BOOK_STOCK = BOOK_STOCK + :NEW.INCOMING_QUANTITY,
            ACTUAL_STOCK = ACTUAL_STOCK + :NEW.INCOMING_QUANTITY
        WHERE DEALER_CODE = :NEW.DEALER_CODE
          AND SPARE_PARTS_CODE = :NEW.MATERIAL_CODE;

    ELSIF :NEW.TRANSACTION_DIRECTION = 'Outbound' THEN
        -- Atualiza o estoque com a quantidade de sa�da
        UPDATE STOCK_INVENTORY
        SET BOOK_STOCK = BOOK_STOCK - :NEW.OUTPUT_QUANTITY,
            ACTUAL_STOCK = ACTUAL_STOCK - :NEW.OUTPUT_QUANTITY
        WHERE DEALER_CODE = :NEW.DEALER_CODE
          AND SPARE_PARTS_CODE = :NEW.MATERIAL_CODE;
    END IF;
END;
/