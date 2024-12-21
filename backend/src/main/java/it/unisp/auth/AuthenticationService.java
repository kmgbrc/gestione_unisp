package it.unisp.auth;

import it.unisp.model.Membri;
import it.unisp.model.Sessione;
import it.unisp.repository.SessioneRepository;
import it.unisp.security.JwtService;
import it.unisp.service.MembriService;
import it.unisp.util.DateUtils;
import it.unisp.util.EmailSender;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final MembriService membriService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final SessioneRepository sessioneRepository;
    private final EmailSender emailSender;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public Long controllaSessioneAttiva(String token) {
        if (token == null || token.isEmpty()) {
            return null; // Nessun token fornito
        }

        // Trova la sessione nel database usando il token
        Sessione sessione = sessioneRepository.findByToken(token);
        if (sessione != null && sessione.getDataScadenza().isAfter(LocalDateTime.now())) {
            // Se la sessione esiste e non è scaduta, restituisci il membro associato
            return sessione.getMembro().getId();
        }
        return null; // Nessuna sessione attiva
    }

    @Transactional
    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response) {
        try {
            var membro = Membri.builder()
                    .nome(request.getNome())
                    .cognome(request.getCognome())
                    .email(request.getEmail())
                    .password(request.getPassword())
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
            nuovaSessione.setMembro(membriService.getMembroByEmail(request.getEmail()));
            nuovaSessione.setToken(token);
            nuovaSessione.setDataScadenza(dataScadenza);

            // Salva la nuova sessione nel database
            sessioneRepository.save(nuovaSessione);

            // Imposta il cookie con il token
            Cookie cookie = new Cookie("tokenUnisp", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Solo su HTTPS
            cookie.setPath("/");
            cookie.setMaxAge((int) jwtExpiration / 1000); // Imposta la durata in secondi
            response.addCookie(cookie);

            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
        } catch (RuntimeException e) {
            // Log dell'errore e gestione dell'eccezione
            throw new RuntimeException("Errore durante la registrazione: " + e.getMessage());
        }
    }

    public AuthenticationResponse login(AuthenticationRequest request, HttpServletResponse response) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            if (userDetails == null || !passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
                throw new BadCredentialsException("Credenziali non valide");
            }

            String token = jwtService.generateToken(userDetails);

            // Imposta il cookie con il token
            Cookie cookie = new Cookie("tokenUnisp", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Solo su HTTPS
            cookie.setPath("/");
            cookie.setMaxAge((int) jwtExpiration / 1000); // Imposta la durata in secondi
            response.addCookie(cookie);

            // Calcola la data di scadenza utilizzando la configurazione
            LocalDateTime dataScadenza = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(System.currentTimeMillis() + jwtExpiration),
                    ZoneId.systemDefault()
            );

            // Salva il token nella tabella sessioni
            Sessione nuovaSessione = new Sessione();
            nuovaSessione.setMembro(membriService.getMembroByEmail(request.getEmail()));
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

    public void logout(String token, HttpServletResponse response) {
        // Rimuovi la sessione corrispondente al token dal database
        Sessione sessione = sessioneRepository.findByToken(token);
        if (sessione != null) {
            sessioneRepository.delete(sessione);

            // Rimuovi il cookie dal client
            Cookie cookie = new Cookie("tokenUnisp", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(0); // Scade immediatamente
            response.addCookie(cookie);

            System.out.println("Logout effettuato con successo.");
        } else {
            System.out.println("Token non trovato, impossibile effettuare il logout.");
        }
    }

    public boolean cambiaPassword(String token, ChangePasswordRequest request) {
        Membri membro = sessioneRepository.findByToken(token).getMembro();

        if (membro != null && passwordEncoder.matches(request.getCurrentPassword(), membro.getPassword())) {
            membriService.registraMembro(membro);

            // Invia l'email con il link per il reset della password
            String oggetto = "PASSWORD CAMBIATA";
            String messaggio = "La tua password è stata cambiata con successo";

            emailSender.inviaEmailGenerico(
                    membro.getEmail(),
                    membro.getNome(),
                    oggetto,
                    messaggio,
                    null,
                    null
            );

            return true;
        }

        return false;
    }

    public boolean sendPasswordResetEmail(String email, HttpServletResponse response) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (userDetails == null) {
            return false; // Utente non trovato
        }

        String token = jwtService.generateToken(userDetails);

        // Imposta il cookie con il token
        Cookie cookie = new Cookie("tokenUnisp", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Solo su HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) jwtExpiration / 1000); // Imposta la durata in secondi
        response.addCookie(cookie);

        // Calcola la data di scadenza utilizzando la configurazione
        LocalDateTime dataScadenza = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis() + jwtExpiration),
                ZoneId.systemDefault()
        );

        // Cancella tutte le sessioni
        List<Sessione> altreSessioni = sessioneRepository.findByMembroIdAndDataScadenzaAfter (membriService.getMembroByEmail(email).getId(), LocalDateTime.now());
        for(Sessione sess : altreSessioni){
            sessioneRepository.delete(sess);
        }

        // Salva il token nella tabella sessioni
        Sessione newSessione = new Sessione();
        newSessione.setMembro(membriService.getMembroByEmail(email));
        newSessione.setToken(token);
        newSessione.setDataScadenza(dataScadenza);

        // Salva la nuova sessione nel database
        sessioneRepository.save(newSessione);

        // Invia l'email con il link per il reset della password
        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;
        String oggetto = "Reset Password";
        String messaggio = "Clicca sul seguente link per resettare la tua password: " + resetLink;

        emailSender.inviaEmailGenerico(
                email,
                membriService.getMembroByEmail(email).getNome(),
                oggetto,
                messaggio,
                null,
                null // Nome dell'allegato
        );

        return true; // Email inviata con successo
    }
    public boolean resetPassword(String token, String newPassword) {

        if(DateUtils.isScaduto(sessioneRepository.findByToken(token).getDataScadenza()))
            return false;

        Membri membro = sessioneRepository.findByToken(token).getMembro();

        // Aggiorna la password dell'utente
        membro.setPassword(newPassword);
        membriService.registraMembro(membro);

        // Invia l'email con il link per il reset della password
        String oggetto = "Reset Password";
        String messaggio = "La tua password è stata cambiata con successo";

        emailSender.inviaEmailGenerico(
                membro.getEmail(),
                membro.getNome(),
                oggetto,
                messaggio,
                null,
                null
        );

        return true; // Password cambiata con successo
    }


}
