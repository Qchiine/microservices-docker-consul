package ma.fleetops.gateway.filters;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Injecte un identifiant de correlation dans chaque requete, le propage aux
 * microservices (entete X-Correlation-Id) et le renvoie au navigateur.
 * Permet de rejouer une requete de bout en bout dans les logs.
 */
@Component
public class CorrelationIdGlobalFilter implements GlobalFilter, Ordered {

    public static final String HEADER = "X-Correlation-Id";
    private static final Logger log = LoggerFactory.getLogger(CorrelationIdGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(HEADER);
        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }
        final String cid = correlationId;
        long debut = System.currentTimeMillis();
        String chemin = exchange.getRequest().getPath().value();

        ServerWebExchange mutated = exchange.mutate()
                .request(builder -> builder.header(HEADER, cid))
                .build();
        mutated.getResponse().beforeCommit(() -> {
            // Ecrasement volontaire : le microservice renvoie deja l'entete, on evite un doublon.
            mutated.getResponse().getHeaders().set(HEADER, cid);
            return Mono.empty();
        });

        return chain.filter(mutated)
                .then(Mono.fromRunnable(() -> log.info("cid={} {} {} -> {} en {} ms",
                        cid,
                        exchange.getRequest().getMethod(),
                        chemin,
                        mutated.getResponse().getStatusCode(),
                        System.currentTimeMillis() - debut)));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
