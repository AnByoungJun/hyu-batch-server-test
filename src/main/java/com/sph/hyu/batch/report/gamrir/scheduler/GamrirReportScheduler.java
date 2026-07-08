package com.sph.hyu.batch.report.gamrir.scheduler;

import com.sph.hyu.batch.report.gamrir.service.GamrirQualityReportService;
import com.sph.hyu.batch.report.gamrir.service.GamrirSupervisionReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 감리 리포트 생성 스케줄러. 비즈니스 로직은 서비스에 위임하고 실행 트리거만 담당한다.
 * <ul>
 *   <li>05:00 - 전일(D-1) 품질 리포트 재생성</li>
 *   <li>17:00 - 금일(D)  감리 리포트 생성/갱신</li>
 * </ul>
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "hyu.scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class GamrirReportScheduler {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final GamrirQualityReportService qualityReportService;
    private final GamrirSupervisionReportService supervisionReportService;

    @Async
    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Seoul")
    public void runQualityReport() {
        log.info("====================== Gamri Quality Schedule Start ======================");
        long start = System.currentTimeMillis();

        String yesterday = LocalDate.now().minusDays(1).format(DATE_FMT);
        qualityReportService.generate(yesterday);

        log.info("프로세스 실행 시간: {} 초", (System.currentTimeMillis() - start) / 1000.0);
        log.info("====================== Gamri Quality Schedule End ======================");
    }

    @Async
    @Scheduled(cron = "0 0 17 * * *", zone = "Asia/Seoul")
    public void runSupervisionReport() {
        log.info("====================== GamriReport Schedule Start ======================");
        long start = System.currentTimeMillis();

        String today = LocalDate.now().format(DATE_FMT);
        supervisionReportService.generate(today);

        log.info("프로세스 실행 시간: {} 초", (System.currentTimeMillis() - start) / 1000.0);
        log.info("====================== GamriReport Schedule End ======================");
    }
}
