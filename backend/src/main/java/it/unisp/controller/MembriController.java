package it.unisp.controller;

import it.unisp.model.Membri;
import it.unisp.service.MembriService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/membri")
@RequiredArgsConstructor
public class MembriController {
    private final MembriService membriService;

    @GetMapping
    @Operation(summary = "Recupera tutti i membri", description = "Restituisce una lista di tutti i membri registrati.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista dei membri recuperata con successo"),
            @ApiResponse(responseCode = "403", description = "Accesso negato")
    })
    public ResponseEntity<List<Membri>> getAllMembri() {
        return ResponseEntity.ok(membriService.findByIsDeletedFalse());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recupera un membro per ID")
    @Parameter(name = "id", description = "ID del membro da recuperare", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membro recuperato con successo"),
            @ApiResponse(responseCode = "403", description = "Accesso negato")
    })
    public ResponseEntity<Membri> getMembroById(@PathVariable Long id) {
        return ResponseEntity.ok(membriService.findByMembroIdAndIsDeletedFalse(id));
    }

    @PostMapping
    @Operation(summary = "Crea un nuovo membro", description = "Registra un nuovo membro nel sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membro creato con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida")
    })
    public ResponseEntity<Membri> createMembro(@RequestBody Membri membro, HttpServletRequest request) {
        return ResponseEntity.ok(membriService.registraMembro(membro, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Aggiorna un membro esistente", description = "Aggiorna i dettagli di un membro specificato dall'ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membro aggiornato con successo"),
            @ApiResponse(responseCode = "404", description = "Membro non trovato"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida")
    })
    public ResponseEntity<Membri> updateMembro(
            @Parameter(description = "ID del membro da aggiornare", required = true)
            @PathVariable Long id,
            @Valid @RequestBody Membri membro,
            HttpServletRequest request) {
        return ResponseEntity.ok(membriService.updateMembro(id, membro, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un membro", description = "Elimina un membro specificato dall'ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Membro eliminato con successo"),
            @ApiResponse(responseCode = "404", description = "Membro non trovato")
    })
    public ResponseEntity<Void> deleteMembro(
            @Parameter(description = "ID del membro da eliminare", required = true)
            @PathVariable Long id,
            HttpServletRequest request) {
        membriService.deleteMembro(id, request);
        return ResponseEntity.noContent().build();
    }
}
