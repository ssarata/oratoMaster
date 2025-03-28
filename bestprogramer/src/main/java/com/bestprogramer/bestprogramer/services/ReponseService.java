package com.bestprogramer.bestprogramer.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bestprogramer.bestprogramer.models.Reponse;
import com.bestprogramer.bestprogramer.repositories.ReponseRepository;

@Service
public class ReponseService {
    @Autowired
    private ReponseRepository reponseRepository;

    public List<Reponse> getReponsesByQuestion(Long questionId) {
        return reponseRepository.findCorrectAnswersByQuestionId(questionId);
    }
}

