package com.sph.hyu.batch.productivity.batch.step;

import com.sph.hyu.batch.productivity.batch.ProductivityJobContext;
import com.sph.hyu.batch.productivity.domain.TbBatchProductivityJob;
import com.sph.hyu.batch.productivity.domain.TbBatchProductivityQueue;
import com.sph.hyu.batch.productivity.domain.type.JobStatus;
import com.sph.hyu.batch.productivity.domain.type.QueueStatus;
import com.sph.hyu.batch.productivity.dto.ProductivityRow;
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
import java.util.UUID;

/** Step 4 - Queue 적재 */
@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class Step4LoadQueueTasklet implements Tasklet {

    private final BatchProductivityQueueRepository queueRepository;
    private final BatchProductivityJobRepository jobRepository;
    private final ProductivityJobContext jobContext;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        TbBatchProductivityJob job = jobContext.getJob();
        List<ProductivityRow> results = jobContext.getAggregationResults();
        log.info("[Step 4] Queue 적재: jobId={}, 건수={}", job.getJobId(), results.size());

        queueRepository.deleteByJobId(job.getJobId());

        List<TbBatchProductivityQueue> items = results.stream()
                .map(row -> toQueueItem(job, row))
                .toList();

        queueRepository.saveAll(items);

        job.setStatus(JobStatus.QUEUED);
        jobRepository.save(job);

        log.info("[Step 4] Queue 적재 완료: {}건", items.size());
        return RepeatStatus.FINISHED;
    }

    private TbBatchProductivityQueue toQueueItem(TbBatchProductivityJob job, ProductivityRow row) {
        TbBatchProductivityQueue item = new TbBatchProductivityQueue();
        item.setJobId(job.getJobId());
        item.setPrjId(job.getPrjId());
        item.setWorkDate(job.getWorkDate());
        if (row.assetId() != null) {
            item.setAssetId(UUID.fromString(row.assetId()));
        }
        item.setAssetNm(row.assetNm());
        item.setAssetType(row.assetType());
        item.setAssetTypeNm(row.assetTypeNm());
        item.setWorkVolumePerHour(row.workVolumePerHour());
        item.setWorkVolumePerHourRate(row.workVolumePerHourRate());
        item.setStatus(QueueStatus.QUEUED);
        item.setRetryCount(0);
        return item;
    }
}
