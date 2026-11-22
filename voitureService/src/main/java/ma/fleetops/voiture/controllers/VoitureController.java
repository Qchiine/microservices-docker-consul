package ma.fleetops.voiture.controllers;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import ma.fleetops.voiture.entities.Voiture;
import ma.fleetops.voiture.services.VoitureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoitureController {

    private final VoitureService voitureService;

    public VoitureController(VoitureService voitureService) {
        this.voitureService = voitureService;
    }

    @GetMapping(value = "/voitures", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Voiture> findAll(@RequestParam(name = "enrich", defaultValue = "true") boolean enrich) {
        return voitureService.toutes(enrich);
    }

    @GetMapping(value = "/voitures/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Voiture findById(@PathVariable Long id) {
        return voitureService.parId(id, true);
    }

    @GetMapping(value = "/voitures/client/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Voiture> findByClient(@PathVariable Long id) {
        return voitureService.parClient(id);
    }

    @GetMapping(value = "/voitures/statistiques", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> statistiques() {
        return voitureService.statistiques();
    }

    @PostMapping("/voitures/{clientId}")
    public ResponseEntity<Voiture> save(@PathVariable Long clientId, @RequestBody @Valid Voiture voiture) {
        Voiture enregistree = voitureService.enregistrer(clientId, voiture);
        return ResponseEntity.status(HttpStatus.CREATED).body(enregistree);
    }

    @PutMapping("/voitures/{id}")
    public Voiture update(@PathVariable Long id, @RequestBody @Valid Voiture voiture) {
        return voitureService.modifier(id, voiture);
    }

    @DeleteMapping("/voitures/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        voitureService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
