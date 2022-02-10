package it.uniroma3.siw.gestionepalestra.controller;

import it.uniroma3.siw.gestionepalestra.controller.session.SessionData;
import it.uniroma3.siw.gestionepalestra.model.Corso;
import it.uniroma3.siw.gestionepalestra.model.Credentials;
import it.uniroma3.siw.gestionepalestra.model.User;
import it.uniroma3.siw.gestionepalestra.repository.CorsoRepository;
import it.uniroma3.siw.gestionepalestra.service.CorsoService;
import it.uniroma3.siw.gestionepalestra.service.CredentialsService;
import it.uniroma3.siw.gestionepalestra.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {
	
	@Autowired
	CredentialsService credentialsService;

	@Autowired
	CorsoService corsoService;

	@Autowired
	CorsoRepository corsoRepository;

	@Autowired
	SessionData sessionData;
	
	public MainController() {}
	
	@RequestMapping(value = { "/siw" }, method = RequestMethod.GET)
	public String siw(Model model) {
		return "redirect:/";
	}
	
	@RequestMapping(value = { "/", "/index", "/home" }, method = RequestMethod.GET)
	public String index(Model model) {
		User visitor = sessionData.getLoggedUser();
		Credentials isAdmin = sessionData.getLoggedCredentials();

		if (visitor != null) {
			// User logged in
			Credentials credentials = credentialsService.getCredentials(visitor);
			model.addAttribute("credentials", credentials);
			List<Corso> corsi = new ArrayList<>();

			for(Corso c : credentials.getUser().getCorsi()) {
				corsi.add(c);
			}

			model.addAttribute("ownedCorso", credentials.getUser().getCorsi());
			model.addAttribute("corsi", corsi);
			model.addAttribute("visitor",visitor);
			model.addAttribute("isAdmin",isAdmin);
			return "home";
		}
		
		model.addAttribute("totalCorso",  corsoService.countAll());
		model.addAttribute("totalMembers", credentialsService.countAll());
		return "index";
	}
	
	@RequestMapping(value = { "/corsi" }, method = RequestMethod.GET)
	public String corsi(Model model) {
		Credentials isAdmin = sessionData.getLoggedCredentials();
		List<Credentials> credentials = credentialsService.getAllCredentials();
		List<Pair<Corso, Credentials>> corsi = new ArrayList<>();
		for (Corso c : corsoService.getAllCorsi()) {
			corsi.add(new Pair<>(c, c.getOwner() != null ? credentialsService.getCredentials(c.getOwner()) : null));
		}
		model.addAttribute("corsi", corsi);
		model.addAttribute("credentials",credentials);
		model.addAttribute("isAdmin", isAdmin);

		return "corsi";
	}

	
	@RequestMapping(value = { "/utenti" }, method = RequestMethod.GET)
	public String users(Model model) {
		model.addAttribute("credentials", credentialsService.getAllCredentials());
		model.addAttribute("isAdmin",sessionData.getLoggedCredentials());
		if(sessionData.getLoggedCredentials().getRole().equals("CLIENTE")){
			return "error/403";
		}
		return "utenti";
	}

	
	@RequestMapping(value = { "/admin" }, method = RequestMethod.GET)
	public String admin(Model model) {
		model.addAttribute("corsi", corsoService.getAllCorsi());
		model.addAttribute("isAdmin",sessionData.getLoggedCredentials());
		model.addAttribute("credentials", credentialsService.getAllCredentials());
		return "admin";
	}
}
