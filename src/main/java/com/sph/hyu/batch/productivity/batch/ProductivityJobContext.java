package com.sph.hyu.batch.productivity.batch;

import com.sph.hyu.batch.productivity.domain.TbBatchProductivityJob;
import com.sph.hyu.batch.productivity.dto.ProductivityRow;
import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Job 실행 단위로 생성되는 스텝 간 공유 컨텍스트.
 */
@Getter
@Setter
@Component
@JobScope
public class ProductivityJobContext {

    private TbBatchProductivityJob job;
    private List<ProductivityRow> aggregationResults = new ArrayList<>();
}
