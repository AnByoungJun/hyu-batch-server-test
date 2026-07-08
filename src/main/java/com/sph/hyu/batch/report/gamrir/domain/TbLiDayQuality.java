package com.sph.hyu.batch.report.gamrir.domain;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 일별 품질(다짐) 원천 데이터.
 */
@Data
@Entity
@Table(schema = "scop", name = "TB_LI_DAY_QUALITY")
public class TbLiDayQuality {

    @Id
    @Column(name = "smid")
    private int smid;

    @Column(name = "PRJ_ID")
    private String prjId;

    @Column(name = "ASSET_ID")
    private String assetId;

    @Column(name = "GPS_DATE")
    private String gpsDate;

    @Column(name = "WORK_GEO_FENCE_ID")
    private String workGeoFenceId;

    @Column(name = "STA_TRACKING_ID")
    private String staTrackingId;

    @Column(name = "END_TRACKING_ID")
    private String endTrackingId;

    @Column(name = "WORK_ARRIVAL_DT")
    private String workArrivalDt;

    @Column(name = "WORK_DEPARTURE_DT")
    private String workDepartureDt;

    @Column(name = "MIN")
    private float min;

    @Column(name = "MAX")
    private float max;

    @Column(name = "AVG")
    private float avg;

    @Column(name = "STD")
    private float std;

    @Column(name = "SUM")
    private float sum;

    @Column(name = "COMPACT_1")
    private float compact1;

    @Column(name = "COMPACT_2")
    private float compact2;

    @Column(name = "COMPACT_3")
    private float compact3;

    @Column(name = "COMPACT_4")
    private float compact4;

    @Column(name = "COMPACT_5")
    private float compact5;

    @Column(name = "COMPACT_6")
    private float compact6;

    @Column(name = "COMPACT_7")
    private float compact7;

    @Column(name = "COMPACT_8")
    private float compact8;
}
