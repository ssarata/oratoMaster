package com.bestprogramer.bestprogramer.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bestprogramer.bestprogramer.models.Question;
import com.bestprogramer.bestprogramer.models.Reponse;

 public interface ReponseRepository  extends JpaRepository<Reponse, Long>{
List<Reponse> findByQuestion(Question question);
    
}
