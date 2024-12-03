package it.unisp.auth;

import it.unisp.model.CategoriaMembro;
import it.unisp.model.Membri;
import it.unisp.model.StatoMembro;
import it.unisp.repository.MembriRepository;
import it.unisp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final MembriRepository membriRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

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
                    .categoria(CategoriaMembro.valueOf("passivo"))
                    .stato(StatoMembro.valueOf("attivo"))
                    .codiceFiscale(request.getCodiceFiscale())
                    .permessoSoggiorno(false)
                    .passaporto(false)
                    .certificatoStudente(false)
                    .dichiarazioneIsee(false)
                    .isDeleted(false)
                    .build();

            membriRepository.save(membro);

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String token = jwtService.generateToken(userDetails);

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
            System.out.println("Tentativo di login per: " + request.getEmail());

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            if (userDetails == null) {
                System.out.println("Utente non trovato");
                throw new BadCredentialsException("Credenziali non valide");
            }

            if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
                System.out.println("Password non valida");
                throw new BadCredentialsException("Credenziali non valide");
            }

            String token = jwtService.generateToken(userDetails);
            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
        } catch (BadCredentialsException e) {
            System.out.println("Errore durante il login: " + e.getMessage());
            throw new BadCredentialsException("Errore durante il login: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Errore imprevisto durante il login: " + e.getMessage());
            throw new RuntimeException("Errore imprevisto durante il login: " + e.getMessage(), e);
        }
    }
}
