package ma.fleetops.voiture.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import ma.fleetops.voiture.clients.ClientFeign;
import ma.fleetops.voiture.entities.Client;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientLookupService {

    private final ClientFeign clientFeign;

    @CircuitBreaker(name = "clientService", fallbackMethod = "clientSecours")
    public Client parId(Long id) {
        return clientFeign.clientById(id);
    }

    @SuppressWarnings("unused")
    private Client clientSecours(Long id, Throwable cause) {
        return Client.builder().id(id).nom("SERVICE-CLIENT INDISPONIBLE").age(0f).build();
    }
}
