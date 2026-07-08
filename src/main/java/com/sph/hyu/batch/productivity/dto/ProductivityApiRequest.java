package com.sph.hyu.batch.productivity.dto;

import com.sph.hyu.batch.productivity.domain.TbBatchProductivityQueue;

import java.math.BigDecimal;

/**
 * 외부 API 전송 요청 본문. (불변 record)
 */
public record ProductivityApiRequest(
        String prjId,
        String workDate,
        String assetId,
        String assetNm,
        String assetType,
        String assetTypeNm,
        BigDecimal workVolumePerHour,
        BigDecimal workVolumePerHourRate
) {
    /** Queue 엔티티로부터 전송 요청 DTO 를 생성한다. */
    public static ProductivityApiRequest from(TbBatchProductivityQueue item) {
        return new ProductivityApiRequest(
                item.getPrjId() != null ? item.getPrjId().toString() : null,
                item.getWorkDate(),
                item.getAssetId() != null ? item.getAssetId().toString() : null,
                item.getAssetNm(),
                item.getAssetType(),
                item.getAssetTypeNm(),
                item.getWorkVolumePerHour(),
                item.getWorkVolumePerHourRate()
        );
    }
}
