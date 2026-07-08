package com.sph.hyu.batch.report.gamrir.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 감리 리포트 - 공종별 작업량.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(schema = "scop", name = "TB_RPT_MT_SUPERVISION_WORK_TYPE")
public class TbRptMtSupervisionWorkType implements Serializable {

    @Id
    @Column(name = "SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_rpt_mt_supervision_work_type_seq_generator")
    @SequenceGenerator(name = "tb_rpt_mt_supervision_work_type_seq_generator",
            sequenceName = "scop.tb_rpt_mt_supervision_work_type_seq",
            allocationSize = 1)
    private int seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    private TbRptMtSupervision report;

    /** 공종 */
    @Column(name = "WORK_TYPE")
    private String workType;

    /** 단위 */
    @Column(name = "UNIT")
    private String unit;

    /** 설계 수량 */
    @Column(name = "COUNT")
    private int count;

    /** 설계 금액 */
    @Column(name = "COST")
    private int cost;

    /** 전일까지 성토 작업량 */
    @Column(name = "YESTERDAY_FILLING_WORK_VOLUME")
    private int yesterdayFillingWorkVolume;

    /** 전일까지 절토 작업량 */
    @Column(name = "YESTERDAY_CUTTING_WORK_VOLUME")
    private int yesterdayCuttingWorkVolume;

    /** 금일 성토 작업량 */
    @Column(name = "TODAY_FILLING_WORK_VOLUME")
    private int todayFillingWorkVolume;

    /** 금일 절토 작업량 */
    @Column(name = "TODAY_CUTTING_WORK_VOLUME")
    private int todayCuttingWorkVolume;
}
