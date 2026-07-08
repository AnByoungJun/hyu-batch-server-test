package com.sph.hyu.batch.productivity.service;

import com.sph.hyu.batch.productivity.dto.ProductivityRow;
import com.sph.hyu.batch.productivity.repository.ProductivityAggregationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 일 집계 수행 서비스. 장비 유형별 시간당 작업량 및 기준 달성률을 DB 에서 집계한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductivityAggregationService {

    private final ProductivityAggregationRepository aggregationRepository;

    public List<ProductivityRow> aggregate(UUID prjId, String workDate) {
        log.info("[집계] prjId={}, workDate={}", prjId, workDate);

        List<ProductivityRow> results = aggregationRepository.getProductivityData(prjId.toString(), workDate)
                .stream()
                .map(this::toRow)
                .toList();

        log.info("[집계] 완료: {}건", results.size());
        return results;
    }

    private ProductivityRow toRow(Map<String, Object> row) {
        return new ProductivityRow(
                str(row, "work_date"),
                str(row, "asset_type"),
                str(row, "asset_type_nm"),
                str(row, "asset_id"),
                str(row, "asset_nm"),
                str(row, "user_id"),
                decimal(row, "work_volume_per_hour"),
                decimal(row, "work_volume_per_hour_rate")
        );
    }

    private String str(Map<String, Object> row, String key) {
        Object val = row.get(key);
        return val != null ? val.toString() : null;
    }

    private BigDecimal decimal(Map<String, Object> row, String key) {
        Object val = row.get(key);
        if (val == null) {
            return null;
        }
        if (val instanceof BigDecimal bd) {
            return bd;
        }
        try {
            return new BigDecimal(val.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
