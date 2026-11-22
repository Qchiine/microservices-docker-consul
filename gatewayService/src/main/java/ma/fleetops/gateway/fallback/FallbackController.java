package ma.fleetops.gateway.fallback;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/** Reponse de secours quand le circuit breaker est ouvert (service cible indisponible). */
@RestController
public class FallbackController {

    @GetMapping("/fallback/service-client")
    public Mono<ResponseEntity<Map<String, Object>>> client(@RequestHeader(name = "X-Correlation-Id", required = false) String cid) {
        return Mono.just(corps("SERVICE-CLIENT", cid));
    }

    @GetMapping("/fallback/service-voiture")
    public Mono<ResponseEntity<Map<String, Object>>> voiture(@RequestHeader(name = "X-Correlation-Id", required = false) String cid) {
        return Mono.just(corps("SERVICE-VOITURE", cid));
    }

    private ResponseEntity<Map<String, Object>> corps(String service, String cid) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("service", service);
        body.put("correlationId", cid);
        body.put("message", "Service momentanement indisponible : circuit ouvert cote Gateway.");
        body.put("action", "Reessayez dans quelques secondes.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}
