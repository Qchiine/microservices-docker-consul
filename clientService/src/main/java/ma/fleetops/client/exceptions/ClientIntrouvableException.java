package ma.fleetops.client.exceptions;

public class ClientIntrouvableException extends RuntimeException {

    public ClientIntrouvableException(Long id) {
        super("Client non trouve avec l'identifiant " + id);
    }
}
