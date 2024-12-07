package it.unisp.controller;

import it.unisp.model.Partecipazioni;
import it.unisp.service.PartecipazioniService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/partecipazioni")
@RequiredArgsConstructor
public class PartecipazioneController {
    private final PartecipazioniService partecipazioniService;

    @PostMapping("/registra")
    @Operation(summary = "Registra una partecipazione", description = "Registra la partecipazione di un membro a un'attività.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Partecipazione registrata con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
            @ApiResponse(responseCode = "404", description = "Membro o attività non trovati")
    })
    public ResponseEntity<Partecipazioni> registraPartecipazione(
            @RequestParam Long membroId,
            @RequestParam Long attivitaId,
            @RequestParam Long delegatoId,
            @RequestParam boolean presente) {
        return ResponseEntity.ok(partecipazioniService.registraPartecipazione(membroId, attivitaId, presente, delegatoId));
    }

    @GetMapping("/membro/{membroId}")
    @Operation(summary = "Recupera le partecipazioni di un membro", description = "Restituisce una lista delle partecipazioni di un membro specificato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista delle partecipazioni recuperata con successo"),
            @ApiResponse(responseCode = "404", description = "Membro non trovato")
    })
    public ResponseEntity<List<Partecipazioni>> getPartecipazioniMembro(@PathVariable Long membroId) {
        return ResponseEntity.ok(partecipazioniService.getPartecipazioniByMembro(membroId));
    }

    @GetMapping
    @Operation(summary = "Recupera tutte le partecipazioni", description = "Restituisce una lista di tutte le partecipazioni registrate.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista delle partecipazioni recuperata con successo")
    })
    public ResponseEntity<List<Partecipazioni>> getAllPartecipazioni() {
        return ResponseEntity.ok(partecipazioniService.getAllPartecipazioni());
    }

    @GetMapping("/attivita/{attivitaId}")
    //@PreAuthorize("hasRole('STAFF')")
    @Operation(summary = "Recupera le partecipazioni di un'attività", description = "Restituisce una lista delle partecipazioni associate a un'attività specificata.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista delle partecipazioni recuperata con successo"),
            @ApiResponse(responseCode = "404", description = "Attività non trovata")
    })
    public ResponseEntity<List<Partecipazioni>> getPartecipazioniAttivita(@PathVariable Long attivitaId) {
        return ResponseEntity.ok(partecipazioniService.getPartecipazioniAttivita(attivitaId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recupera una partecipazione per ID", description = "Restituisce i dettagli di una partecipazione specificata dall'ID.")
    @Parameter(name = "id", description = "ID della partecipazione da recuperare", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Partecipazione recuperata con successo"),
            @ApiResponse(responseCode = "404", description = "Partecipazione non trovata")
    })
    public ResponseEntity<Partecipazioni> getPartecipazioniById(@PathVariable Long id) {
        return ResponseEntity.ok(partecipazioniService.getPartecipazioniById(id));
    }
}
