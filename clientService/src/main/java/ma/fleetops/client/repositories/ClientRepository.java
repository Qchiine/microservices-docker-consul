package ma.fleetops.client.repositories;

import java.util.List;

import ma.fleetops.client.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByNomContainingIgnoreCase(String nom);
}
