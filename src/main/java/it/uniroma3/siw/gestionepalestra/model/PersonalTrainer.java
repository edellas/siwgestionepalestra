package it.uniroma3.siw.gestionepalestra.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class PersonalTrainer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;

    private String cognome;

    @OneToMany(mappedBy = "personalTrainer")
    private List<Corso> corsiInsegnati;

    public PersonalTrainer(){

    }

    public PersonalTrainer(String nome, String cognome, List<Corso> corsiInsegnati) {
        this.nome = nome;
        this.cognome = cognome;
        this.corsiInsegnati = corsiInsegnati;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public List<Corso> getCorsiInsegnati() {
        return corsiInsegnati;
    }

    public void setCorsiInsegnati(List<Corso> corsiInsegnati) {
        this.corsiInsegnati = corsiInsegnati;
    }
}