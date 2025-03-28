package com.bestprogramer.bestprogramer.models;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String sexe;
    private String contact;
    @OneToMany(mappedBy = "users")
    private List<Inscription> inscriptions;
    
}