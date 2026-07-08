package com.sph.hyu.batch.report.gamrir.repository;

import com.sph.hyu.batch.report.gamrir.domain.TbRptMtSupervisionWorkerInput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SupervisionWorkerInputRepository extends JpaRepository<TbRptMtSupervisionWorkerInput, Integer> {

    /** 인력(근로자) 투입 현황 (금일/전일 인원 누적) — 검증된 원본 SQL 재사용 */
    String GET_WORKFORCE_MANAGEMENT = ""
            + " WITH date_range AS ( "
            + "   SELECT prj_id "
            + "         ,TO_DATE(event_date, 'YYYYMMDD')\\:\\:date  AS event_date "
            + "     FROM iot.tb_iot_it_sensor_tracking_hist tiisth "
            + "    WHERE 1 = 1 "
            + "      AND prj_id = :prjId "
            + "      AND event_date = :eventDate "
            + "      AND type = 'PT' "
            + "    GROUP BY prj_id, event_date "
            + " ),"
            + " cumulative_counts AS ( "
            + "   SELECT tpmp.prj_id "
            + "         ,tpmp.prj_nm "
            + "         ,\\'근로자\\' as sort_1 "
            + "         ,tiisth.type as sort_2 "
            + "         ,TO_DATE(event_date, 'YYYYMMDD')\\:\\:date  AS event_date "
            + "         ,COALESCE ( "
            + "           SUM(COUNT(DISTINCT tiisth.sensor_id)) OVER ( "
            + "             PARTITION BY tpmp.prj_id, tpmp.prj_nm, tiisth.type "
            + "                 ORDER BY TO_DATE(tiisth.event_date, 'YYYYMMDD') "
            + "                  ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING "
            + "           ) "
            + "         , 0) AS yesterday_count "
            + "        , COUNT(DISTINCT tiisth.sensor_id) AS today_count "
            + "    FROM iot.tb_iot_it_sensor_tracking_hist tiisth "
            + "    LEFT JOIN "
            + "      scop.tb_prj_mt_project tpmp ON tiisth.prj_id = tpmp.prj_id "
            + "   WHERE 1 = 1 "
            + "     AND tiisth.prj_id = :prjId "
            + "     AND TO_DATE(tiisth.event_date, 'YYYYMMDD') >= TO_DATE(TO_CHAR(tpmp.reg_dt, 'YYYYMMDD'), 'YYYYMMDD') "
            + "     AND tiisth.type = 'PT' "
            + "   GROUP BY tpmp.prj_id, tiisth.type, tiisth.event_date "
            + " ) "
            + " SELECT "
            + "   CAST(c.prj_id AS VARCHAR) AS prj_id "
            + "  ,c.prj_nm "
            + "  ,c.sort_1 "
            + "  ,c.sort_2 "
            + "  ,COALESCE(c.yesterday_count, 0) AS yesterday_count "
            + "  ,COALESCE(c.today_count, 0) AS today_count "
            + "   FROM date_range dr "
            + "   LEFT JOIN "
            + "     cumulative_counts c "
            + "     ON dr.prj_id = c.prj_id "
            + "    AND dr.event_date = c.event_date ";

    @Query(value = GET_WORKFORCE_MANAGEMENT, nativeQuery = true)
    List<Map<String, Object>> getWorkforceManagement(@Param("prjId") UUID prjId,
                                                      @Param("eventDate") String eventDate);
}
