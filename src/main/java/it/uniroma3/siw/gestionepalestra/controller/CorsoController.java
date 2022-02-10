package it.uniroma3.siw.gestionepalestra.controller;

import it.uniroma3.siw.gestionepalestra.controller.session.SessionData;
import it.uniroma3.siw.gestionepalestra.controller.validation.CorsoValidator;
import it.uniroma3.siw.gestionepalestra.controller.validation.CredentialsValidator;
import it.uniroma3.siw.gestionepalestra.model.Corso;
import it.uniroma3.siw.gestionepalestra.model.Credentials;
import it.uniroma3.siw.gestionepalestra.model.PersonalTrainer;
import it.uniroma3.siw.gestionepalestra.model.User;
import it.uniroma3.siw.gestionepalestra.repository.CorsoRepository;
import it.uniroma3.siw.gestionepalestra.repository.CredentialsRepository;
import it.uniroma3.siw.gestionepalestra.repository.UserRepository;
import it.uniroma3.siw.gestionepalestra.service.CorsoService;
import it.uniroma3.siw.gestionepalestra.service.CredentialsService;
import it.uniroma3.siw.gestionepalestra.service.PersonalTrainerService;
import it.uniroma3.siw.gestionepalestra.service.UserService;
import org.apache.catalina.manager.util.SessionUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.uniroma3.siw.gestionepalestra.model.Credentials.ADMIN_ROLE;

@Controller
public class CorsoController {
	
	@Autowired
	CredentialsRepository credentialsRepository;
	@Autowired
	CorsoRepository corsoRepository;
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;
	@Autowired
	CredentialsService credentialsService;
	@Autowired
	CorsoService corsoService;
	
	
	@Autowired
	CredentialsValidator credentialsValidator;
	@Autowired
	CorsoValidator corsoValidator;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	SessionData sessionData;
	@Autowired
	PersonalTrainerService personalTrainerService;
	
	@RequestMapping(value = { "/corso/{id}" }, method = RequestMethod.GET)
	public String showCorso(Model model, @PathVariable Long id) {
		Credentials visitor = sessionData.getLoggedCredentials();
		model.addAttribute("visitor", visitor);
		Credentials credentials = credentialsService.getCredentials(id);
		model.addAttribute("credentials",credentials);
		Corso corso = corsoService.getCorso(id);
		model.addAttribute("corsi",corsoService.getAllCorsi());
		model.addAttribute("corso", corso);
		if (corso != null) {
			// If the corso exists
			model.addAttribute("owner", credentialsRepository.findByUser(corso.getOwner()).orElse(null));
			model.addAttribute("credentialsRepository", credentialsRepository);
			Map<Corso,List<User>> corso2members = new HashMap<>();
			List<User> users = new ArrayList<>();
			List<Long> ids = new ArrayList<>();
			for(User u : corso.getMembers()){
				if(!corso2members.containsValue(u)){
					users.add(u);
					corso2members.put(corso,users);
				}
				else{
					users = corso2members.get(u);
				}
			}
			for(User u : users){
				ids.add(u.getId());
			}

			model.addAttribute("corso2members",corso2members);
			model.addAttribute("ids",ids);
			corsoService.saveCorso(corso);
		}
		
		return "corso/corso";
	}
	
	@RequestMapping(value = { "/corso/create" }, method = RequestMethod.GET)
	public String showCreateCorsoPrompt(Model model) {
		Credentials visitor = sessionData.getLoggedCredentials();
		List<PersonalTrainer> listPersonal = personalTrainerService.getAllPersonalTrainer();
		model.addAttribute("listPersonal",listPersonal);
		model.addAttribute("visitor", visitor);
		model.addAttribute("corsoForm", new Corso());
		model.addAttribute("editing", false);
		if(visitor.getRole().equals("CLIENTE")){
			return "/error/403";
		}
		return "corso/corsoEdit";
	}
	
	@RequestMapping(value = { "/corso/create" }, method = RequestMethod.POST)
	public String createCorso(@Valid @ModelAttribute("corsoForm") Corso corso,
								BindingResult corsoBindingResult,
								Model model) throws Exception {
		Credentials visitor = sessionData.getLoggedCredentials();
		model.addAttribute("visitor", visitor);
		this.corsoValidator.validate(corso, corsoBindingResult);
		if(!(corsoBindingResult.hasErrors())) {
			corso.setOwner(visitor.getUser());
			Corso savedCorso = corsoService.saveCorso(corso);

			return "redirect:/corso/" + savedCorso.getId();
		}
		if(visitor.getRole().equals("CLIENTE")){
			return "/error/403";
		}
		return "corso/corsoEdit";
	}

	@RequestMapping(value = { "/corso/{id}/delete" }, method = RequestMethod.GET)
	public String deleteCorso(Model model, @PathVariable Long id) {
		Credentials visitor = sessionData.getLoggedCredentials();
		
		Corso corso = corsoService.getCorso(id);
		if (corso != null) {
			if (visitor.getRole().equals(ADMIN_ROLE)
					|| visitor.getUser().getId().equals(corso.getOwner().getId())) {
				// Delete the corso
				corsoService.deleteCorso(corso);
			}
		}
		if(visitor.getRole().equals("CLIENTE")){
			return "/error/403";
		}
		return "redirect:/";
	}
	
	@RequestMapping(value = { "/corso/{id}/edit" }, method = RequestMethod.GET)
	public String showEditCorsoPrompt(Model model, @PathVariable Long id) {
		Credentials visitor = sessionData.getLoggedCredentials();
		model.addAttribute("visitor", visitor);
		Corso corso = corsoService.getCorso(id);
		if (corso != null) {
			if (visitor.getRole().equals(ADMIN_ROLE) || visitor.getUser().getId().equals(corso.getOwner().getId())) {
				// Only admins/owners can edit corso
				model.addAttribute("corsoForm", corso);
				model.addAttribute("editing", true);
				
				return "corso/corsoEdit";
			}
		}
		if(visitor.getRole().equals("CLIENTE")){
			return "/error/403";
		}
		return "error/403";
	}
	
	@RequestMapping(value = { "/corso/{id}/edit" }, method = RequestMethod.POST)
	public String editCorso(@Valid @ModelAttribute("corsoForm") Corso corsoForm,
							  BindingResult corsoBindingResult,
							  Model model,
							  @PathVariable Long id) throws Exception{
		this.corsoValidator.validate(corsoForm, corsoBindingResult);
		
		Corso corso = corsoService.getCorso(id);
		Credentials visitor = sessionData.getLoggedCredentials();
		if (corso != null) {
			if (!corsoBindingResult.hasErrors()) {
				if (visitor.getRole().equals(ADMIN_ROLE) || corso.getOwner().getId().equals(visitor.getUser().getId())) {
					// Only admins/owners can edit corso
					corso.setName(corsoForm.getName());
					corso.setDifficolta(corsoForm.getDifficolta());
					corso.setPostiDisponibili(corsoForm.getPostiDisponibili());
					corso.setGiornoErogazione(corsoForm.getGiornoErogazione());
					corso.setFasciaOraria(corsoForm.getFasciaOraria());
					corsoService.saveCorso(corso);
				}
				return "redirect:/corso/" + id;
			}
			model.addAttribute("visitor", visitor);
			return "corso/corsoEdit";
		}
		if(visitor.getRole().equals("CLIENTE")){
			return "/error/403";
		}
		return "redirect:/corso/" + id;
	}

	@RequestMapping(value = { "/corso/{id}/prenota" }, method = RequestMethod.GET)
	public String prenotaCorso(Model model, @PathVariable Long id) {
		Credentials visitor = sessionData.getLoggedCredentials();
		Corso corso = corsoService.getCorso(id);

		//List<Corso> giornofasciaCorsi = corsoRepository.findByGiornoErogazioneAndFasciaOraria(corso.getGiornoErogazione(), corso.getFasciaOraria());
		if(!visitor.getUser().getCorsi().isEmpty()) {
			for (Corso c : visitor.getUser().getCorsi()) {
				if ( c.getGiornoErogazione().equals(corso.getGiornoErogazione()) && c.getFasciaOraria().equals(corso.getFasciaOraria())) {
					return "error/prenotazione";
				}
			}
		}
		if(visitor.getRole().equals("ADMIN")){
			return "/error/403";
		}
		corsoService.getCorso(id).addMember(sessionData.getLoggedUser());
		corsoService.saveCorso(corso);
		List<Corso> corsi = visitor.getUser().getCorsi();
		corsi.add(corso);
		User user = visitor.getUser();
		user.setCorsi(corsi);
		visitor.setUser(user);
		return "/corso/prenota";
	}

	@RequestMapping(value = { "/corso/{id}/disiscrivi" }, method = RequestMethod.GET)
	public String disiscriviCorso(Model model, @PathVariable Long id) {
		Credentials visitor = sessionData.getLoggedCredentials();
		Corso corso = corsoService.getCorso(id);
		corsoService.getCorso(id).removeMember(sessionData.getLoggedUser());
		List<Corso> corsi = visitor.getUser().getCorsi();
		corsi.remove(corso);
		User user = visitor.getUser();
		user.setCorsi(corsi);
		visitor.setUser(user);
		corsoService.saveCorso(corso);
		if(visitor.getRole().equals("ADMIN")){
			return "/error/403";
		}
		return "/corso/disiscrivi";
	}
}
