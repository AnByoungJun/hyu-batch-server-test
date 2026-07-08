package com.sph.hyu.batch.productivity.batch.step;

import com.sph.hyu.batch.productivity.batch.ProductivityJobContext;
import com.sph.hyu.batch.productivity.domain.TbBatchProductivityJob;
import com.sph.hyu.batch.productivity.domain.type.JobStatus;
import com.sph.hyu.batch.productivity.dto.ProductivityRow;
import com.sph.hyu.batch.productivity.repository.BatchProductivityJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/** Step 3 - 검증. 집계 결과가 없으면 ExitStatus "NO_DATA" 로 분기시킨다. */
@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class Step3ValidateTasklet implements Tasklet {

    private final BatchProductivityJobRepository jobRepository;
    private final ProductivityJobContext jobContext;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        TbBatchProductivityJob job = jobContext.getJob();
        List<ProductivityRow> results = jobContext.getAggregationResults();
        log.info("[Step 3] 검증: jobId={}, 집계건수={}", job.getJobId(), results.size());

        if (results.isEmpty()) {
            log.info("[Step 3] 집계 결과 없음 → NO_DATA");
            job.setStatus(JobStatus.NO_DATA);
            jobRepository.save(job);
            contribution.setExitStatus(new ExitStatus("NO_DATA"));
            return RepeatStatus.FINISHED;
        }

        long nullVolumeCount = results.stream()
                .filter(r -> r.workVolumePerHour() == null)
                .count();
        if (nullVolumeCount > 0) {
            log.warn("[Step 3] work_volume_per_hour 가 null 인 데이터 {}건 (계속 진행)", nullVolumeCount);
        }

        job.setStatus(JobStatus.VALIDATED);
        job.setTotalCount(results.size());
        jobRepository.save(job);

        log.info("[Step 3] 검증 통과: 총 {}건", results.size());
        return RepeatStatus.FINISHED;
    }
}
