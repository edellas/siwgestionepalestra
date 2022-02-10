package it.uniroma3.siw.gestionepalestra.repository;

import it.uniroma3.siw.gestionepalestra.model.Corso;
import it.uniroma3.siw.gestionepalestra.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CorsoRepository extends CrudRepository<Corso, Long> {

    List<Corso> findByMembers(User member);

    List<Corso> findByGiornoErogazione(String giorno);

    List<Corso> findByFasciaOraria(String fascia);

    List<Corso> findByGiornoErogazioneAndFasciaOraria(String giorno, String fascia);
}

