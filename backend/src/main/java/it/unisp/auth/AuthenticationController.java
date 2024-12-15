package it.unisp.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrazione di un nuovo utente", description = "Registra un nuovo utente nel sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registrazione completata con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida, controlla i parametri"),
            @ApiResponse(responseCode = "409", description = "Conflitto, l'utente potrebbe già esistere")
    })
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login di un utente", description = "Autentica un utente e restituisce un token di accesso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login completato con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida, controlla i parametri"),
            @ApiResponse(responseCode = "401", description = "Autenticazione fallita, credenziali non valide")
    })
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout di un utente", description = "Disconnette l'utente attualmente autenticato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout completato con successo"),
            @ApiResponse(responseCode = "401", description = "Utente non autenticato o token non valido")
    })
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        // Estrai il token dall'intestazione Authorization (es. "Bearer <token>")
        String token = authorizationHeader.substring(7); // Rimuovi "Bearer "

        authService.logout(token);

        return ResponseEntity.ok("Logout effettuato con successo.");
    }
}
