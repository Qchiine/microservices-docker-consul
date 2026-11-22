package ma.fleetops.client.exceptions;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ClientIntrouvableException.class)
    public ResponseEntity<ProblemDetail> clientIntrouvable(ClientIntrouvableException ex, HttpServletRequest request) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Client introuvable");
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setProperty("service", "SERVICE-CLIENT");
        detail.setProperty("correlationId", MDC.get("cid"));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> erreurs = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        erreur -> erreur.getField(),
                        erreur -> erreur.getDefaultMessage() == null ? "valeur invalide" : erreur.getDefaultMessage(),
                        (premier, second) -> premier));
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "La requete contient des champs invalides");
        detail.setTitle("Validation echouee");
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setProperty("service", "SERVICE-CLIENT");
        detail.setProperty("erreurs", erreurs);
        detail.setProperty("correlationId", MDC.get("cid"));
        return ResponseEntity.badRequest().body(detail);
    }
}
