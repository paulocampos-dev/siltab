package com.BYD.mapper;

import java.util.List;
import com.BYD.dto.RegionDTO;
import org.apache.ibatis.annotations.Param;

public interface UserAccessRegionMapper {
    List<Integer> findRegionIdsByUserId(@Param("userId") Long userId);
    List<Integer> findAllRegionIds();
    List<RegionDTO> findRegionsByIds(@Param("regionIds") List<Integer> regionIds);
}