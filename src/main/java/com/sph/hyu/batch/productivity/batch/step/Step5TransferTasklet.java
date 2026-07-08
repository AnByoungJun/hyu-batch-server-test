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

/** Step 5 - API 전송 */
@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class Step5TransferTasklet implements Tasklet {

    private final BatchProductivityQueueRepository queueRepository;
    private final BatchProductivityJobRepository jobRepository;
    private final ProductivityApiClient apiClient;
    private final ProductivityJobContext jobContext;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        TbBatchProductivityJob job = jobContext.getJob();
        log.info("[Step 5] API 전송: jobId={}", job.getJobId());

        job.setStatus(JobStatus.TRANSFERRING);
        jobRepository.save(job);

        List<TbBatchProductivityQueue> items = queueRepository.findByJobIdAndStatus(job.getJobId(), QueueStatus.QUEUED);
        log.info("[Step 5] 전송 대상: {}건", items.size());

        Date now = new Date();
        for (TbBatchProductivityQueue item : items) {
            boolean ok = apiClient.send(item);
            item.setStatus(ok ? QueueStatus.COMPLETED : QueueStatus.FAILED);
            item.setUpdDt(now);
        }
        queueRepository.saveAll(items);

        log.info("[Step 5] API 전송 완료");
        return RepeatStatus.FINISHED;
    }
}
