package com.sph.hyu.batch.report.gamrir.service;

import com.sph.hyu.batch.report.gamrir.domain.*;
import com.sph.hyu.batch.report.gamrir.repository.SupervisionAssetInputRepository;
import com.sph.hyu.batch.report.gamrir.repository.SupervisionDHistRepository;
import com.sph.hyu.batch.report.gamrir.repository.SupervisionReportRepository;
import com.sph.hyu.batch.report.gamrir.repository.SupervisionWorkerInputRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 감리 리포트 - 장비/인력 투입 및 위험요소, 안전 항목 생성 서비스.
 * 리포트가 없으면 새로 생성하고, 있으면 하위 항목을 재생성(upsert)한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GamrirSupervisionReportService {

    private static final SimpleDateFormat EVENT_DT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private final SupervisionReportRepository supervisionReportRepository;
    private final SupervisionAssetInputRepository supervisionAssetInputRepository;
    private final SupervisionWorkerInputRepository supervisionWorkerInputRepository;
    private final SupervisionDHistRepository supervisionDHistRepository;

    /**
     * @param eventDate 리포트 생성 기준 일자 (yyyyMMdd). 통상 금일.
     */
    @Transactional
    public void generate(String eventDate) {
        List<Map<String, Object>> workDates = supervisionReportRepository.getWorkDates(eventDate);
        Date now = new Date();

        for (Map<String, Object> row : workDates) {
            UUID prjId = UUID.fromString(row.get("prj_id").toString());
            String workDate = row.get("work_date").toString();
            log.info("[감리 리포트] 생성일자={}, PRJ_ID={}, WORK_DATE={}", eventDate, prjId, workDate);

            TbRptMtSupervision report = supervisionReportRepository.findByPrjIdAndWorkDate(prjId, workDate)
                    .map(this::clearChildren)
                    .orElseGet(() -> newReport(prjId, workDate));

            report.setRegDt(now);
            fillAssetInputs(report, prjId, eventDate);
            fillWorkerInputs(report, prjId, eventDate);
            fillRiskFactors(report, prjId, eventDate);

            // 안전 - 공종/지시사항/조치사항 (빈 껍데기 생성)
            TbRptMtSupervisionSafety safety = new TbRptMtSupervisionSafety();
            safety.setReport(report);
            report.setSafety(safety);

            supervisionReportRepository.save(report);
            supervisionReportRepository.flush();
        }
    }

    /** 기존 리포트의 자식 컬렉션(장비/인력/위험요소)을 비운다. (orphanRemoval 로 삭제) */
    private TbRptMtSupervision clearChildren(TbRptMtSupervision report) {
        report.getAssetInputs().clear();
        report.getWorkerInputs().clear();
        report.getDHists().clear();
        supervisionReportRepository.save(report);
        supervisionReportRepository.flush();
        return report;
    }

    private TbRptMtSupervision newReport(UUID prjId, String workDate) {
        TbRptMtSupervision report = new TbRptMtSupervision();
        report.setPrjId(prjId);
        report.setWorkDate(workDate);
        return report;
    }

    private void fillAssetInputs(TbRptMtSupervision report, UUID prjId, String eventDate) {
        for (Map<String, Object> m : supervisionAssetInputRepository.getEquipementManagement(prjId, eventDate)) {
            TbRptMtSupervisionAssetInput input = new TbRptMtSupervisionAssetInput();
            input.setAssetType(m.get("asset_type").toString());
            input.setAssetId(UUID.fromString(m.get("asset_id").toString()));
            input.setAssetNm(m.get("asset_nm").toString());
            input.setAssetStandard(m.get("asset_standard").toString());
            input.setTodayCount(Integer.parseInt(m.get("today_count").toString()));
            input.setYesterdayCount(Integer.parseInt(m.get("yesterday_count").toString()));
            input.setReport(report);
            report.getAssetInputs().add(input);
        }
    }

    private void fillWorkerInputs(TbRptMtSupervision report, UUID prjId, String eventDate) {
        for (Map<String, Object> m : supervisionWorkerInputRepository.getWorkforceManagement(prjId, eventDate)) {
            TbRptMtSupervisionWorkerInput input = new TbRptMtSupervisionWorkerInput();
            input.setSort1(m.get("sort_1").toString());
            input.setSort2(m.get("sort_2").toString());
            input.setTodayCount(Integer.parseInt(m.get("today_count").toString()));
            input.setYesterdayCount(Integer.parseInt(m.get("yesterday_count").toString()));
            input.setReport(report);
            report.getWorkerInputs().add(input);
        }
    }

    private void fillRiskFactors(TbRptMtSupervision report, UUID prjId, String eventDate) {
        for (Map<String, Object> m : supervisionDHistRepository.getEquipmentRiskFactor(prjId, eventDate)) {
            TbRptMtSupervisionDHist dHist = new TbRptMtSupervisionDHist();
            dHist.setTrackingId(m.get("tracking_id").toString());
            dHist.setAssetId(UUID.fromString(m.get("asset_id").toString()));
            dHist.setAssetType(m.get("asset_type").toString());
            dHist.setUserId(m.get("user_id").toString());
            dHist.setD(Float.parseFloat(m.get("d").toString()));
            dHist.setEventDt(parseEventDt(m.get("event_dt").toString()));
            dHist.setReport(report);
            report.getDHists().add(dHist);
        }
    }

    private Date parseEventDt(String value) {
        try {
            return EVENT_DT_FORMAT.parse(value);
        } catch (ParseException e) {
            throw new IllegalStateException("event_dt 파싱 실패: " + value, e);
        }
    }
}
