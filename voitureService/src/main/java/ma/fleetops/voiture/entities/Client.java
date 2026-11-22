package ma.fleetops.voiture.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Representation locale (POJO) du client expose par SERVICE-CLIENT, utilisee par OpenFeign. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client {

    private Long id;
    private String nom;
    private Float age;
    private String email;
}
