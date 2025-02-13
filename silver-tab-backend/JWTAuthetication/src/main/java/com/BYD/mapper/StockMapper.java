package com.BYD.mapper;

import com.BYD.dto.stock.StockInventoryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface StockMapper {

    Integer getUserPosition(String userId);

    List<StockInventoryDTO> getStockInventoryByPosition(
            @Param("userId") String userId,
            @Param("positionId") Integer positionId
    );
}