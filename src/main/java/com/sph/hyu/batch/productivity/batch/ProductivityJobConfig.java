package com.sph.hyu.batch.productivity.batch;

import com.sph.hyu.batch.productivity.batch.step.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 일 집계 데이터 전송 Spring Batch Job 설정 (Spring Batch 5 API).
 *
 * <pre>
 * Job: productivityTransferJob
 *   Step 1. createJob      - Job 생성
 *   Step 2. aggregate      - 집계 수행
 *   Step 3. validate       - 검증 (NO_DATA → Step8 로 분기)
 *   Step 4. loadQueue      - Queue 적재
 *   Step 5. transfer       - API 전송
 *   Step 6. updateStatus   - 상태 반영
 *   Step 7. retry          - 재시도 (실패분)
 *   Step 8. completeJob    - Job 완료
 * </pre>
 */
@Configuration
public class ProductivityJobConfig {

    public static final String JOB_NAME = "productivityTransferJob";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final Step1CreateJobTasklet step1CreateJobTasklet;
    private final Step2AggregateTasklet step2AggregateTasklet;
    private final Step3ValidateTasklet step3ValidateTasklet;
    private final Step4LoadQueueTasklet step4LoadQueueTasklet;
    private final Step5TransferTasklet step5TransferTasklet;
    private final Step6UpdateStatusTasklet step6UpdateStatusTasklet;
    private final Step7RetryTasklet step7RetryTasklet;
    private final Step8CompleteJobTasklet step8CompleteJobTasklet;

    public ProductivityJobConfig(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 Step1CreateJobTasklet step1CreateJobTasklet,
                                 Step2AggregateTasklet step2AggregateTasklet,
                                 Step3ValidateTasklet step3ValidateTasklet,
                                 Step4LoadQueueTasklet step4LoadQueueTasklet,
                                 Step5TransferTasklet step5TransferTasklet,
                                 Step6UpdateStatusTasklet step6UpdateStatusTasklet,
                                 Step7RetryTasklet step7RetryTasklet,
                                 Step8CompleteJobTasklet step8CompleteJobTasklet) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.step1CreateJobTasklet = step1CreateJobTasklet;
        this.step2AggregateTasklet = step2AggregateTasklet;
        this.step3ValidateTasklet = step3ValidateTasklet;
        this.step4LoadQueueTasklet = step4LoadQueueTasklet;
        this.step5TransferTasklet = step5TransferTasklet;
        this.step6UpdateStatusTasklet = step6UpdateStatusTasklet;
        this.step7RetryTasklet = step7RetryTasklet;
        this.step8CompleteJobTasklet = step8CompleteJobTasklet;
    }

    @Bean
    public Job productivityTransferJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(createJobStep())
                    .on("*").to(aggregateStep())
                .from(aggregateStep())
                    .on("*").to(validateStep())
                .from(validateStep())
                    .on("NO_DATA").to(completeJobStep())    // 집계 없음 → Step8 바로 이동
                .from(validateStep())
                    .on("*").to(loadQueueStep())            // 정상 → Step4~8 순차 실행
                        .next(transferStep())
                        .next(updateStatusStep())
                        .next(retryStep())
                        .next(completeJobStep())
                .end()
                .build();
    }

    @Bean
    public Step createJobStep() {
        return new StepBuilder("createJob", jobRepository)
                .tasklet(step1CreateJobTasklet, transactionManager).build();
    }

    @Bean
    public Step aggregateStep() {
        return new StepBuilder("aggregate", jobRepository)
                .tasklet(step2AggregateTasklet, transactionManager).build();
    }

    @Bean
    public Step validateStep() {
        return new StepBuilder("validate", jobRepository)
                .tasklet(step3ValidateTasklet, transactionManager).build();
    }

    @Bean
    public Step loadQueueStep() {
        return new StepBuilder("loadQueue", jobRepository)
                .tasklet(step4LoadQueueTasklet, transactionManager).build();
    }

    @Bean
    public Step transferStep() {
        return new StepBuilder("transfer", jobRepository)
                .tasklet(step5TransferTasklet, transactionManager).build();
    }

    @Bean
    public Step updateStatusStep() {
        return new StepBuilder("updateStatus", jobRepository)
                .tasklet(step6UpdateStatusTasklet, transactionManager).build();
    }

    @Bean
    public Step retryStep() {
        return new StepBuilder("retry", jobRepository)
                .tasklet(step7RetryTasklet, transactionManager).build();
    }

    @Bean
    public Step completeJobStep() {
        return new StepBuilder("completeJob", jobRepository)
                .tasklet(step8CompleteJobTasklet, transactionManager).build();
    }
}
