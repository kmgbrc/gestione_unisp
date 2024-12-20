package it.unisp.auth;

import it.unisp.model.Membri;
import it.unisp.model.Sessione;
import it.unisp.repository.MembriRepository;
import it.unisp.repository.SessioneRepository;
import it.unisp.security.JwtService;
import it.unisp.service.MembriService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final MembriRepository membriRepository;
    private final MembriService membriService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final SessioneRepository sessioneRepository;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public Membri verificaSessioneAttiva(String token) {
        // Trova la sessione nel database usando il token
        Sessione sessione = sessioneRepository.findByToken(token);
        if (sessione != null && sessione.getDataScadenza().isAfter(LocalDateTime.now())) {
            // Se la sessione esiste e non è scaduta, restituisci il membro associato
            return sessione.getMembro();
        }
        return null; // Nessuna sessione attiva
    }


    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        try {

            if (membriRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email già registrata");
            }

            var membro = Membri.builder()
                    .nome(request.getNome())
                    .cognome(request.getCognome())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .codiceFiscale(request.getCodiceFiscale())
                    .build();

            membriService.registraMembro(membro);

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String token = jwtService.generateToken(userDetails);

            // Calcola la data di scadenza utilizzando la configurazione
            LocalDateTime dataScadenza = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(System.currentTimeMillis() + jwtExpiration),
                    ZoneId.systemDefault()
            );

            // Salva il token nella tabella sessioni
            Sessione nuovaSessione = new Sessione();
            nuovaSessione.setMembro(membriRepository.findByEmail(request.getEmail()));
            nuovaSessione.setToken(token);
            nuovaSessione.setDataScadenza(dataScadenza);

            // Salva la nuova sessione nel database
            sessioneRepository.save(nuovaSessione);

            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
        } catch (RuntimeException e) {
            // Log dell'errore e gestione dell'eccezione
            throw new RuntimeException("Errore durante la registrazione: " + e.getMessage());
        }
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            if (userDetails == null || !passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
                throw new BadCredentialsException("Credenziali non valide");
            }

            String token = jwtService.generateToken(userDetails);

            // Calcola la data di scadenza utilizzando la configurazione
            LocalDateTime dataScadenza = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(System.currentTimeMillis() + jwtExpiration),
                    ZoneId.systemDefault()
            );

            // Salva il token nella tabella sessioni
            Sessione nuovaSessione = new Sessione();
            nuovaSessione.setMembro(membriRepository.findByEmail(request.getEmail()));
            nuovaSessione.setToken(token);
            nuovaSessione.setDataScadenza(dataScadenza);

            // Salva la nuova sessione nel database
            sessioneRepository.save(nuovaSessione);

            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Errore durante il login: " + e.getMessage());
        }
    }

    public void logout(String token) {
        // Rimuovi la sessione corrispondente al token dal database
        Sessione sessione = sessioneRepository.findByToken(token);
        if (sessione != null) {
            sessioneRepository.delete(sessione);
            System.out.println("Logout effettuato con successo.");
        } else {
            System.out.println("Token non trovato, impossibile effettuare il logout.");
        }
    }
}
