package ma.fleetops.gateway.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

/**
 * Backend-for-frontend de la console FleetOps : la Gateway agrege les
 * reponses de plusieurs microservices en un seul payload.
 */
@Service
public class FleetAggregationService {

    private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTE =
            new ParameterizedTypeReference<>() { };

    private final WebClient.Builder loadBalancedWebClient;
    private final ObjectProvider<ReactiveDiscoveryClient> discoveryClientProvider;

    public FleetAggregationService(WebClient.Builder loadBalancedWebClient,
                                   ObjectProvider<ReactiveDiscoveryClient> discoveryClientProvider) {
        this.loadBalancedWebClient = loadBalancedWebClient;
        this.discoveryClientProvider = discoveryClientProvider;
    }

    public Mono<List<Map<String, Object>>> clients(String cid) {
        return lire("http://SERVICE-CLIENT/clients", LISTE, cid, List.of());
    }

    public Mono<List<Map<String, Object>>> voitures(String cid) {
        return lire("http://SERVICE-VOITURE/voitures?enrich=false", LISTE, cid, List.of());
    }

    public Mono<Map<String, Object>> statistiques(String cid) {
        return lire("http://SERVICE-VOITURE/voitures/statistiques",
                new ParameterizedTypeReference<Map<String, Object>>() { }, cid, Map.of("nombreVoitures", 0));
    }

    /** Instances reellement enregistrees dans Consul a cet instant. */
    public Mono<List<Map<String, Object>>> registre() {
        ReactiveDiscoveryClient discoveryClient = discoveryClientProvider.getIfAvailable();
        if (discoveryClient == null) {
            return Mono.just(List.of());
        }
        return discoveryClient.getServices()
                .flatMap(serviceId -> discoveryClient.getInstances(serviceId)
                        .map(instance -> ligne(serviceId, instance))
                        .collectList())
                .flatMapIterable(listes -> listes)
                .collectList();
    }

    private <T> Mono<T> lire(String uri, ParameterizedTypeReference<T> type, String cid, T valeurParDefaut) {
        return loadBalancedWebClient.build()
                .get()
                .uri(uri)
                .header("X-Correlation-Id", cid)
                .retrieve()
                .bodyToMono(type)
                .onErrorReturn(valeurParDefaut);
    }

    private Map<String, Object> ligne(String serviceId, ServiceInstance instance) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("service", serviceId);
        map.put("instanceId", instance.getInstanceId());
        map.put("uri", instance.getUri().toString());
        map.put("metadata", Optional.ofNullable(instance.getMetadata()).orElse(Map.of()));
        return map;
    }
}
