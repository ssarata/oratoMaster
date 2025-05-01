package com.bestprogramer.bestprogramer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bestprogramer.bestprogramer.models.Cours;
import com.bestprogramer.bestprogramer.models.Module;

import java.util.List;

@Repository
public interface CoursRepository extends JpaRepository<Cours, Long> {
    List<Cours> findByModuleOrderByOrdreAsc(Module module);
}
