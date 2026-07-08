package com.sph.hyu.batch.report.gamrir.repository;

import com.sph.hyu.batch.report.gamrir.domain.TbLiDayQuality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QualityRepository extends JpaRepository<TbLiDayQuality, Integer> {

    List<TbLiDayQuality> findByPrjIdAndGpsDate(String prjId, String gpsDate);
}
