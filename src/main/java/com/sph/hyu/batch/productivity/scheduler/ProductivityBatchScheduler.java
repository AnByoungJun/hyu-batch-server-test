package com.sph.hyu.batch.productivity.scheduler;

import com.sph.hyu.batch.productivity.domain.TbBatchSchedule;
import com.sph.hyu.batch.productivity.repository.BatchScheduleRepository;
import com.sph.hyu.batch.productivity.service.ProductivityJobLauncher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * DB 에 저장된 run_time 기준으로 매일 Job 을 자동 실행하는 폴링 스케줄러.
 * <ul>
 *   <li>매 1분마다 실행 대상 스케줄을 조회</li>
 *   <li>last_run_date 를 먼저 업데이트하여 중복 실행 방지</li>
 * </ul>
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "hyu.scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class ProductivityBatchScheduler {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final BatchScheduleRepository scheduleRepository;
    private final ProductivityJobLauncher jobLauncher;

    @Async
    @Scheduled(fixedRate = 60_000)
    public void poll() {
        String currentTime = LocalTime.now().format(TIME_FMT);
        LocalDate today = LocalDate.now();

        List<TbBatchSchedule> schedules = scheduleRepository.findSchedulesToRun(currentTime, today);
        if (schedules.isEmpty()) {
            return;
        }

        log.info("[스케줄 폴링] 실행 대상 {}건 (현재시각={})", schedules.size(), currentTime);

        for (TbBatchSchedule schedule : schedules) {
            // 중복 실행 방지: 실행 전 last_run_date 먼저 저장
            schedule.setLastRunDate(today);
            scheduleRepository.save(schedule);

            String workDate = today.minusDays(1).format(DATE_FMT);
            String prjId = schedule.getPrjId().toString();

            log.info("[스케줄 폴링] Job 실행: prjId={}, workDate={}", prjId, workDate);
            try {
                jobLauncher.launch(prjId, workDate);
            } catch (Exception e) {
                log.error("[스케줄 폴링] Job 실행 오류: prjId={}, workDate={}, error={}", prjId, workDate, e.getMessage(), e);
            }
        }
    }
}
