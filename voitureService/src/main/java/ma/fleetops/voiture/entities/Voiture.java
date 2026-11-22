package ma.fleetops.voiture.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "voiture")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Voiture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La marque est obligatoire")
    private String marque;

    @NotBlank(message = "Le matricule est obligatoire")
    @Column(unique = true, nullable = false)
    private String matricule;

    private String modele;

    @Min(value = 1950, message = "Annee invalide")
    @Max(value = 2100, message = "Annee invalide")
    private Integer annee;

    @Min(value = 0, message = "Le kilometrage ne peut pas etre negatif")
    private Integer kilometrage;

    @Enumerated(EnumType.STRING)
    private Carburant carburant;

    @NotNull(message = "L'identifiant du client est obligatoire")
    @Column(name = "id_client", nullable = false)
    private Long idClient;

    /** Donnee jointre recuperee a la volee via OpenFeign : jamais persistee. */
    @Transient
    private Client client;
}
