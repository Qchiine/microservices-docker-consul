package ma.fleetops.voiture.exceptions;

public class VoitureIntrouvableException extends RuntimeException {

    public VoitureIntrouvableException(Long id) {
        super("Voiture introuvable avec l'identifiant " + id);
    }
}
