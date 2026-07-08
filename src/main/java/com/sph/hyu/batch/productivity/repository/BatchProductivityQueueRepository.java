package com.sph.hyu.batch.productivity.repository;

import com.sph.hyu.batch.productivity.domain.TbBatchProductivityQueue;
import com.sph.hyu.batch.productivity.domain.type.QueueStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BatchProductivityQueueRepository extends JpaRepository<TbBatchProductivityQueue, Integer> {

    List<TbBatchProductivityQueue> findByJobId(UUID jobId);

    List<TbBatchProductivityQueue> findByJobIdAndStatus(UUID jobId, QueueStatus status);

    @Modifying
    void deleteByJobId(UUID jobId);
}
