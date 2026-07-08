package com.sph.hyu.batch.report.gamrir.repository;

import com.sph.hyu.batch.report.gamrir.domain.TbRptMtSupervisionDHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SupervisionDHistRepository extends JpaRepository<TbRptMtSupervisionDHist, Integer> {

    /** 장비 위험요소(경사도 D = √(roll² + pitch²)) 이력 — 검증된 원본 SQL 재사용 */
    String GET_EQUIPMENT_RISK_FACTOR = ""
            + " SELECT "
            + "        CAST(tiith.prj_id AS VARCHAR) AS prj_id"
            + "       ,tiith.tracking_id "
            + "       ,CAST(tiith.asset_id AS VARCHAR) AS asset_id "
            + "       ,tiith.asset_type "
            + "       ,tiith.user_id "
            + "       ,TO_DATE(tiith.event_date, 'YYYYMMDD')\\:\\:date  AS event_date "
            + "       ,tiith.event_dt "
            + "       ,CAST(SQRT(COALESCE(tiith.ROLL , 0)^2 + COALESCE(tiith.PITCH, 0)^2) AS VARCHAR) AS d  "
            + "   FROM iot.tb_iot_it_tracking_hist tiith "
            + "  WHERE 1 = 1 "
            + "    AND prj_id = :prjId "
            + "    AND event_date = :eventDate "
            + "  ORDER BY prj_id, asset_id, event_dt ASC  ";

    @Query(value = GET_EQUIPMENT_RISK_FACTOR, nativeQuery = true)
    List<Map<String, Object>> getEquipmentRiskFactor(@Param("prjId") UUID prjId,
                                                      @Param("eventDate") String eventDate);
}
