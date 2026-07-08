package com.sph.hyu.batch.report.gamrir.service;

import com.sph.hyu.batch.report.gamrir.domain.TbLiDayQuality;
import com.sph.hyu.batch.report.gamrir.domain.TbRptMtSupervision;
import com.sph.hyu.batch.report.gamrir.domain.TbRptMtSupervisionQuality;
import com.sph.hyu.batch.report.gamrir.domain.TbRptMtSupervisionQualityTestOption;
import com.sph.hyu.batch.report.gamrir.repository.QualityRepository;
import com.sph.hyu.batch.report.gamrir.repository.SupervisionQualityRepository;
import com.sph.hyu.batch.report.gamrir.repository.SupervisionQualityTestOptionRepository;
import com.sph.hyu.batch.report.gamrir.repository.SupervisionReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 감리 리포트 - 품질(다짐) 항목 생성 서비스.
 * 기존 감리 리포트가 존재할 때 품질 항목을 재생성한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GamrirQualityReportService {

    private final SupervisionReportRepository supervisionReportRepository;
    private final SupervisionQualityRepository supervisionQualityRepository;
    private final SupervisionQualityTestOptionRepository supervisionQualityTestOptionRepository;
    private final QualityRepository qualityRepository;

    /**
     * @param eventDate 조회 기준 일자 (yyyyMMdd). 통상 전일(D-1).
     */
    @Transactional
    public void generate(String eventDate) {
        List<Map<String, Object>> workDates = supervisionReportRepository.getWorkDates(eventDate);

        for (Map<String, Object> row : workDates) {
            String prjId = row.get("prj_id").toString();
            String workDay = row.get("work_date").toString();
            log.info("[품질 리포트] PRJ_ID={}, WORK_DATE={}", prjId, workDay);

            Optional<TbRptMtSupervision> reportOpt =
                    supervisionReportRepository.findByPrjIdAndWorkDate(UUID.fromString(prjId), workDay);
            if (reportOpt.isEmpty()) {
                continue;
            }

            TbRptMtSupervision report = reportOpt.get();
            log.info("[품질 리포트] 기존 Qualities 수: {}", report.getQualities().size());

            clearExistingQualities(report);
            appendQualities(report, qualityRepository.findByPrjIdAndGpsDate(prjId, workDay));

            supervisionReportRepository.save(report);
            supervisionReportRepository.flush();
        }
    }

    /** 기존 품질 항목 및 시험 옵션을 제거한다. */
    private void clearExistingQualities(TbRptMtSupervision report) {
        if (report.getQualities().isEmpty()) {
            return;
        }
        report.getQualities().forEach(quality -> {
            quality.getTestOptions().forEach(option -> {
                option.setQuality(null);
                supervisionQualityTestOptionRepository.delete(option);
            });
            quality.getTestOptions().clear();
            quality.setReport(null);
            supervisionQualityRepository.delete(quality);
        });
        report.getQualities().clear();
        supervisionReportRepository.save(report);
        supervisionReportRepository.flush();
    }

    /** 원천 다짐 데이터를 품질 항목으로 변환하여 리포트에 추가한다. */
    private void appendQualities(TbRptMtSupervision report, List<TbLiDayQuality> sources) {
        for (TbLiDayQuality source : sources) {
            TbRptMtSupervisionQuality quality = new TbRptMtSupervisionQuality();
            quality.setGeoFenceId(source.getWorkGeoFenceId());
            quality.setCompactionCnt(source.getAvg());
            quality.setReport(report);
            report.getQualities().add(quality);

            TbRptMtSupervisionQualityTestOption option = new TbRptMtSupervisionQualityTestOption();
            option.setSelected(true);
            option.setCompactionCnt(1);
            option.setQuality(quality);
            quality.getTestOptions().add(option);
        }
    }
}
