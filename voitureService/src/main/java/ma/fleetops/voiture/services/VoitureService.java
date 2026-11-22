package ma.fleetops.voiture.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ma.fleetops.voiture.entities.Client;
import ma.fleetops.voiture.entities.Voiture;
import ma.fleetops.voiture.exceptions.VoitureIntrouvableException;
import ma.fleetops.voiture.repositories.VoitureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class VoitureService {

    private final VoitureRepository voitureRepository;
    private final ClientLookupService clientLookupService;

    public List<Voiture> toutes(boolean enrichir) {
        List<Voiture> voitures = voitureRepository.findAll();
        if (enrichir) {
            voitures.forEach(this::rattacherClient);
        }
        return voitures;
    }

    public Voiture parId(Long id, boolean enrichir) {
        Voiture voiture = voitureRepository.findById(id)
                .orElseThrow(() -> new VoitureIntrouvableException(id));
        if (enrichir) {
            rattacherClient(voiture);
        }
        return voiture;
    }

    public List<Voiture> parClient(Long idClient) {
        return voitureRepository.findByIdClient(idClient).stream()
                .peek(this::rattacherClient)
                .toList();
    }

    @Transactional
    public Voiture enregistrer(Long idClient, Voiture voiture) {
        voiture.setId(null);
        voiture.setIdClient(idClient);
        rattacherClient(voiture);
        return voitureRepository.save(voiture);
    }

    @Transactional
    public Voiture modifier(Long id, Voiture modification) {
        Voiture existante = parId(id, false);
        if (StringUtils.hasText(modification.getMarque())) {
            existante.setMarque(modification.getMarque());
        }
        if (StringUtils.hasText(modification.getMatricule())) {
            existante.setMatricule(modification.getMatricule());
        }
        if (StringUtils.hasText(modification.getModele())) {
            existante.setModele(modification.getModele());
        }
        if (modification.getAnnee() != null) {
            existante.setAnnee(modification.getAnnee());
        }
        if (modification.getKilometrage() != null) {
            existante.setKilometrage(modification.getKilometrage());
        }
        if (modification.getCarburant() != null) {
            existante.setCarburant(modification.getCarburant());
        }
        if (modification.getIdClient() != null) {
            existante.setIdClient(modification.getIdClient());
        }
        return voitureRepository.save(existante);
    }

    @Transactional
    public void supprimer(Long id) {
        voitureRepository.delete(parId(id, false));
    }

    /** Indicateurs agreges du parc (utilises par la console FleetOps). */
    public Map<String, Object> statistiques() {
        List<Voiture> voitures = voitureRepository.findAll();

        Map<String, Long> parMarque = voitures.stream()
                .collect(Collectors.groupingBy(Voiture::getMarque, TreeMap::new, Collectors.counting()));

        Map<String, Long> parCarburant = voitures.stream()
                .filter(v -> v.getCarburant() != null)
                .collect(Collectors.groupingBy(v -> v.getCarburant().name(),
                        () -> new TreeMap<>(), Collectors.counting()));

        double kilometrageMoyen = voitures.stream()
                .map(Voiture::getKilometrage)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0d);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("nombreVoitures", voitures.size());
        stats.put("kilometrageMoyen", Math.round(kilometrageMoyen));
        stats.put("repartitionParMarque", parMarque);
        stats.put("repartitionParCarburant", parCarburant);
        return stats;
    }

    private void rattacherClient(Voiture voiture) {
        if (voiture.getIdClient() != null) {
            Client client = clientLookupService.parId(voiture.getIdClient());
            voiture.setClient(client);
        }
    }
}
