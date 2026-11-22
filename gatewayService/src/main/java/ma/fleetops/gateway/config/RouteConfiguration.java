package ma.fleetops.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Routage par nom logique : "lb://NOM" resout l instance via Consul.
 */
@Configuration
public class RouteConfiguration {

    @Bean
    public RouteLocator fleetRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("clients-lb", r -> r.path("/clients/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("clientRoute")
                                .setFallbackUri("forward:/fallback/service-client")))
                        .uri("lb://SERVICE-CLIENT"))
                .route("client-detail-lb", r -> r.path("/client/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("clientRoute")
                                .setFallbackUri("forward:/fallback/service-client")))
                        .uri("lb://SERVICE-CLIENT"))
                .route("voitures-lb", r -> r.path("/voitures/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("voitureRoute")
                                .setFallbackUri("forward:/fallback/service-voiture")))
                        .uri("lb://SERVICE-VOITURE"))
                .build();
    }
}
