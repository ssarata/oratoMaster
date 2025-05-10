package com.bestprogramer.bestprogramer.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bestprogramer.bestprogramer.models.Formation;
import com.bestprogramer.bestprogramer.models.User;
import com.bestprogramer.bestprogramer.repositories.FormationRepository;

@Service
public class FormationService {

    @Autowired
    private FormationRepository formationRepository;

    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }

    public List<Formation> getFormationsAVenir() {
        return formationRepository.findByDateDebutAfterOrderByDateDebutAsc(LocalDate.now());
    }

    public List<Formation> getFormationsByFormateur(User formateur) {
        return formationRepository.findByFormateur(formateur);
    }

    public Optional<Formation> getFormationById(Long id) {
        return formationRepository.findById(id);
    }

    public Formation saveFormation(Formation formation) {
        return formationRepository.save(formation);
    }

    public void deleteFormation(Long id) {
        formationRepository.deleteById(id);
    }
}
