package ma.fleetops.client;

import java.time.LocalDate;
import java.util.List;

import ma.fleetops.client.entities.Client;
import ma.fleetops.client.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    CommandLineRunner initialiserBase(ClientRepository clientRepository) {
        return args -> {
            if (clientRepository.count() > 0) {
                return;
            }
            clientRepository.saveAll(List.of(
                    Client.builder().nom("Rabab SELIMANI").age(23f)
                            .email("rabab.selimani@fleetops.ma").dateAdhesion(LocalDate.of(2023, 3, 14)).build(),
                    Client.builder().nom("Amal RAMI").age(22f)
                            .email("amal.rami@fleetops.ma").dateAdhesion(LocalDate.of(2023, 9, 2)).build(),
                    Client.builder().nom("Samir SAFI").age(22f)
                            .email("samir.safi@fleetops.ma").dateAdhesion(LocalDate.of(2024, 1, 21)).build(),
                    Client.builder().nom("Nadia EL FASSI").age(31f)
                            .email("nadia.elfassi@fleetops.ma").dateAdhesion(LocalDate.of(2022, 6, 8)).build(),
                    Client.builder().nom("Youssef BENALI").age(27f)
                            .email("youssef.benali@fleetops.ma").dateAdhesion(LocalDate.of(2024, 4, 30)).build()));
        };
    }
}
