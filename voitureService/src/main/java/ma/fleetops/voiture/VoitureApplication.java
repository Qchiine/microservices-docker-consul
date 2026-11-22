package ma.fleetops.voiture;

import ma.fleetops.voiture.entities.Carburant;
import ma.fleetops.voiture.entities.Voiture;
import ma.fleetops.voiture.repositories.VoitureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class VoitureApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoitureApplication.class, args);
    }

    @Bean
    CommandLineRunner initialiserParc(VoitureRepository voitureRepository) {
        return args -> {
            if (voitureRepository.count() > 0) {
                return;
            }
            voitureRepository.save(Voiture.builder()
                    .marque("Toyota").matricule("A 25 333").modele("Corolla").annee(2021)
                    .kilometrage(48000).carburant(Carburant.HYBRIDE).idClient(1L).build());
            voitureRepository.save(Voiture.builder()
                    .marque("Renault").matricule("B 6 3456").modele("Megane").annee(2019)
                    .kilometrage(96500).carburant(Carburant.DIESEL).idClient(1L).build());
            voitureRepository.save(Voiture.builder()
                    .marque("Peugeot").matricule("A 55 4444").modele("301").annee(2022)
                    .kilometrage(23000).carburant(Carburant.ESSENCE).idClient(2L).build());
        };
    }
}
