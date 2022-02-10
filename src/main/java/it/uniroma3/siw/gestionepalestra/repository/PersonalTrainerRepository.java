package it.uniroma3.siw.gestionepalestra.repository;

import it.uniroma3.siw.gestionepalestra.model.PersonalTrainer;
import org.springframework.data.repository.CrudRepository;

public interface PersonalTrainerRepository extends CrudRepository<PersonalTrainer, Long> {
    PersonalTrainer findByNomeAndCognome(String nome, String cognome);
}
