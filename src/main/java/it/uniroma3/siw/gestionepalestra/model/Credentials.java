package it.uniroma3.siw.gestionepalestra.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class Credentials {
    public static final String DEFAULT_ROLE = "CLIENTE";
    public static final String ADMIN_ROLE = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String userName;

    @Column(nullable = false, length = 100)
    private String password;

    @Transient
    private String confirmPassword;

    @OneToOne(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude @ToString.Exclude
    private User user;

    @Column(nullable = false, length = 10)
    private String role;

    @Column(updatable = false, nullable = false)
    private LocalDateTime creationTimestamp;

    @Column(nullable = false)
    private LocalDateTime lastUpdateTimestamp;

    public Credentials() {}

    public Credentials(String userName, String password) {
        this();
        this.userName = userName;
        this.password = password;
    }

    public Credentials(String userName, String password, String role, User user) {
        this(userName, password);
        this.role = role;
        this.user = user;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public static String getDefaultRole() {
        return DEFAULT_ROLE;
    }

    public static String getAdminRole() {
        return ADMIN_ROLE;
    }


}
