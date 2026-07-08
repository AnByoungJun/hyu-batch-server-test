package com.sph.hyu.batch.productivity.domain;

import com.sph.hyu.batch.productivity.domain.type.JobStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 생산성 집계 배치 Job 실행 이력.
 */
@Getter
@Setter
@Entity
@Table(schema = "scop", name = "TB_BATCH_PRODUCTIVITY_JOB")
public class TbBatchProductivityJob implements Serializable {

    @Id
    @Column(name = "SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_batch_productivity_job_seq_gen")
    @SequenceGenerator(name = "tb_batch_productivity_job_seq_gen",
            sequenceName = "scop.tb_batch_productivity_job_seq",
            allocationSize = 1)
    private int seq;

    /** 배치 Job 고유 식별자 */
    @Column(name = "JOB_ID")
    private UUID jobId;

    /** 프로젝트 ID */
    @Column(name = "PRJ_ID")
    private UUID prjId;

    /** 집계 작업일자 (yyyyMMdd) */
    @Column(name = "WORK_DATE")
    private String workDate;

    /** Job 진행 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private JobStatus status;

    /** 집계 총 건수 */
    @Column(name = "TOTAL_COUNT")
    private int totalCount;

    /** API 전송 성공 건수 */
    @Column(name = "SUCCESS_COUNT")
    private int successCount;

    /** API 전송 실패 건수 */
    @Column(name = "FAIL_COUNT")
    private int failCount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DT")
    private Date startDt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DT")
    private Date endDt;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REG_DT")
    private Date regDt;
}
