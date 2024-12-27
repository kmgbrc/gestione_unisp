package it.unisp.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @GetMapping("/session")
    @Operation(summary = "Controllo della sessione attiva", description = "Verifica se l'utente ha una sessione attiva.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessione attiva"),
            @ApiResponse(responseCode = "401", description = "Nessuna sessione attiva")
    })
    public ResponseEntity<Long> verificaSessioneAttiva(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Rimuovi "Bearer "

        Long membroId = authService.controllaSessioneAttiva(token);

        if (membroId != null) {
            return ResponseEntity.ok(membroId);
        } else {
            return ResponseEntity.status(401).build(); // Nessuna sessione attiva
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registrazione di un nuovo utente", description = "Registra un nuovo utente nel sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registrazione completata con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida, controlla i parametri"),
            @ApiResponse(responseCode = "409", description = "Conflitto, l'utente potrebbe già esistere")
    })
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response, HttpServletRequest req) {
        return ResponseEntity.ok(authService.register(request, response, req));
    }

    @PostMapping("/login")
    @Operation(summary = "Login di un utente", description = "Autentica un utente e restituisce un token di accesso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login completato con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida, controlla i parametri"),
            @ApiResponse(responseCode = "401", description = "Autenticazione fallita, credenziali non valide")
    })
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout di un utente", description = "Disconnette l'utente attualmente autenticato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout completato con successo"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato o token non valido")
    })
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader, HttpServletResponse response) {
        String token = authorizationHeader.substring(7); // Rimuovi "Bearer "

        authService.logout(token, response);

        return ResponseEntity.ok("Logout effettuato con successo.");
    }

    @PostMapping("/change-password")
    @Operation(summary = "Cambia la password dell'utente", description = "Permette all'utente di cambiare la propria password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password cambiata con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida, controlla i parametri"),
            @ApiResponse(responseCode = "401", description = "Autenticazione fallita, credenziali non valide"),
            @ApiResponse(responseCode = "403", description = "Accesso negato, utente non autorizzato")
    })
    public ResponseEntity<String> cambiaPassword(@RequestHeader("Authorization") String authorizationHeader,
                                                 @Valid @RequestBody ChangePasswordRequest request, HttpServletRequest req) {
        String token = authorizationHeader.substring(7); // Rimuovi "Bearer "

        boolean success = authService.cambiaPassword(token, request, req);

        if (success) {
            return ResponseEntity.ok("Password cambiata con successo.");
        } else {
            return ResponseEntity.status(403).body("Accesso negato, verifica le credenziali.");
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Richiesta di reset della password", description = "Invia un'email per il reset della password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email inviata con successo per il reset della password"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida, controlla i parametri"),
            @ApiResponse(responseCode = "404", description = "Utente non trovato con l'email fornita")
    })
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, HttpServletResponse response) {
        boolean emailSent = authService.sendPasswordResetEmail(request.getEmail(), response);

        if (emailSent) {
            return ResponseEntity.ok("Email inviata con successo per il reset della password.");
        } else {
            return ResponseEntity.status(404).body("Utente non trovato con l'email fornita.");
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset della password", description = "Imposta una nuova password utilizzando un token di reset.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password cambiata con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida, controlla i parametri"),
            @ApiResponse(responseCode = "404", description = "Token non valido o scaduto")
    })
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
                                                @Valid @RequestBody ResetPasswordRequest request,
                                                HttpServletRequest req) {
        boolean success = authService.resetPassword(token, request.getNewPassword(), req);

        if (success) {
            return ResponseEntity.ok("Password cambiata con successo.");
        } else {
            return ResponseEntity.status(404).body("Token non valido o scaduto.");
        }
    }


}
