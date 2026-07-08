package com.sph.hyu.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * hyu-batch 애플리케이션 진입점.
 *
 * <p>건설현장 IoT 데이터를 처리하는 두 개의 배치 도메인을 포함한다.
 * <ul>
 *   <li>productivity : 장비 생산성 집계 → 외부 API 전송 (Spring Batch Job)</li>
 *   <li>report.gamrir : 감리 리포트 생성 (스케줄 기반)</li>
 * </ul>
 *
 * <p>Spring Batch Job 은 부팅 시 자동 실행하지 않고(스케줄러/REST 로 수동 트리거),
 * 메타 테이블은 {@code scop.BATCH_} prefix 로 관리한다. (application.yml 참조)
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class HyuBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(HyuBatchApplication.class, args);
    }
}
