package it.uniroma3.siw.gestionepalestra.repository;

import it.uniroma3.siw.gestionepalestra.model.Corso;
import it.uniroma3.siw.gestionepalestra.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByCorsi(Corso corso);

}
