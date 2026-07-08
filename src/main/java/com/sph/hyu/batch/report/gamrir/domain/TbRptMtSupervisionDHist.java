package com.sph.hyu.batch.report.gamrir.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

/**
 * 감리 리포트 - 장비 위험요소(경사도 D) 이력.
 */
@Getter
@Setter
@Entity
@Table(schema = "scop", name = "TB_RPT_MT_SUPERVISION_D_HIST")
public class TbRptMtSupervisionDHist {

    @Id
    @Column(name = "SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_rpt_mt_supervision_d_hist_seq_generator")
    @SequenceGenerator(name = "tb_rpt_mt_supervision_d_hist_seq_generator",
            sequenceName = "scop.tb_rpt_mt_supervision_d_hist_seq",
            allocationSize = 1)
    private int seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    private TbRptMtSupervision report;

    @Column(name = "ASSET_ID")
    private UUID assetId;

    @Column(name = "ASSET_TYPE")
    private String assetType;

    @Column(name = "D")
    private Float d;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EVENT_DT")
    private Date eventDt;

    @Column(name = "TRACKING_ID")
    private String trackingId;

    @Column(name = "USER_ID")
    private String userId;
}
