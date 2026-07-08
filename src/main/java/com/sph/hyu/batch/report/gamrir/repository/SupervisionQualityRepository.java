package com.sph.hyu.batch.report.gamrir.repository;

import com.sph.hyu.batch.report.gamrir.domain.TbRptMtSupervisionQuality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupervisionQualityRepository extends JpaRepository<TbRptMtSupervisionQuality, Integer> {

    Optional<TbRptMtSupervisionQuality> findByGeoFenceId(String geoFenceId);

    void deleteByReportSeq(int reportSeq);
}
