package com.sph.hyu.batch.report.gamrir.repository;

import com.sph.hyu.batch.report.gamrir.domain.TbRptMtSupervisionQualityTestOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupervisionQualityTestOptionRepository extends JpaRepository<TbRptMtSupervisionQualityTestOption, Integer> {

    void deleteByQualitySeq(int qualitySeq);
}
