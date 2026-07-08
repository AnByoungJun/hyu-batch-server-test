package com.sph.hyu.batch.productivity.domain.type;

/**
 * 생산성 전송 Queue 아이템 상태.
 */
public enum QueueStatus {
    QUEUED,
    TRANSFERRING,
    COMPLETED,
    FAILED
}
