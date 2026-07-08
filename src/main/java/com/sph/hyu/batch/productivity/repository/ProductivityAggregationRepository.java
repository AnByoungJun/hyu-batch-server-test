package com.sph.hyu.batch.productivity.repository;

import com.sph.hyu.batch.productivity.domain.TbBatchProductivityJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 장비 유형별(불도저 01 / 굴삭기 02 / 모토그레이더 08 / 롤러 09) 시간당 작업량 및
 * 기준 달성률(%)을 집계하는 네이티브 쿼리 Repository.
 *
 * <p>쿼리 로직은 기존 검증된 SQL 을 그대로 재사용한다.
 */
@Repository
public interface ProductivityAggregationRepository extends JpaRepository<TbBatchProductivityJob, Integer> {

    String AGGREGATE_QUERY = """
        select result.work_date
              ,result.asset_type
              ,result.asset_type_nm
              ,CAST(result.asset_id AS VARCHAR) AS asset_id
              ,result.asset_nm
              ,result.user_id
              ,( CASE
                   WHEN result.asset_type = '01' then
                       round(sum(result.work_cycle) * tpipau.dozer_blade_capacity
                                       * (case when tpipau.dozer_pulling_thickness\\:\\:numeric = 10 then 1.298
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 20 then 1.539
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 30 then 1.89
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 40 then 2.39
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 50 then 3.094
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 60 then 3.97
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 70 then 5.271
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 80 then 7.088
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 90 then 10.882
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 100 then 16.972
                                          end)\\:\\:numeric
                                       / (round(((sum(result.total_tracking_time)) / 3600)\\:\\:numeric, 10)), 2)
                   WHEN result.asset_type = '02' THEN
                       round(sum(result.work_cycle) * tpipau.excavator_bucket_capacity
                       / (round(sum(result.total_tracking_time)) / 3600)\\:\\:numeric, 2)
                   WHEN result.asset_type = '08' then
                       round((sum(result.l1) * tpipau.grader_blade_width / sum(result.total_tracking_time))\\:\\:numeric, 2)
                   WHEN result.asset_type = '09' THEN
                       round((((sum(result.l1) + sum(result.l2)) * tpipau.roller_drum_width) / round(((sum(result.total_tracking_time)) / 3600)\\:\\:numeric, 10))\\:\\:numeric, 2)
                 END
               ) AS work_volume_per_hour
              ,( case
                   WHEN result.asset_type = '01' THEN
                       round(((sum(result.work_cycle) * tpipau.dozer_blade_capacity
                                       * (case when tpipau.dozer_pulling_thickness\\:\\:numeric = 10 then 1.298
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 20 then 1.539
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 30 then 1.89
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 40 then 2.39
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 50 then 3.094
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 60 then 3.97
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 70 then 5.271
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 80 then 7.088
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 90 then 10.882
                                            when tpipau.dozer_pulling_thickness\\:\\:numeric = 100 then 16.972
                                          end)\\:\\:numeric
                                       / (round(((sum(result.total_tracking_time)) / 3600)\\:\\:numeric, 10)))
                       - result.work_volume_criteria) / result.work_volume_criteria * 100, 2)
                   WHEN result.asset_type = '02' THEN
                       round(((round(sum(result.work_cycle) * tpipau.excavator_bucket_capacity / (round(sum(result.total_tracking_time)) / 3600)\\:\\:numeric, 2) - result.work_volume_criteria)
                       / result.work_volume_criteria * 100)\\:\\:numeric, 2)
                   WHEN result.asset_type = '08' then
                       round(((round((sum(result.l1) * tpipau.grader_blade_width / sum(result.total_tracking_time))\\:\\:numeric, 2) - result.work_volume_criteria) / result.work_volume_criteria * 100)\\:\\:numeric, 2)
                   WHEN result.asset_type = '09' THEN
                       round(((round((((sum(result.l1) + sum(result.l2)) * tpipau.roller_drum_width) / round(((sum(result.total_tracking_time)) / 3600)\\:\\:numeric, 10))\\:\\:numeric, 2) - result.work_volume_criteria) / result.work_volume_criteria * 100)\\:\\:numeric, 2)
                 END
               ) AS work_volume_per_hour_rate
          from (
            select tiitle.seq, tiitle.gps_date as work_date
                  ,tiitle.prj_id
                  ,tiitle.asset_id
                  ,tiitle.user_id
                  ,tsma.asset_nm
                  ,tiitle.asset_type
                  ,( select tsmc.comm_cd_nm
                        from scop.tb_sys_mt_comm tsmc
                       where tsmc.grp_cd = 'ASSET_TYPE'
                         and tsmc.comm_cd = tiitle.asset_type
                  ) as asset_type_nm
                  ,tiitle.total_tracking_time
                  ,tiitle.total_idle_time
                  ,tiitle.cycle_time
                  ,tiitle.work_cycle
                  ,( CASE
                       WHEN tiitle.asset_type = '01' THEN tpmp.dozer_cycle_time_criteria
                       WHEN tiitle.asset_type = '02' THEN tpmp.excavator_cycle_time_criteria
                       WHEN tiitle.asset_type = '08' THEN tpmp.grader_cycle_time_criteria
                       WHEN tiitle.asset_type = '09' THEN tpmp.roller_cycle_time_criteria
                       WHEN tiitle.asset_type = '17' THEN tpmp.paver_cycle_time_criteria
                     END
                  ) AS cycle_time_criteria
                  ,( CASE
                       WHEN tiitle.asset_type = '01' THEN tpmp.dozer_work_volume_criteria
                       WHEN tiitle.asset_type = '02' THEN tpmp.excavator_work_volume_criteria
                       WHEN tiitle.asset_type = '08' THEN tpmp.grader_work_volume_criteria
                       WHEN tiitle.asset_type = '09' THEN tpmp.roller_work_volume_criteria
                       WHEN tiitle.asset_type = '17' THEN tpmp.paver_work_volume_criteria
                     END
                  ) AS work_volume_criteria
                  ,( CASE
                       WHEN tiitle.asset_type = '01' THEN
                         (select sum(trmecd.l1) from scop.tb_rpt_mt_earthwork_cycle_dozer trmecd where trmecd.earthwork_id = CAST(tiitle.seq AS varchar))
                       WHEN tiitle.asset_type = '08' THEN
                         (select sum(trmecg.l1) from scop.tb_rpt_mt_earthwork_cycle_grader trmecg where trmecg.earthwork_id = CAST(tiitle.seq AS varchar))
                       WHEN tiitle.asset_type = '09' THEN
                         (select sum(trmecr.l1) from scop.tb_rpt_mt_earthwork_cycle_roller trmecr where trmecr.earthwork_id = CAST(tiitle.seq AS varchar))
                     END
                  ) AS l1
                  ,( CASE
                       WHEN tiitle.asset_type = '01' THEN
                         (select sum(trmecd.l2) from scop.tb_rpt_mt_earthwork_cycle_dozer trmecd where trmecd.earthwork_id = CAST(tiitle.seq AS varchar))
                       WHEN tiitle.asset_type = '08' THEN
                         (select sum(trmecg.l2) from scop.tb_rpt_mt_earthwork_cycle_grader trmecg where trmecg.earthwork_id = CAST(tiitle.seq AS varchar))
                       WHEN tiitle.asset_type = '09' THEN
                         (select sum(trmecr.l2) from scop.tb_rpt_mt_earthwork_cycle_roller trmecr where trmecr.earthwork_id = CAST(tiitle.seq AS varchar))
                     END
                  ) AS l2
              from iot.tb_iot_it_tracking_load_earthwork tiitle
             inner join scop.tb_prj_mt_project tpmp on tiitle.prj_id = tpmp.prj_id
             inner join scop.tb_sys_mt_asset tsma on tiitle.asset_id = tsma.asset_id
             where tiitle.prj_id = CAST(:prjId AS uuid)
               and tiitle.gps_date between :workDate and :workDate
               and tiitle.asset_type in ('01', '08', '09', '02')
             order by tiitle.gps_date asc, tiitle.asset_type asc
          ) result
         inner join scop.tb_prj_it_project_asset_user tpipau
            on tpipau.prj_id = result.prj_id
           and tpipau.asset_id = result.asset_id
           and tpipau.user_id = result.user_id
         group by result.work_date
              ,result.asset_type
              ,result.user_id
              ,result.asset_id
              ,result.asset_nm
              ,result.asset_type_nm
              ,result.cycle_time_criteria
              ,result.work_volume_criteria
              ,tpipau.grader_blade_width
              ,tpipau.dozer_blade_capacity
              ,tpipau.dozer_pulling_thickness
              ,tpipau.excavator_bucket_capacity
              ,tpipau.roller_drum_width
        """;

    @Query(value = AGGREGATE_QUERY, nativeQuery = true)
    List<Map<String, Object>> getProductivityData(@Param("prjId") String prjId,
                                                   @Param("workDate") String workDate);
}
