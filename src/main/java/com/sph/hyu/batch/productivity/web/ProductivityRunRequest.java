package com.sph.hyu.batch.productivity.web;

import jakarta.validation.constraints.NotBlank;

/**
 * 생산성 Job 수동 실행 요청.
 *
 * @param prjId    프로젝트 ID (UUID 문자열)
 * @param workDate 집계 작업일자 (yyyyMMdd)
 */
public record ProductivityRunRequest(
        @NotBlank String prjId,
        @NotBlank String workDate
) {
}
