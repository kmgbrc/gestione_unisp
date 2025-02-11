package it.unisp.model;

import it.unisp.enums.CategoriaMembro;
import it.unisp.enums.StatoMembro;
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

    @NotBlank(message = "Nome è obbligatorio")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Cognome è obbligatorio")
    @Column(nullable = false)
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

    @NotBlank(message = "Codice Fiscale è obbligatorio")
    @Column(name = "codice_fiscale", nullable = false)
    private String codiceFiscale;

    @Column(name = "permesso_soggiorno", nullable = false)
    private boolean permessoSoggiorno;

    private boolean passaporto;

    @Column(name = "certificato_studente")
    private boolean certificatoStudente;

    @Column(name = "dichiarazione_isee")
    private boolean dichiarazioneIsee;

    @Column(name="data_creazione", nullable=false)
    private LocalDate dataCreazione=LocalDate.now();

    @Column(name = "anno_scadenza_iscrizione")
    private Integer annoScadenzaIscrizione;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @NotBlank(message = "Password è obbligatoria")
    @Column(nullable = false)
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
