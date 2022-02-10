package it.uniroma3.siw.gestionepalestra.repository;

import it.uniroma3.siw.gestionepalestra.model.Credentials;
import it.uniroma3.siw.gestionepalestra.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {

    Optional<Credentials> findByUserNameIgnoreCase(String userName);

    Optional<Credentials> findByUser(User user);
}
