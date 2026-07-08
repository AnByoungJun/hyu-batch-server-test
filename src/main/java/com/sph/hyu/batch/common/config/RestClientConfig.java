package com.sph.hyu.batch.common.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 외부 API 호출용 {@link RestTemplate} 설정. 타임아웃을 중앙에서 관리한다.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate productivityRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }
}
