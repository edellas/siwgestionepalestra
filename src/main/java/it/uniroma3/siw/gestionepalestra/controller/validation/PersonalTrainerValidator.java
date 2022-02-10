package it.uniroma3.siw.gestionepalestra.controller.validation;


import it.uniroma3.siw.gestionepalestra.model.PersonalTrainer;
import it.uniroma3.siw.gestionepalestra.service.PersonalTrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonalTrainerValidator implements Validator {
    @Autowired
    PersonalTrainerService personalTrainerService;

    @Override
    public void validate(Object o, Errors errors) {
        PersonalTrainer personalTrainer = (PersonalTrainer) o;
        String nome = personalTrainer.getNome().trim();
        String cognome = personalTrainer.getCognome();

        if (nome.isEmpty())
            errors.rejectValue("nome", "required");
        else if(cognome.isEmpty())
            errors.rejectValue("cognome", "required");
        else if (this.personalTrainerService.getPersonalTrainer(nome,cognome) != null)
            errors.rejectValue("nome", "duplicate");

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PersonalTrainer.class.equals(clazz);
    }
}
