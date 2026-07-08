package com.sph.hyu.batch.report.gamrir.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 감리 리포트 - 품질 시험 옵션.
 */
@Getter
@Setter
@Entity
@Table(schema = "scop", name = "TB_RPT_MT_SUPERVISION_QUALITY_TEST_OPTION")
public class TbRptMtSupervisionQualityTestOption {

    @Id
    @Column(name = "SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_rpt_mt_supervision_quality_test_option_seq_generator")
    @SequenceGenerator(name = "tb_rpt_mt_supervision_quality_test_option_seq_generator",
            sequenceName = "scop.tb_rpt_mt_supervision_quality_test_option_seq",
            allocationSize = 1)
    private int seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUALITY_ID")
    private TbRptMtSupervisionQuality quality;

    @Column(name = "COMPACTION_COUNT")
    private int compactionCnt;

    @Column(name = "COEFFICIENT_OF_BEARING_CAPACITY")
    private float coefficientOfBearingCapacity;

    @Column(name = "SELECTED")
    private boolean selected;
}
