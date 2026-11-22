package ma.fleetops.voiture.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class FeignCorrelationInterceptor {

    @Bean
    RequestInterceptor correlationIdRelay() {
        return template -> {
            String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
            if (correlationId != null) {
                template.header(CorrelationIdFilter.HEADER, correlationId);
            }
        };
    }
}
