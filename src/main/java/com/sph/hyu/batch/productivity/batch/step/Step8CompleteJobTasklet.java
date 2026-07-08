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
import org.springframework.stereotype.Component;

import java.util.Date;

/** Step 8 - Job 완료 */
@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class Step8CompleteJobTasklet implements Tasklet {

    private final BatchProductivityJobRepository jobRepository;
    private final ProductivityJobContext jobContext;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        TbBatchProductivityJob job = jobContext.getJob();
        log.info("[Step 8] Job 완료: jobId={}", job.getJobId());

        JobStatus finalStatus = (job.getStatus() == JobStatus.NO_DATA) ? JobStatus.NO_DATA : JobStatus.COMPLETED;
        job.setStatus(finalStatus);
        job.setEndDt(new Date());
        jobRepository.save(job);

        log.info("[Step 8] prjId={}, workDate={}, 상태={}, 총={}건, 성공={}건, 실패={}건",
                job.getPrjId(), job.getWorkDate(), finalStatus,
                job.getTotalCount(), job.getSuccessCount(), job.getFailCount());
        return RepeatStatus.FINISHED;
    }
}
