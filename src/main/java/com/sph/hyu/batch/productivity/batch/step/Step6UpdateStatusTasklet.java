package com.sph.hyu.batch.productivity.batch.step;

import com.sph.hyu.batch.productivity.batch.ProductivityJobContext;
import com.sph.hyu.batch.productivity.domain.TbBatchProductivityJob;
import com.sph.hyu.batch.productivity.domain.TbBatchProductivityQueue;
import com.sph.hyu.batch.productivity.domain.type.JobStatus;
import com.sph.hyu.batch.productivity.domain.type.QueueStatus;
import com.sph.hyu.batch.productivity.repository.BatchProductivityJobRepository;
import com.sph.hyu.batch.productivity.repository.BatchProductivityQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/** Step 6 - 상태 반영 (성공/실패 건수 집계) */
@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class Step6UpdateStatusTasklet implements Tasklet {

    private final BatchProductivityQueueRepository queueRepository;
    private final BatchProductivityJobRepository jobRepository;
    private final ProductivityJobContext jobContext;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        TbBatchProductivityJob job = jobContext.getJob();
        log.info("[Step 6] 상태 반영: jobId={}", job.getJobId());

        List<TbBatchProductivityQueue> allItems = queueRepository.findByJobId(job.getJobId());
        int successCount = (int) allItems.stream().filter(i -> i.getStatus() == QueueStatus.COMPLETED).count();
        int failCount = (int) allItems.stream().filter(i -> i.getStatus() == QueueStatus.FAILED).count();

        job.setSuccessCount(successCount);
        job.setFailCount(failCount);
        job.setStatus(JobStatus.STATUS_UPDATED);
        jobRepository.save(job);

        log.info("[Step 6] 상태 반영 완료: 성공={}, 실패={}", successCount, failCount);
        return RepeatStatus.FINISHED;
    }
}
