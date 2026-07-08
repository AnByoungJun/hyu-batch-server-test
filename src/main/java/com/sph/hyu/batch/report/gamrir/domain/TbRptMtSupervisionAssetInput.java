package com.sph.hyu.batch.report.gamrir.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * 감리 리포트 - 장비 투입 현황.
 */
@Getter
@Setter
@Entity
@Table(schema = "scop", name = "TB_RPT_MT_SUPERVISION_ASSET_INPUT")
public class TbRptMtSupervisionAssetInput implements Serializable {

    @Id
    @Column(name = "SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_rpt_mt_supervision_asset_input_seq_generator")
    @SequenceGenerator(name = "tb_rpt_mt_supervision_asset_input_seq_generator",
            sequenceName = "scop.tb_rpt_mt_supervision_asset_input_seq",
            allocationSize = 1)
    private int seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    private TbRptMtSupervision report;

    @Column(name = "ASSET_TYPE")
    private String assetType;

    @Column(name = "ASSET_ID")
    private UUID assetId;

    @Column(name = "ASSET_NM")
    private String assetNm;

    @Column(name = "ASSET_STANDARD")
    private String assetStandard;

    @Column(name = "YESTERDAY_COUNT")
    private int yesterdayCount;

    @Column(name = "TODAY_COUNT")
    private int todayCount;

    @Column(name = "REMARK")
    private String remark;
}
