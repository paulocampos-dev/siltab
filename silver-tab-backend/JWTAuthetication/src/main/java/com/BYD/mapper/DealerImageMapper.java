package com.BYD.mapper;

import com.BYD.dto.dealer.DealerImageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
@Component
public interface DealerImageMapper {

    List<DealerImageDTO> findByDealerCode(@Param("dealerCode") String dealerCode);

    List<DealerImageDTO> findByDealerCodeAndType(@Param("dealerCode") String dealerCode, @Param("typeId") Long typeId);

    DealerImageDTO findById(@Param("imageId") Long imageId);

    void insertImage(DealerImageDTO imageInfo);

    void insertDealerImage(
            @Param("imageId") Long imageId,
            @Param("dealerCode") String dealerCode,
            @Param("imageTypeId") Long imageTypeId
    );

    void deleteImageFromDealerImage(@Param("imageId") Long imageId);
    void deleteImageFromImageInfo(@Param("imageId") Long imageId);

    Long getImageTypeIdByName(@Param("typeName") String typeName);

    String findDealerCodeByImageId(@Param("imageId") Long imageId);

    boolean isValidImageType(@Param("imageType") String imageType);
}