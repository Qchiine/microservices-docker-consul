package ma.fleetops.voiture.clients;

import ma.fleetops.voiture.entities.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback declenche par le circuit breaker Resilience4j quand SERVICE-CLIENT
 * est indisponible : le parc reste consultable avec un client "fantome".
 */
@Component
public class ClientFeignFallback implements FallbackFactory<ClientFeign> {

    private static final Logger log = LoggerFactory.getLogger(ClientFeignFallback.class);

    @Override
    public ClientFeign create(Throwable cause) {
        return id -> {
            log.warn("SERVICE-CLIENT indisponible pour le client {} -> {}", id, cause.toString());
            return Client.builder()
                    .id(id)
                    .nom("SERVICE-CLIENT INDISPONIBLE")
                    .age(0f)
                    .email("n/a")
                    .build();
        };
    }
}
