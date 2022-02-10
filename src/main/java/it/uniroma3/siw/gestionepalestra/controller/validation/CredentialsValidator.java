package it.uniroma3.siw.gestionepalestra.controller.validation;

import it.uniroma3.siw.gestionepalestra.model.Credentials;
import it.uniroma3.siw.gestionepalestra.model.User;
import it.uniroma3.siw.gestionepalestra.service.CredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CredentialsValidator implements Validator {

    final Integer MAX_USERNAME_LENGTH = 20;
    final Integer MIN_USERNAME_LENGTH = 4;
    final Integer MAX_PASSWORD_LENGTH = 20;
    final Integer MIN_PASSWORD_LENGTH = 4;

    @Autowired
    CredentialsService credentialsService;

    @Override
    public void validate(Object o, Errors errors) {
        Credentials credentials = (Credentials) o;
        String userName = credentials.getUserName().trim();
        String password = credentials.getPassword().trim();

        if (userName.isEmpty())
            errors.rejectValue("userName", "required");
        else if (userName.length() < MIN_USERNAME_LENGTH || userName.length() > MAX_USERNAME_LENGTH)
            errors.rejectValue("userName", "size");
        else if (this.credentialsService.getCredentials(userName) != null)
            errors.rejectValue("userName", "duplicate");

        if (password.isEmpty())
            errors.rejectValue("password", "required");
        else if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH)
            errors.rejectValue("password", "size");

        if(!(credentials.getPassword().equals(credentials.getConfirmPassword()))){
            errors.rejectValue("password", "notmatch.password");
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

}