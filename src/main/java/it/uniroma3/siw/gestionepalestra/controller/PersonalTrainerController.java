package it.uniroma3.siw.gestionepalestra.controller;


import it.uniroma3.siw.gestionepalestra.controller.session.SessionData;
import it.uniroma3.siw.gestionepalestra.controller.validation.PersonalTrainerValidator;
import it.uniroma3.siw.gestionepalestra.model.Credentials;
import it.uniroma3.siw.gestionepalestra.model.PersonalTrainer;
import it.uniroma3.siw.gestionepalestra.repository.CredentialsRepository;
import it.uniroma3.siw.gestionepalestra.repository.UserRepository;
import it.uniroma3.siw.gestionepalestra.service.CorsoService;
import it.uniroma3.siw.gestionepalestra.service.CredentialsService;
import it.uniroma3.siw.gestionepalestra.service.PersonalTrainerService;
import it.uniroma3.siw.gestionepalestra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

import static it.uniroma3.siw.gestionepalestra.model.Credentials.ADMIN_ROLE;
import static it.uniroma3.siw.gestionepalestra.model.Credentials.DEFAULT_ROLE;

@Controller
public class PersonalTrainerController {
    @Autowired
    CredentialsRepository credentialsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;
    @Autowired
    CredentialsService credentialsService;
    @Autowired
    CorsoService corsoService;
    @Autowired
    PersonalTrainerService personalTrainerService;

    @Autowired
    PersonalTrainerValidator personalTrainerValidator;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    SessionData sessionData;


    @RequestMapping(value = { "/personals" }, method = RequestMethod.GET)
    public String showPersonals(Model model) {
        Credentials visitor = sessionData.getLoggedCredentials();
        model.addAttribute("visitor", visitor);
        model.addAttribute("personals",personalTrainerService.getAllPersonalTrainer());
        if(visitor.getRole().equals("CLIENTE")){
            return "/error/403";
        }

        return "personal/personals";
    }

    @RequestMapping(value = { "/personal/{id}" }, method = RequestMethod.GET)
    public String showPersonal(Model model, @PathVariable Long id) {
        Credentials visitor = sessionData.getLoggedCredentials();
        model.addAttribute("visitor", visitor);
        Credentials credentials = credentialsService.getCredentials(id);
        model.addAttribute("credentials",credentials);
        PersonalTrainer personalTrainer = personalTrainerService.getPersonalTrainer(id);
        model.addAttribute("personals",personalTrainerService.getAllPersonalTrainer());
        model.addAttribute("personal", personalTrainer);
        if(visitor.getRole().equals(DEFAULT_ROLE)){
            return "error/403";
        }
        if (personalTrainer != null) {
            // If the tipologia exists
            personalTrainerService.savePersonalTrainer(personalTrainer);
            return "personal/personal";
        }

        return "error/404";
    }

    @RequestMapping(value = { "/personals/create" }, method = RequestMethod.GET)
    public String showCreatePersonalPrompt(Model model) {
        Credentials visitor = sessionData.getLoggedCredentials();
        model.addAttribute("visitor", visitor);
        model.addAttribute("personalForm", new PersonalTrainer());
        model.addAttribute("editing", false);
        if(visitor.getRole().equals(DEFAULT_ROLE)){
            return "error/403";
        }
        return "personal/personalEdit";
    }

    @RequestMapping(value = { "/personals/create" }, method = RequestMethod.POST)
    public String createPersonal(@Valid @ModelAttribute("personalForm") PersonalTrainer personalTrainer,
                                            BindingResult personalBindingResult,
                                            Model model) throws Exception {
        Credentials visitor = sessionData.getLoggedCredentials();
        model.addAttribute("visitor", visitor);
        this.personalTrainerValidator.validate(personalTrainer,personalBindingResult);
        if(visitor.getRole().equals(DEFAULT_ROLE)){
            return "error/403";
        }
        if(!personalBindingResult.hasErrors()){
            PersonalTrainer savedPersonal = personalTrainerService.savePersonalTrainer(personalTrainer);

            return "redirect:/personal/" + savedPersonal.getId();
        }
        return "personal/personalEdit";
    }

    @RequestMapping(value = { "/personal/{id}/edit" }, method = RequestMethod.GET)
    public String showEditPersonalPrompt(Model model, @PathVariable Long id) {
        Credentials visitor = sessionData.getLoggedCredentials();
        model.addAttribute("visitor", visitor);
        PersonalTrainer personalTrainer = personalTrainerService.getPersonalTrainer(id);
        if(visitor.getRole().equals(DEFAULT_ROLE)){
            return "error/403";
        }
        if (personalTrainer != null) {
            if (visitor.getRole().equals(ADMIN_ROLE)) {
                // Only admins/owners can edit Meccanico
                model.addAttribute("personalForm", personalTrainer);
                model.addAttribute("editing", true);

                return "personal/personalEdit";
            }
        }
        return "error/403";
    }

    @RequestMapping(value = { "/personal/{id}/edit" }, method = RequestMethod.POST)
    public String editPersonal(@Valid @ModelAttribute("personalForm") PersonalTrainer personalTrainerForm,
                                          BindingResult personalBindingResult,
                                          Model model,
                                          @PathVariable Long id) throws Exception{


        PersonalTrainer personalTrainer = personalTrainerService.getPersonalTrainer(id);
        Credentials visitor = sessionData.getLoggedCredentials();
        this.personalTrainerValidator.validate(personalTrainerForm,personalBindingResult);
        if(visitor.getRole().equals(DEFAULT_ROLE)){
            return "error/403";
        }
        if (personalTrainer != null) {
            if (visitor.getRole().equals(ADMIN_ROLE) && !personalBindingResult.hasErrors()) {
                // Only admins/owners can edit meccanico
                personalTrainer.setNome(personalTrainerForm.getNome());
                personalTrainer.setCognome(personalTrainerForm.getCognome());
                personalTrainerService.savePersonalTrainer(personalTrainer);
            }
            return "redirect:/personal/" + id;
        }
        model.addAttribute("visitor", visitor);

        return "redirect:/personal/" + id;
    }
}
