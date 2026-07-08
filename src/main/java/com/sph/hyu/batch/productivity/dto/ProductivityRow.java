package com.sph.hyu.batch.productivity.dto;

import java.math.BigDecimal;

/**
 * 집계 쿼리 결과 1행. (불변 record)
 *
 * @param workDate               작업일자 (yyyyMMdd)
 * @param assetType              장비 유형 코드
 * @param assetTypeNm            장비 유형명
 * @param assetId                장비 ID (UUID 문자열)
 * @param assetNm                장비명
 * @param userId                 사용자 ID
 * @param workVolumePerHour      시간당 작업량
 * @param workVolumePerHourRate  시간당 작업량 기준 달성률(%)
 */
public record ProductivityRow(
        String workDate,
        String assetType,
        String assetTypeNm,
        String assetId,
        String assetNm,
        String userId,
        BigDecimal workVolumePerHour,
        BigDecimal workVolumePerHourRate
) {
}
