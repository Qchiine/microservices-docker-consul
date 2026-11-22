package ma.fleetops.gateway.controllers;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 * Diagnostic "conteneurise" : expose ce que l'on verifie habituellement a la main
 * (docker compose ps, consul catalog, resolution DNS interne) sous forme d'API,
 * lisible depuis un navigateur.
 */
@RestController
@RequestMapping("/api/diagnostic")
public class DiagnosticController {

    private final DiscoveryClient discoveryClient;
    private final String consulHost;
    private final String consulPort;
    private final String applicationName;

    public DiagnosticController(DiscoveryClient discoveryClient,
                                @Value("${spring.cloud.consul.host:localhost}") String consulHost,
                                @Value("${spring.cloud.consul.port:8500}") String consulPort,
                                @Value("${spring.application.name:unknown}") String applicationName) {
        this.discoveryClient = discoveryClient;
        this.consulHost = consulHost;
        this.consulPort = consulPort;
        this.applicationName = applicationName;
    }

    @GetMapping("/container")
    public Mono<Map<String, Object>> container() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("service", applicationName);
        body.put("consulHost", consulHost + ":" + consulPort);
        try {
            body.put("containerHostname", InetAddress.getLocalHost().getHostName());
            body.put("containerIp", InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            body.put("containerHostname", "indisponible");
            body.put("containerIp", "indisponible");
        }
        body.put("javaVersion", System.getProperty("java.version"));
        body.put("uptimeSeconds", Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()).toSeconds());
        body.put("generatedAt", OffsetDateTime.now().toString());
        return Mono.just(body);
    }

    @GetMapping("/stack")
    public Mono<Map<String, Object>> stack() {
        Set<String> services = new TreeSet<>(discoveryClient.getServices());
        List<Map<String, Object>> instances = new ArrayList<>();
        List<String> problems = new ArrayList<>();

        for (String id : services) {
            for (ServiceInstance instance : discoveryClient.getInstances(id)) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("service", id);
                row.put("instanceId", instance.getInstanceId());
                row.put("host", instance.getHost());
                row.put("port", instance.getPort());
                row.put("uri", instance.getUri().toString());
                instances.add(row);
                if (instance.getHost() == null || instance.getHost().isBlank()) {
                    problems.add("Instance " + id + " sans adresse");
                }
            }
        }

        // Controles de coherence propres a une execution conteneurisee
        if (!services.contains("SERVICE-CLIENT")) {
            problems.add("SERVICE-CLIENT absent du registre Consul");
        }
        if (!services.contains("SERVICE-VOITURE")) {
            problems.add("SERVICE-VOITURE absent du registre Consul");
        }
        boolean localhostEnDur = instances.stream()
                .anyMatch(i -> "localhost".equals(i.get("host")));
        if (localhostEnDur) {
            problems.add("Une instance est enregistree sur 'localhost' : injoignable depuis un autre conteneur");
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("provider", "CONSUL");
        body.put("serviceCount", services.size());
        body.put("instanceCount", instances.size());
        body.put("services", services);
        body.put("instances", instances);
        body.put("problems", problems);
        body.put("dockerReady", problems.isEmpty());
        body.put("generatedAt", OffsetDateTime.now().toString());
        return Mono.just(body);
    }
}
