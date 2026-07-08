package com.sph.hyu.batch.report.gamrir.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 감리 리포트 - 검측(검사) 항목.
 */
@Getter
@Setter
@Entity
@Table(schema = "scop", name = "TB_RPT_MT_SUPERVISION_INSPECTION")
public class TbRptMtSupervisionInspection implements Serializable {

    @Id
    @Column(name = "SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_rpt_mt_supervision_inspection_seq_generator")
    @SequenceGenerator(name = "tb_rpt_mt_supervision_inspection_seq_generator",
            sequenceName = "scop.tb_rpt_mt_supervision_inspection_seq",
            allocationSize = 1)
    private int seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    private TbRptMtSupervision report;

    @Column(name = "MODEL_NM")
    private String modelNm;

    @Column(name = "WORK_TYPE")
    private String workType;

    @Column(name = "INSPECTION_PART")
    private String inspectionPart;

    @Column(name = "AVG_TOPOGRAPHY_ALTITUDE")
    private float avgTopographyAltitude;

    @Column(name = "AVG_TARGET_ALTITUDE")
    private float avgTargetAltitude;
}
