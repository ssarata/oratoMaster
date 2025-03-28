package com.bestprogramer.bestprogramer.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Data

@Table(name = "certificats")
public class Certificat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
}