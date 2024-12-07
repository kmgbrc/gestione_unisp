package it.unisp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "membri")
public class Membri implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Nome è obbligatorio")
    private String nome;

    @Column(nullable = false)
    @NotBlank(message = "Cognome è obbligatorio")
    private String cognome;

    @NotBlank(message = "Email è obbligatoria")
    @Email(message = "Email non valida")
    @Column(nullable = false, unique = true)
    private String email;

    private String telefono;

    @Enumerated(EnumType.STRING)
    private CategoriaMembro categoria;

    @Enumerated(EnumType.STRING)
    private StatoMembro stato;

    @Column(name = "codice_fiscale", nullable = false)
    @NotBlank(message = "Codice Fiscale è obbligatorio")
    private String codiceFiscale;

    @Column(name = "permesso_soggiorno")
    private boolean permessoSoggiorno;

    private boolean passaporto;

    @Column(name = "certificato_studente")
    private boolean certificatoStudente;

    @Column(name = "dichiarazione_isee")
    private boolean dichiarazioneIsee;

    @Column(name = "data_iscrizione")
    private LocalDate dataIscrizione;

    @Column(name = "data_ultimo_rinnovo")
    private LocalDate dataUltimoRinnovo;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + categoria.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isDeleted;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !isDeleted;
    }
}
