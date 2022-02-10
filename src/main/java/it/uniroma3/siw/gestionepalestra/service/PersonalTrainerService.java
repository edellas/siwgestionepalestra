package it.uniroma3.siw.gestionepalestra.service;

import it.uniroma3.siw.gestionepalestra.model.PersonalTrainer;
import it.uniroma3.siw.gestionepalestra.repository.PersonalTrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonalTrainerService {
    @Autowired
    protected PersonalTrainerRepository personalTrainerRepository;


    @Transactional
    public PersonalTrainer getPersonalTrainer(long id) {
        Optional<PersonalTrainer> result = this.personalTrainerRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public PersonalTrainer getPersonalTrainer(String nome, String cognome) {
        Optional<PersonalTrainer> result = Optional.ofNullable(this.personalTrainerRepository.findByNomeAndCognome(nome,cognome));
        return result.orElse(null);
    }


    @Transactional
    public PersonalTrainer savePersonalTrainer(PersonalTrainer personalTrainer) {
        return this.personalTrainerRepository.save(personalTrainer);
    }

    @Transactional
    public List<PersonalTrainer> getAllPersonalTrainer() {
        List<PersonalTrainer> result = new ArrayList<>();
        Iterable<PersonalTrainer> iterable = this.personalTrainerRepository.findAll();
        for(PersonalTrainer personalTrainer : iterable)
            result.add(personalTrainer);
        return result;
    }

    @Transactional
    public void deletePersonalTrainer(PersonalTrainer personalTrainer) {
        this.personalTrainerRepository.delete(personalTrainer);
    }

    @Transactional
    public long countAll() {
        return personalTrainerRepository.count();
    }
}
