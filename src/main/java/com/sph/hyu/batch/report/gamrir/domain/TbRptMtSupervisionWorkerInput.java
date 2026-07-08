package com.sph.hyu.batch.report.gamrir.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 감리 리포트 - 인력 투입 현황.
 */
@Getter
@Setter
@Entity
@Table(schema = "scop", name = "TB_RPT_MT_SUPERVISION_WORKER_INPUT")
public class TbRptMtSupervisionWorkerInput implements Serializable {

    @Id
    @Column(name = "SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_rpt_mt_supervision_worker_input_seq_generator")
    @SequenceGenerator(name = "tb_rpt_mt_supervision_worker_input_seq_generator",
            sequenceName = "scop.tb_rpt_mt_supervision_worker_input_seq",
            allocationSize = 1)
    private int seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    private TbRptMtSupervision report;

    @Column(name = "SORT_1")
    private String sort1;

    @Column(name = "SORT_2")
    private String sort2;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "YESTERDAY_COUNT")
    private int yesterdayCount;

    @Column(name = "TODAY_COUNT")
    private int todayCount;

    @Column(name = "REMARK")
    private String remark;
}
