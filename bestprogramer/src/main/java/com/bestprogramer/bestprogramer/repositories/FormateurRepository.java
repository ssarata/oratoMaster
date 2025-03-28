package com.bestprogramer.bestprogramer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bestprogramer.bestprogramer.models.Formateur;

public interface FormateurRepository extends JpaRepository<Formateur, Long> {
}