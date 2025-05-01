package com.bestprogramer.bestprogramer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bestprogramer.bestprogramer.models.Message;
import com.bestprogramer.bestprogramer.models.User;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByDestinataireOrderByDateEnvoiDesc(User destinataire);
    List<Message> findByExpediteurOrderByDateEnvoiDesc(User expediteur);
    List<Message> findByDestinataireAndLuOrderByDateEnvoiDesc(User destinataire, boolean lu);
}
