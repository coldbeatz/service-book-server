package servicebook.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import servicebook.user.confirmation.EmailConfirmation;

import servicebook.user.role.Role;

import java.time.LocalDateTime;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
@Entity(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Електронна адреса користувача
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Чи підтверджено e-mail
     */
    @Column(name = "confirm_email", nullable = false)
    private boolean confirmEmail;

    /**
     * ФІО користувача
     */
    @Column(name = "full_name")
    private String fullName;

    /**
     * Хеш пароля BCrypt
     */
    @JsonIgnore
    @Column(name = "password_hash")
    private String password;

    /**
     * Тип (роль) користувача
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @JsonIgnore
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Чи увімкнена розсилка новин електронною поштою
     */
    @Column(name = "enable_email_newsletter")
    private boolean enableEmailNewsletter;

    @ToString.Exclude
    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private EmailConfirmation emailConfirmation;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
