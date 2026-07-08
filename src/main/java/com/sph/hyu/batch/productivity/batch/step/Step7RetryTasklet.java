package com.sph.hyu.batch.productivity.batch.step;

import com.sph.hyu.batch.productivity.batch.ProductivityJobContext;
import com.sph.hyu.batch.productivity.domain.TbBatchProductivityJob;
import com.sph.hyu.batch.productivity.domain.TbBatchProductivityQueue;
import com.sph.hyu.batch.productivity.domain.type.JobStatus;
import com.sph.hyu.batch.productivity.domain.type.QueueStatus;
import com.sph.hyu.batch.productivity.repository.BatchProductivityJobRepository;
import com.sph.hyu.batch.productivity.repository.BatchProductivityQueueRepository;
import com.sph.hyu.batch.productivity.service.ProductivityApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/** Step 7 - 재시도 (실패분, 최대 {@value #MAX_RETRY} 회) */
@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class Step7RetryTasklet implements Tasklet {

    private static final int MAX_RETRY = 3;

    private final BatchProductivityQueueRepository queueRepository;
    private final BatchProductivityJobRepository jobRepository;
    private final ProductivityApiClient apiClient;
    private final ProductivityJobContext jobContext;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        TbBatchProductivityJob job = jobContext.getJob();

        List<TbBatchProductivityQueue> failedItems =
                queueRepository.findByJobIdAndStatus(job.getJobId(), QueueStatus.FAILED);

        if (failedItems.isEmpty()) {
            log.info("[Step 7] 재시도 대상 없음");
            return RepeatStatus.FINISHED;
        }

        log.info("[Step 7] 재시도: jobId={}, 실패건수={}", job.getJobId(), failedItems.size());
        job.setStatus(JobStatus.RETRYING);
        jobRepository.save(job);

        Date now = new Date();
        for (TbBatchProductivityQueue item : failedItems) {
            if (item.getRetryCount() >= MAX_RETRY) {
                log.warn("[Step 7] 최대 재시도({}) 초과: assetId={}", MAX_RETRY, item.getAssetId());
                continue;
            }

            item.setRetryCount(item.getRetryCount() + 1);
            boolean ok = apiClient.send(item);
            item.setStatus(ok ? QueueStatus.COMPLETED : QueueStatus.FAILED);
            item.setUpdDt(now);

            if (ok) {
                job.setSuccessCount(job.getSuccessCount() + 1);
                job.setFailCount(Math.max(0, job.getFailCount() - 1));
            }
        }

        queueRepository.saveAll(failedItems);
        jobRepository.save(job);
        log.info("[Step 7] 재시도 완료");
        return RepeatStatus.FINISHED;
    }
}
