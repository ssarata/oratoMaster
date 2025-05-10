package com.bestprogramer.bestprogramer.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bestprogramer.bestprogramer.models.Formation;
import com.bestprogramer.bestprogramer.models.Inscription;
import com.bestprogramer.bestprogramer.models.User;
import com.bestprogramer.bestprogramer.repositories.InscriptionRepository;

@Service
public class InscriptionService {

    @Autowired
    private InscriptionRepository inscriptionRepository;

    public List<Inscription> getAllInscriptions() {
        return inscriptionRepository.findAll();
    }

    public Optional<Inscription> getInscriptionById(Long id) {
        return inscriptionRepository.findById(id);
    }

    public List<Inscription> getInscriptionsByApprenant(User apprenant) {
        return inscriptionRepository.findByApprenant(apprenant);
    }

    public List<Inscription> getInscriptionsByFormation(Formation formation) {
        return inscriptionRepository.findByFormation(formation);
    }

    public Inscription saveInscription(Inscription inscription) {
        return inscriptionRepository.save(inscription);
    }

    public void deleteInscription(Long id) {
        inscriptionRepository.deleteById(id);
    }

    public boolean isDejaInscrit(User apprenant, Formation formation) {
        return inscriptionRepository.existsByApprenantAndFormation(apprenant, formation);
    }
}
