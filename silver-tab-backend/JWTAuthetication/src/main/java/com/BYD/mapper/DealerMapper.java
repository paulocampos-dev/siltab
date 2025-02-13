package com.BYD.mapper;

import com.BYD.dto.dealer.DealerInfoDTO;
import com.BYD.dto.dealer.ConciseDealerInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface DealerMapper {

    /**
     * Check if user has access to specific dealer based on their position
     */
    boolean checkDealerAccess(
            @Param("userId") String userId,
            @Param("dealerCode") String dealerCode,
            @Param("positionId") Integer positionId
    );

    /**
     * Get dealer information by dealer code
     */
    DealerInfoDTO getDealerInfo(@Param("dealerCode") String dealerCode);

    List<String> getUserAccessibleDealers(@Param("userId") String userId, @Param("positionId") Integer positionId);

    List<Map<String, Object>> getDealerNamesByCode(@Param("dealerCodes") List<String> dealerCodes);

    List<ConciseDealerInfoDTO> getUserAccessibleDealersSummary(@Param("userId") String userId, @Param("positionId") Integer positionId);

    List<String> getUserAccessibleCommercialPolicyDealerCodes(@Param("userId") String userId, @Param("positionId") Integer positionId);

}