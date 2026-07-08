package com.sph.hyu.batch.report.gamrir.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 감리(監理) 리포트 집계 루트 엔티티.
 */
@Getter
@Setter
@Entity
@Table(schema = "scop", name = "TB_RPT_MT_SUPERVISION")
public class TbRptMtSupervision implements Serializable {

    @Id
    @Column(name = "SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_rpt_mt_supervision_seq_generator")
    @SequenceGenerator(name = "tb_rpt_mt_supervision_seq_generator",
            sequenceName = "scop.tb_rpt_mt_supervision_seq",
            allocationSize = 1)
    private int seq;

    @Column(name = "PRJ_ID")
    private UUID prjId;

    @Column(name = "WORK_DATE")
    private String workDate;

    @Column(name = "TODAY_WORK")
    private String todayWork;

    @Column(name = "TOMORROW_PLAN")
    private String tomorrowPlan;

    @OneToMany(mappedBy = "report")
    private List<TbRptMtSupervisionWorkType> workTypes = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TbRptMtSupervisionWorkerInput> workerInputs = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TbRptMtSupervisionAssetInput> assetInputs = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TbRptMtSupervisionDHist> dHists = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TbRptMtSupervisionQuality> qualities = new ArrayList<>();

    @OneToMany(mappedBy = "report")
    private List<TbRptMtSupervisionInspection> inspections = new ArrayList<>();

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private TbRptMtSupervisionSafety safety;

    /** 생성 일자 */
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REG_DT")
    private Date regDt;
}
