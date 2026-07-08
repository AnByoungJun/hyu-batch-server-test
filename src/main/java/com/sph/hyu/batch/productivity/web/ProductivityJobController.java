package com.sph.hyu.batch.productivity.web;

import com.sph.hyu.batch.productivity.service.ProductivityJobLauncher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 생산성 집계 Job 수동 실행 API.
 * {@code POST /batch/productivity/run}
 */
@Slf4j
@RestController
@RequestMapping("/productivity")
@RequiredArgsConstructor
public class ProductivityJobController {

    private final ProductivityJobLauncher jobLauncher;

    @PostMapping("/run")
    public ResponseEntity<String> run(@Valid @RequestBody ProductivityRunRequest request) {
        log.info("[수동 실행] prjId={}, workDate={}", request.prjId(), request.workDate());
        try {
            jobLauncher.launch(request.prjId(), request.workDate());
            return ResponseEntity.ok("실행 완료");
        } catch (Exception e) {
            log.error("[수동 실행] 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("실행 실패: " + e.getMessage());
        }
    }
}
