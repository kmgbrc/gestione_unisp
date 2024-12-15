package it.unisp.controller;

import it.unisp.model.Attivita;
import it.unisp.service.AttivitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attivita")
@RequiredArgsConstructor
public class AttivitaController {
    private final AttivitaService attivitaService;

    @Operation(summary = "Recupera tutte le attività")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista delle attività recuperata con successo"),
            @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping
    public ResponseEntity<List<Attivita>> getAllAttivita() {
        return ResponseEntity.ok(attivitaService.getAllAttivita());
    }

    @Operation(summary = "Recupera un'attività per ID")
    @Parameter(name = "id", description = "ID dell'attività da recuperare", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attività recuperata con successo"),
            @ApiResponse(responseCode = "404", description = "Attività non trovata")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Attivita> getAttivitaById(@PathVariable Long id) {
        return ResponseEntity.ok(attivitaService.getAttivitaById(id));
    }

    @Operation(summary = "Recupera attività in un intervallo di date")
    @Parameter(name = "start", description = "Data e ora di inizio", required = true)
    @Parameter(name = "end", description = "Data e ora di fine", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista delle attività recuperata con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
            @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @GetMapping("/range")
    public ResponseEntity<List<Attivita>> getAttivitaByDateRange(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(attivitaService.getAttivitaByDateRange(start, end));
    }

    @Operation(summary = "Crea una nuova attività")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attività creata con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
            @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping
    public ResponseEntity<Attivita> createAttivita(@RequestBody Attivita attivita) {
        return ResponseEntity.ok(attivitaService.createAttivita(attivita));
    }

    @Operation(summary = "Aggiorna un'attività esistente")
    @Parameter(name = "id", description = "ID dell'attività da aggiornare", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attività aggiornata con successo"),
            @ApiResponse(responseCode = "404", description = "Attività non trovata"),
            @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Attivita> updateAttivita(@PathVariable Long id, @RequestBody Attivita attivita) {
        return ResponseEntity.ok(attivitaService.updateAttivita(id, attivita));
    }

    @Operation(summary = "Elimina un'attività per ID")
    @Parameter(name = "id", description = "ID dell'attività da eliminare", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attività eliminata con successo"),
            @ApiResponse(responseCode = "404", description = "Attività non trovata"),
            @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttivita(@PathVariable Long id) {
        attivitaService.deleteAttivita(id);
        return ResponseEntity.ok().build();
    }
}
