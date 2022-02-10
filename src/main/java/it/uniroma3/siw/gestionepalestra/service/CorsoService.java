package it.uniroma3.siw.gestionepalestra.service;

import it.uniroma3.siw.gestionepalestra.model.Corso;
import it.uniroma3.siw.gestionepalestra.model.User;
import it.uniroma3.siw.gestionepalestra.repository.CorsoRepository;
import it.uniroma3.siw.gestionepalestra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CorsoService {

    @Autowired
    protected CorsoRepository corsoRepository;

    @Autowired
    protected UserRepository userRepository;

    @Transactional
    public Corso getCorso(long id) {
        Optional<Corso> result = this.corsoRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public Corso saveCorso(Corso corso) {
        return this.corsoRepository.save(corso);
    }

    @Transactional
    public void deleteCorso(Corso corso) {
        this.corsoRepository.delete(corso);
    }

    @Transactional
    public List<Corso> getAllCorsi() {
        List<Corso> result = new ArrayList<>();
        Iterable<Corso> iterable = this.corsoRepository.findAll();
        for(Corso corso : iterable)
            result.add(corso);
        return result;
    }

    @Transactional
    public List<Corso> getPrenotati(){
        List<Corso> lista = new ArrayList<>();
        for(User u : userRepository.findAll()){
            for(Corso c : this.corsoRepository.findAll()){
                if(c.getMembers().equals(u))
                    lista.add(c);
            }
        }
        return lista;
    }

    @Transactional
    public long countAll() {
        return corsoRepository.count();
    }
}

