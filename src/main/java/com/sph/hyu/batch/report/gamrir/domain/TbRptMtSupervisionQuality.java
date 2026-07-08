package com.sph.hyu.batch.report.gamrir.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 감리 리포트 - 품질(다짐) 항목.
 */
@Getter
@Setter
@Entity
@Table(schema = "scop", name = "TB_RPT_MT_SUPERVISION_QUALITY")
public class TbRptMtSupervisionQuality {

    @Id
    @Column(name = "SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_rpt_mt_supervision_quality_seq_generator")
    @SequenceGenerator(name = "tb_rpt_mt_supervision_quality_seq_generator",
            sequenceName = "scop.tb_rpt_mt_supervision_quality_seq",
            allocationSize = 1)
    private int seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    private TbRptMtSupervision report;

    @Column(name = "GEO_FENCE_ID")
    private String geoFenceId;

    @Column(name = "MATERIAL")
    private String material;

    @Column(name = "PULLING_THICKNESS")
    private float pullingThickness;

    @Column(name = "COMPACTION_COUNT")
    private float compactionCnt;

    @Column(name = "MANUAL_COMPACTION_COUNT")
    private float manualCompactionCnt;

    @OneToMany(mappedBy = "quality", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TbRptMtSupervisionQualityTestOption> testOptions = new ArrayList<>();
}
