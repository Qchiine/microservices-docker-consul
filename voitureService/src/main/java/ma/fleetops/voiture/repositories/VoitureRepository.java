package ma.fleetops.voiture.repositories;

import java.util.List;

import ma.fleetops.voiture.entities.Voiture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoitureRepository extends JpaRepository<Voiture, Long> {

    List<Voiture> findByIdClient(Long idClient);

    long countByIdClient(Long idClient);
}
