package com.bestprogramer.bestprogramer.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "formations")
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String lieu;
    private Date dateDebut;
    private Date dateFin;
    private Integer prix;

    @OneToMany(mappedBy = "formation")
    private List<Inscription> inscriptions;
}
