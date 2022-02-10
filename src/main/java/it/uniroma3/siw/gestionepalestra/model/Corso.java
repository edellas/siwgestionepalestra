package it.uniroma3.siw.gestionepalestra.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EqualsAndHashCode
@Getter @Setter
public class Corso {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private String difficolta;

    @Column(nullable = false)
    private  int postiDisponibili;

    @ManyToMany(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude @ToString.Exclude
    private List<User> members;

    @ManyToOne
    private PersonalTrainer personalTrainer;

   private String giornoErogazione;

   private String fasciaOraria;

    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;


    @Column(updatable = false, nullable = false)
    private LocalDateTime creationTimestamp;

    @Column(nullable = false)
    private LocalDateTime lastUpdateTimestamp;

    public Corso() {
        this.members = new ArrayList<>();
    }

    public Corso(String name, String description) {
        this.name = name;
        this.difficolta = description;
    }

    public void addMember(User user) {
        if (!this.members.contains(user))
            this.members.add(user);
            this.setPostiDisponibili(this.getPostiDisponibili() - 1);
    }

    public void addMembers(List<User> users) {
        if (!this.members.contains(users)) {
            for(User u : users){
                this.members.add(u);
            }
        }
    }

    public void removeMember(User user) {
        if (this.members.contains(user))
            this.members.remove(user);
        this.setPostiDisponibili(this.getPostiDisponibili() + 1);
    }

    @PrePersist
    protected void onPersist() {
        this.creationTimestamp = LocalDateTime.now();
        this.lastUpdateTimestamp = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdateTimestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDifficolta() {
        return difficolta;
    }

    public void setDifficolta(String description) {
        this.difficolta = description;
    }

    public List<User> getMembers() {
        return this.members;
    }

    public int getSize(){
        return this.members.size();
    }

    public Long getMember(User user){
        if(this.members.contains(user))
            return user.getId();
        else
            return null;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public LocalDateTime getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getOwner() {
        return owner;
    }

    public int getPostiDisponibili() {
        return postiDisponibili;
    }

    public void setPostiDisponibili(int postiDisponibili) {
        this.postiDisponibili = postiDisponibili;
    }

    public String getGiornoErogazione() {
        return giornoErogazione;
    }

    public void setGiornoErogazione(String giornoErogazione) {
        this.giornoErogazione = giornoErogazione;
    }

    public String getFasciaOraria() {
        return fasciaOraria;
    }

    public void setFasciaOraria(String fasciaOraria) {
        this.fasciaOraria = fasciaOraria;
    }

    public PersonalTrainer getPersonalTrainer() {
        return personalTrainer;
    }

    public void setPersonalTrainer(PersonalTrainer personalTrainer) {
        this.personalTrainer = personalTrainer;
    }
}
