package com.bestprogramer.bestprogramer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bestprogramer.bestprogramer.models.CoursModel;


public interface CoursRepository extends JpaRepository<CoursModel, Long> {

}
