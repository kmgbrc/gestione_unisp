package it.unisp.auth;

import it.unisp.model.Membri;
import it.unisp.repository.MembriRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MembriRepository membriRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            Membri membro = membriRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + email));

            // Ottieni la categoria dell'utente
            String categoria = membro.getCategoria().name(); // Ottieni il nome dell'enum

            // Crea l'autorità con il prefisso ROLE_
            return new User(
                    membro.getEmail(),
                    membro.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + categoria))
            );
        } catch (Exception e) {
            throw new RuntimeException("Errore nel caricamento dell'utente: " + e.getMessage());
        }
    }
}
