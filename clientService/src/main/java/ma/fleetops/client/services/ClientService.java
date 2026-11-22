package ma.fleetops.client.services;

import java.util.List;

import lombok.RequiredArgsConstructor;
import ma.fleetops.client.entities.Client;
import ma.fleetops.client.exceptions.ClientIntrouvableException;
import ma.fleetops.client.repositories.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public List<Client> rechercher(String q) {
        if (!StringUtils.hasText(q)) {
            return clientRepository.findAll();
        }
        return clientRepository.findByNomContainingIgnoreCase(q.trim());
    }

    @Transactional(readOnly = true)
    public Client parId(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new ClientIntrouvableException(id));
    }

    @Transactional
    public Client creer(Client client) {
        client.setId(null);
        return clientRepository.save(client);
    }

    @Transactional
    public Client modifier(Long id, Client modification) {
        Client existant = parId(id);
        if (StringUtils.hasText(modification.getNom())) {
            existant.setNom(modification.getNom());
        }
        if (modification.getAge() != null) {
            existant.setAge(modification.getAge());
        }
        if (StringUtils.hasText(modification.getEmail())) {
            existant.setEmail(modification.getEmail());
        }
        if (modification.getDateAdhesion() != null) {
            existant.setDateAdhesion(modification.getDateAdhesion());
        }
        return clientRepository.save(existant);
    }

    @Transactional
    public void supprimer(Long id) {
        clientRepository.delete(parId(id));
    }
}
