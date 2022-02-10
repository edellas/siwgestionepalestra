package it.uniroma3.siw.gestionepalestra.controller.validation;

import it.uniroma3.siw.gestionepalestra.model.Corso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

/**
 * Validator for Project
 */
@Component
public class CorsoValidator implements Validator {
	
	final Integer MAX_NAME_LENGTH = 20;
	final Integer MIN_NAME_LENGTH = 2;
	final Integer MAX_DESCRIPTION_LENGTH = 50;
	final Integer MIN_DESCRIPTION_LENGTH = 4;
	private static final Logger logger = LoggerFactory.getLogger(CorsoValidator.class);


	@Override
	public void validate(Object o, Errors errors) {
		Corso corso = (Corso) o;
		String name = corso.getName().trim();
		String description = corso.getDifficolta().trim();
		if (name.isEmpty())
			errors.rejectValue("name", "required");
		else if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH)
			errors.rejectValue("name", "size");
		
		if (description.isEmpty())
			errors.rejectValue("description", "required");
		else if (description.length() < MIN_DESCRIPTION_LENGTH || description.length() > MAX_DESCRIPTION_LENGTH)
			errors.rejectValue("description", "size");
		if(corso.getPostiDisponibili()<=0)
			errors.rejectValue("postiDisponibili","size");
		if(corso.getGiornoErogazione().isEmpty())
			errors.rejectValue("inizio","required");

		if (!errors.hasErrors()) {
			logger.debug("confermato: valori non nulli");
		}

	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Corso.class.equals(clazz);
	}
	
}

