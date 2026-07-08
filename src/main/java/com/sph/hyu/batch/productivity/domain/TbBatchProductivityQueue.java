package com.sph.hyu.batch.productivity.domain;

import com.sph.hyu.batch.productivity.domain.type.QueueStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * 외부 API 로 전송할 생산성 데이터 Queue 아이템.
 */
@Getter
@Setter
@Entity
@Table(schema = "scop", name = "TB_BATCH_PRODUCTIVITY_QUEUE")
public class TbBatchProductivityQueue implements Serializable {

    @Id
    @Column(name = "SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_batch_productivity_queue_seq_gen")
    @SequenceGenerator(name = "tb_batch_productivity_queue_seq_gen",
            sequenceName = "scop.tb_batch_productivity_queue_seq",
            allocationSize = 1)
    private int seq;

    /** 배치 Job 고유 식별자 */
    @Column(name = "JOB_ID")
    private UUID jobId;

    @Column(name = "PRJ_ID")
    private UUID prjId;

    /** 집계 작업일자 (yyyyMMdd) */
    @Column(name = "WORK_DATE")
    private String workDate;

    @Column(name = "ASSET_ID")
    private UUID assetId;

    @Column(name = "ASSET_NM")
    private String assetNm;

    /** 장비 유형 코드 (01:불도저, 02:굴삭기, 08:모토그레이더, 09:롤러) */
    @Column(name = "ASSET_TYPE")
    private String assetType;

    @Column(name = "ASSET_TYPE_NM")
    private String assetTypeNm;

    /** 시간당 작업량 */
    @Column(name = "WORK_VOLUME_PER_HOUR")
    private BigDecimal workVolumePerHour;

    /** 시간당 작업량 기준 달성률 (%) */
    @Column(name = "WORK_VOLUME_PER_HOUR_RATE")
    private BigDecimal workVolumePerHourRate;

    /** Queue 아이템 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private QueueStatus status;

    /** API 전송 재시도 횟수 */
    @Column(name = "RETRY_COUNT")
    private int retryCount;

    /** 전송 실패 시 오류 메시지 */
    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REG_DT")
    private Date regDt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPD_DT")
    private Date updDt;
}
