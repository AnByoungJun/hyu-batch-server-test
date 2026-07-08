package com.sph.hyu.batch.productivity.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 생산성 배치 자동 실행 스케줄 (프로젝트별 실행 시각).
 */
@Getter
@Setter
@Entity
@Table(schema = "scop", name = "TB_BATCH_SCHEDULE")
public class TbBatchSchedule {

    @Id
    @Column(name = "PRJ_ID")
    private UUID prjId;

    /** 실행 시각 (HH:mm) */
    @Column(name = "RUN_TIME")
    private String runTime;

    /** 당일 중복 실행 방지용 최종 실행일 */
    @Column(name = "LAST_RUN_DATE")
    private LocalDate lastRunDate;

    /** 활성 여부 (Y/N) */
    @Column(name = "USE_YN")
    private String useYn;
}
