package com.sph.hyu.batch.productivity.batch.step;

import com.sph.hyu.batch.productivity.batch.ProductivityJobContext;
import com.sph.hyu.batch.productivity.domain.TbBatchProductivityJob;
import com.sph.hyu.batch.productivity.domain.type.JobStatus;
import com.sph.hyu.batch.productivity.dto.ProductivityRow;
import com.sph.hyu.batch.productivity.repository.BatchProductivityJobRepository;
import com.sph.hyu.batch.productivity.service.ProductivityAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/** Step 2 - 집계 수행 */
@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class Step2AggregateTasklet implements Tasklet {

    private final ProductivityAggregationService aggregationService;
    private final BatchProductivityJobRepository jobRepository;
    private final ProductivityJobContext jobContext;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        TbBatchProductivityJob job = jobContext.getJob();
        log.info("[Step 2] 집계 수행: jobId={}", job.getJobId());

        job.setStatus(JobStatus.AGGREGATING);
        jobRepository.save(job);

        List<ProductivityRow> results = aggregationService.aggregate(job.getPrjId(), job.getWorkDate());
        jobContext.setAggregationResults(results);

        log.info("[Step 2] 집계 완료: {}건", results.size());
        return RepeatStatus.FINISHED;
    }
}
