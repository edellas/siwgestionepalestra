package it.uniroma3.siw.gestionepalestra;

import it.uniroma3.siw.gestionepalestra.model.Corso;
import it.uniroma3.siw.gestionepalestra.model.Credentials;
import it.uniroma3.siw.gestionepalestra.model.User;
import it.uniroma3.siw.gestionepalestra.service.CorsoService;
import it.uniroma3.siw.gestionepalestra.service.CredentialsService;
import it.uniroma3.siw.gestionepalestra.service.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.Month;

@SpringBootApplication
public class GestionePalestraApplication {
    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    UserService userService;

    @Autowired
    private CorsoService corsoService;

    public static void main(String[] args) {
        SpringApplication.run(GestionePalestraApplication.class, args);
    }


    @Bean
    public InitializingBean populateDatabaseIfEmpty() {
        return () -> {
            if (credentialsService.countAll() == 0) {
                User cliente1 = new User("Aharon", "Salmoni");
                // Users
                Credentials credAdmin = credentialsService.saveCredentials(new Credentials(
                        "admin",
                        "3001680705",
                        Credentials.ADMIN_ROLE,
                        new User("Edoardo", "Della Seta"))
                );


                Credentials credDefault = credentialsService.saveCredentials(new Credentials(
                        "default",
                        "3001680705",
                        Credentials.DEFAULT_ROLE,
                        cliente1)
                );
            }
            ;
        };
    }
}
