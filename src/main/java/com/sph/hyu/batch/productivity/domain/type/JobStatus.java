package com.sph.hyu.batch.productivity.domain.type;

/**
 * 생산성 배치 Job 진행 상태.
 * (기존 문자열 상수를 enum 으로 승격 — 오타 방지 및 상태 전이 추적 용이)
 */
public enum JobStatus {
    CREATED,
    AGGREGATING,
    VALIDATED,
    NO_DATA,
    QUEUED,
    TRANSFERRING,
    STATUS_UPDATED,
    RETRYING,
    COMPLETED,
    FAILED
}
