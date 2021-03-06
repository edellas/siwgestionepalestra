package it.uniroma3.siw.gestionepalestra.authentication;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

import static it.uniroma3.siw.gestionepalestra.model.Credentials.ADMIN_ROLE;
/**
 * The AuthConfiguration is a Spring Security Configuration.
 * It extends WebSecurityConfigurerAdapter, meaning that it provides the settings for Web security.
 */
@Configuration
@EnableWebSecurity
public class AuthConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * The datasource is automatically injected into the AuthConfiguration (using its getters and setters)
     * and it is used to access the DB to get the Credentials to perform authentication and authorization
     */
    @Autowired
    DataSource datasource;

    /**
     * This method provides the whole authentication and authorization configuration to use.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // authorization paragraph: here we define WHO can access WHICH pages
                .authorizeRequests()
                // anyone (authenticated or not) can access the welcome page, the login page, and the registration page
                .antMatchers(HttpMethod.GET, "/", "/index", "/corsi", "/utenti",
                        "/login", "/register",
                        "/webjars/**", "/css/**", "/js/**").permitAll()
                // anyone (authenticated or not) can send POST requests to the login endpoint and the register endpoint
                .antMatchers(HttpMethod.POST, "/login", "/register").permitAll()
                // only authenticated users with ADMIN authority can access the admin pag
                .antMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
                .antMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
                // all authenticated users can access all the remaining other pages
                .anyRequest().authenticated()

                // login paragraph: here we define how to login
                // use formlogin protocol to perform login
                .and().formLogin()
                .defaultSuccessUrl("/")
                .loginPage("/login")
                .permitAll()
                .successHandler((request, response, authentication) -> response.sendRedirect("/siw/"))
                // after login is successful, redirect to the logged user homepage

                // NOTE: using the default configuration, the /login endpoint is mapped to an auto-generated login page.
                // If we wanted to create a login page of own page, we would need to
                // - use the .loginPage() method in this configuration
                // - write a controller method that returns our login view when a GET method is sent to /login
                //   (but Spring would still handle the POST automatically)

                // logout paragraph: we are going to define here how to logout
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login?logout");        // after logout is successful, redirect to /index page
    }

    /**
     * This method provides the SQL queries to get username and password.
     * NOTE: field denoted in Java by camelCase convention
     *       are denoted in Postgres by snake_case convention by default
     *       (e.g. "userName" field in the Java class results in "user_name" DB column)
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(this.datasource)
                //retrieve username and role
                .authoritiesByUsernameQuery("SELECT user_name, role FROM credentials WHERE lower(user_name)=lower(?)")
                //retrieve username, password and a boolean flag specifying whether the user is enabled or not (always enabled in our case)
                .usersByUsernameQuery("SELECT user_name, password, 1 as enabled FROM credentials WHERE lower(user_name)=lower(?)");
    }

    /**
     * This method defines a "passwordEncoder" Bean.
     * The passwordEncoder Bean is used to encrypt and decrypt the Credentials passwords.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authenticate an user with given credentials
     * @param userName The username of the user
     * @param plainPassword The password of the user
     */
    public void authenticate(String userName, String plainPassword) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, plainPassword);
        try {
            Authentication auth = authenticationManager().authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
