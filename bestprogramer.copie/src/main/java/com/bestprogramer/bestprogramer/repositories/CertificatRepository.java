package com.bestprogramer.bestprogramer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bestprogramer.bestprogramer.models.Certificat;
import com.bestprogramer.bestprogramer.models.User;

import java.util.List;

@Repository
public interface CertificatRepository extends JpaRepository<Certificat, Long> {
    List<Certificat> findByUser(User user);
    boolean existsByUser(User user);
}
