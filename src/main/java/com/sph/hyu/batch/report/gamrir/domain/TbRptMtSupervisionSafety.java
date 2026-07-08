package com.sph.hyu.batch.report.gamrir.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 감리 리포트 - 안전 관리 (공종/지시사항/조치사항).
 */
@Getter
@Setter
@Entity
@Table(schema = "scop", name = "TB_RPT_MT_SUPERVISION_SAFETY")
public class TbRptMtSupervisionSafety {

    @Id
    @Column(name = "SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_rpt_mt_supervision_safety_seq_generator")
    @SequenceGenerator(name = "tb_rpt_mt_supervision_safety_seq_generator",
            sequenceName = "scop.tb_rpt_mt_supervision_safety_seq",
            allocationSize = 1)
    private int seq;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    private TbRptMtSupervision report;

    /** 공종 */
    @Column(name = "WORK_TYPE")
    private String workType;

    /** 지시사항 */
    @Column(name = "INSTRUCTION")
    private String instruction;

    /** 조치사항 */
    @Column(name = "ACTION_TAKEN")
    private String actionTaken;
}
