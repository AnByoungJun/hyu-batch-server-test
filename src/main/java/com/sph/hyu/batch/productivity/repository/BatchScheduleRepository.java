package com.sph.hyu.batch.productivity.repository;

import com.sph.hyu.batch.productivity.domain.TbBatchSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BatchScheduleRepository extends JpaRepository<TbBatchSchedule, UUID> {

    /**
     * 실행 대상 스케줄 조회.
     * <ul>
     *   <li>활성 상태 (use_yn = 'Y')</li>
     *   <li>현재 시각 &ge; run_time (실행 시각 도달)</li>
     *   <li>오늘 아직 실행되지 않음 (last_run_date &lt; 오늘 또는 null)</li>
     * </ul>
     */
    @Query("SELECT s FROM TbBatchSchedule s " +
           "WHERE s.useYn = 'Y' " +
           "AND s.runTime <= :currentTime " +
           "AND (s.lastRunDate IS NULL OR s.lastRunDate < :today)")
    List<TbBatchSchedule> findSchedulesToRun(@Param("currentTime") String currentTime,
                                             @Param("today") LocalDate today);
}
