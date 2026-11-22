package ma.fleetops.gateway.controllers;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import ma.fleetops.gateway.services.FleetAggregationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/** API consommee par la console FleetOps servie par la Gateway. */
@RestController
@RequestMapping("/api/fleet")
public class FleetConsoleController {

    private final FleetAggregationService aggregationService;

    public FleetConsoleController(FleetAggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @GetMapping("/overview")
    public Mono<Map<String, Object>> overview(
            @RequestHeader(name = "X-Correlation-Id", required = false) String correlationId) {

        String cid = correlationId == null ? "console" : correlationId;
        return Mono.zip(
                        aggregationService.clients(cid),
                        aggregationService.voitures(cid),
                        aggregationService.statistiques(cid),
                        aggregationService.registre())
                .map(tuple -> {
                    Map<String, Object> reponse = new LinkedHashMap<>();
                    reponse.put("genereLe", OffsetDateTime.now().toString());
                    reponse.put("correlationId", cid);
                    reponse.put("registre", tuple.getT4());
                    reponse.put("clients", tuple.getT1());
                    reponse.put("voitures", tuple.getT2());
                    reponse.put("statistiques", tuple.getT3());
                    return reponse;
                });
    }

    @GetMapping("/ping")
    public Mono<Map<String, Object>> ping() {
        Map<String, Object> pong = new LinkedHashMap<>();
        pong.put("gateway", "UP");
        pong.put("timestamp", OffsetDateTime.now().toString());
        return Mono.just(pong);
    }
}
