package com.sph.hyu.batch.productivity.service;

import com.sph.hyu.batch.productivity.domain.TbBatchProductivityQueue;
import com.sph.hyu.batch.productivity.dto.ProductivityApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 외부 API 전송 클라이언트. Queue 아이템 하나를 외부 API 로 POST 전송한다.
 */
@Slf4j
@Service
public class ProductivityApiClient {

    private final RestTemplate restTemplate;
    private final String apiUrl;

    public ProductivityApiClient(RestTemplate productivityRestTemplate,
                                 @Value("${api.productivity.url:}") String apiUrl) {
        this.restTemplate = productivityRestTemplate;
        this.apiUrl = apiUrl;
    }

    /**
     * @return 전송 성공 여부. URL 미설정 시 false 를 반환하며 전송을 건너뛴다.
     */
    public boolean send(TbBatchProductivityQueue item) {
        if (!StringUtils.hasText(apiUrl)) {
            log.warn("[API 전송] api.productivity.url 미설정. 전송 건너뜀: assetId={}", item.getAssetId());
            return false;
        }

        ProductivityApiRequest request = ProductivityApiRequest.from(item);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("[API 전송] 성공: assetId={}, status={}", item.getAssetId(), response.getStatusCode().value());
                return true;
            }
            log.warn("[API 전송] 실패 응답: assetId={}, status={}, body={}",
                    item.getAssetId(), response.getStatusCode().value(), response.getBody());
            return false;
        } catch (RestClientException e) {
            log.error("[API 전송] 오류: assetId={}, error={}", item.getAssetId(), e.getMessage());
            return false;
        }
    }
}
