package com.sph.hyu.batch.report.gamrir.repository;

import com.sph.hyu.batch.report.gamrir.domain.TbRptMtSupervision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface SupervisionReportRepository extends JpaRepository<TbRptMtSupervision, Integer> {

    /** 해당 일자에 트래킹 데이터가 존재하는 (prj_id, work_date) 목록 */
    String GET_WORK_DATES = """
        select CAST(prj_id AS VARCHAR) AS prj_id
             ,gps_date as work_date
          from (
            select prj_id, gps_date
              from iot.tb_iot_it_tracking_load tiitl
            union all
            select prj_id, gps_date
              from iot.tb_iot_it_tracking_load_earthwork tiitle
          ) load
         where 1 = 1
           and gps_date = :eventDate
         group by prj_id, gps_date
        """;

    @Query(value = GET_WORK_DATES, nativeQuery = true)
    List<Map<String, Object>> getWorkDates(@Param("eventDate") String eventDate);

    Optional<TbRptMtSupervision> findByPrjIdAndWorkDate(UUID prjId, String workDate);
}
