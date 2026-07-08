package com.sph.hyu.batch.productivity.batch.step;

import com.sph.hyu.batch.productivity.batch.ProductivityJobContext;
import com.sph.hyu.batch.productivity.domain.TbBatchProductivityJob;
import com.sph.hyu.batch.productivity.domain.type.JobStatus;
import com.sph.hyu.batch.productivity.repository.BatchProductivityJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/** Step 1 - Job 생성 */
@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class Step1CreateJobTasklet implements Tasklet {

    private final BatchProductivityJobRepository jobRepository;
    private final ProductivityJobContext jobContext;

    @Value("#{jobParameters['prjId']}")
    private String prjId;

    @Value("#{jobParameters['workDate']}")
    private String workDate;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("[Step 1] Job 생성: prjId={}, workDate={}", prjId, workDate);

        TbBatchProductivityJob job = new TbBatchProductivityJob();
        job.setJobId(UUID.randomUUID());
        job.setPrjId(UUID.fromString(prjId));
        job.setWorkDate(workDate);
        job.setStatus(JobStatus.CREATED);
        job.setTotalCount(0);
        job.setSuccessCount(0);
        job.setFailCount(0);
        job.setStartDt(new Date());

        TbBatchProductivityJob saved = jobRepository.save(job);
        jobContext.setJob(saved);

        log.info("[Step 1] Job 생성 완료: jobId={}", saved.getJobId());
        return RepeatStatus.FINISHED;
    }
}
