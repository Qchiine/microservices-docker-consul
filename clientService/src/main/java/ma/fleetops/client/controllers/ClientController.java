package ma.fleetops.client.controllers;

import java.util.List;

import jakarta.validation.Valid;
import ma.fleetops.client.entities.Client;
import ma.fleetops.client.services.ClientService;
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
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping(value = "/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Client> findAll(@RequestParam(name = "q", required = false) String q) {
        return clientService.rechercher(q);
    }

    /** Deux chemins exposes : /client/{id} (enonce) et /clients/{id} (appel OpenFeign cote SERVICE-VOITURE). */
    @GetMapping(value = {"/client/{id}", "/clients/{id}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Client findById(@PathVariable("id") Long id) {
        return clientService.parId(id);
    }

    @PostMapping("/clients")
    public ResponseEntity<Client> save(@RequestBody @Valid Client client) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.creer(client));
    }

    @PutMapping({"/client/{id}", "/clients/{id}"})
    public Client update(@PathVariable("id") Long id, @RequestBody @Valid Client client) {
        return clientService.modifier(id, client);
    }

    @DeleteMapping({"/client/{id}", "/clients/{id}"})
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        clientService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
