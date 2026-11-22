package ma.fleetops.voiture.clients;

import ma.fleetops.voiture.entities.Client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Nom logique resolu par Consul (plus aucune adresse en dur). */
@FeignClient(name = "SERVICE-CLIENT", fallbackFactory = ClientFeignFallback.class)
public interface ClientFeign {

    @GetMapping("/client/{id}")
    Client clientById(@PathVariable("id") Long id);
}
