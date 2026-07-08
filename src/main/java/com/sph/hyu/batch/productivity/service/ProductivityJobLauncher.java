package com.sph.hyu.batch.productivity.service;

import com.sph.hyu.batch.productivity.batch.ProductivityJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 생산성 전송 Job 실행 진입점. JobParameters 구성 로직을 한 곳에서 관리하여
 * 컨트롤러/스케줄러가 공통으로 사용한다.
 */
@Slf4j
@Service
public class ProductivityJobLauncher {

    private final JobLauncher jobLauncher;
    private final Job productivityTransferJob;

    public ProductivityJobLauncher(JobLauncher jobLauncher,
                                   @Qualifier(ProductivityJobConfig.JOB_NAME) Job productivityTransferJob) {
        this.jobLauncher = jobLauncher;
        this.productivityTransferJob = productivityTransferJob;
    }

    /**
     * Job 을 실행한다. (runTime 파라미터로 매 실행을 고유하게 만들어 재실행 가능)
     *
     * @throws Exception JobLauncher 실행 중 발생한 예외 (호출측에서 처리)
     */
    public void launch(String prjId, String workDate) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("prjId", prjId)
                .addString("workDate", workDate)
                .addLong("runTime", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(productivityTransferJob, params);
    }
}
