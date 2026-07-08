package com.sph.hyu.batch.productivity.repository;

import com.sph.hyu.batch.productivity.domain.TbBatchProductivityJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchProductivityJobRepository extends JpaRepository<TbBatchProductivityJob, Integer> {

    Optional<TbBatchProductivityJob> findByJobId(UUID jobId);

    List<TbBatchProductivityJob> findByPrjIdAndWorkDate(UUID prjId, String workDate);
}
