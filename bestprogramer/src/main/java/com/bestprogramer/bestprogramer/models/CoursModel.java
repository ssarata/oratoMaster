package com.bestprogramer.bestprogramer.models;

import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
@Entity
@Table(name="cours")

public class CoursModel{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String titre;
	@Lob
	@Basic(fetch = FetchType.EAGER)

    @Column
    private String description;
	
	@OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizzes;

   
	
	public List<Quiz> getQuizzes() {
		return quizzes;
	}
	public void setQuizzes(List<Quiz> quizzes) {
		this.quizzes = quizzes;
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	
	public String getDescription() {
		return description;
	}
	
	public  void setDescription(String description) {
		this.description = description;
	}
	
	

	public void setId(Long id) {
		this.id=id;
	}

	public Long getId() {
		return id;
	}

}
