package it.unisp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.unisp.model.Prenotazioni;
import it.unisp.service.PrenotazioneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prenotazioni")
@RequiredArgsConstructor
public class PrenotazioneController {
    private final PrenotazioneService prenotazioneService;

    @PostMapping("/prenota")
    @Operation(summary = "Prenota un'attività", description = "Invia una richiesta di prenotazione per un membro e un'attività specificati.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Richiesta di prenotazione inviata con successo"),
            @ApiResponse(responseCode = "404", description = "Membro o attività non trovati"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida")
    })
    public ResponseEntity<String> prenota(@RequestParam Long membroId, @RequestParam Long attivitaId, @RequestParam(required = false) Long delegatoId) {
        System.out.println("Membro ID: " + membroId + ", Attività ID: " + attivitaId + ", Delegato ID: " + delegatoId);
        prenotazioneService.processaPrenotazione(membroId, attivitaId, delegatoId);
        return ResponseEntity.ok("Richiesta di prenotazione inviata.");
    }

    @PostMapping("/valida")
    @Operation(summary = "Valida una prenotazione", description = "Valida una prenotazione specificata dall'ID utilizzando un QR Code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prenotazione validata con successo"),
            @ApiResponse(responseCode = "404", description = "Prenotazione non trovata"),
            @ApiResponse(responseCode = "400", description = "QR Code non valido")
    })
    public ResponseEntity<Prenotazioni> validaPrenotazione(
            @RequestParam String qrCode) {
        return ResponseEntity.ok(prenotazioneService.validaPrenotazione(qrCode));
    }

    @GetMapping("/attivita/{attivitaId}")
    @Operation(summary = "Recupera tutte le prenotazioni di un'attività", description = "Restituisce tutte le prenotazioni associate a una specifica attività.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prenotazioni recuperate con successo"),
            @ApiResponse(responseCode = "404", description = "Attività non trovata")
    })
    public ResponseEntity<List<Prenotazioni>> getAllPrenotazioniAttivita(@PathVariable Long attivitaId) {
        List<Prenotazioni> prenotazioni = prenotazioneService.getAllPrenotazioniByAttivita(attivitaId);
        return ResponseEntity.ok(prenotazioni);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Annulla una prenotazione", description = "Annulla una prenotazione specificata dall'ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Prenotazione annullata con successo"),
            @ApiResponse(responseCode = "404", description = "Prenotazione non trovata")
    })
    public ResponseEntity<Void> annullaPrenotazione(@PathVariable Long id) {
        prenotazioneService.annullaPrenotazione(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/membro/{membroId}/attiva")
    @Operation(summary = "Recupera la prenotazione di un membro a un attività", description = "Restituisce la prenotazione per un membro e un'attività specificati.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prenotazione attiva recuperata con successo"),
            @ApiResponse(responseCode = "404", description = "Membro o attività non trovati")
    })
    public ResponseEntity<Prenotazioni> getPrenotazioneMembroAttivita(
            @PathVariable Long membroId,
            @RequestParam Long attivitaId) {
        return ResponseEntity.ok(prenotazioneService.getPrenotazioneMembroAttivita(membroId, attivitaId));
    }
}
