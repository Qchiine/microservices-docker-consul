package ma.fleetops.client.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "client")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du client est obligatoire")
    @Size(max = 80, message = "Le nom ne peut pas depasser 80 caracteres")
    @Column(nullable = false, length = 80)
    private String nom;

    @NotNull(message = "L'age est obligatoire")
    @Min(value = 16, message = "Un client doit avoir au moins 16 ans")
    @Max(value = 120, message = "Age invalide")
    @Column(nullable = false)
    private Float age;

    @Email(message = "Adresse email invalide")
    @Column(length = 120)
    private String email;

    @Column(name = "date_adhesion")
    private LocalDate dateAdhesion;
}
