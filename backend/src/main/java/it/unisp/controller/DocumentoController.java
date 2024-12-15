package it.unisp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.unisp.exception.MissingDocumentException;
import it.unisp.model.Documenti;
import it.unisp.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documenti")
@RequiredArgsConstructor
public class DocumentoController {
    private final DocumentoService documentoService;

    @PostMapping("/carica")
    @Operation(summary = "Carica un documento", description = "Carica un documento per un membro specificato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento caricato con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida, controlla i parametri"),
            @ApiResponse(responseCode = "404", description = "Membro non trovato"),
            @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    public ResponseEntity<Documenti> caricaDocumento(
            @RequestParam Long membroId,
            @RequestParam String tipo,
            @RequestParam MultipartFile file) throws MissingDocumentException, IOException {
        return ResponseEntity.ok(documentoService.caricaDocumento(membroId, tipo, file));
    }

    @PutMapping("/{id}/approva")
    @Operation(summary = "Approva un documento", description = "Approva un documento specificato dall'ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento approvato con successo"),
            @ApiResponse(responseCode = "404", description = "Documento non trovato")
    })
    public ResponseEntity<Documenti> approvaDocumento(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.approvaDocumento(id));
    }

    @PutMapping("/{id}/rifiuta")
    @Operation(summary = "Rifiuta un documento", description = "Rifiuta un documento specificato dall'ID con una motivazione.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento rifiutato con successo"),
            @ApiResponse(responseCode = "404", description = "Documento non trovato"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida, motivazione mancante")
    })
    public ResponseEntity<Documenti> rifiutaDocumento(
            @PathVariable Long id,
            @RequestParam String motivazione) {
        return ResponseEntity.ok(documentoService.rifiutaDocumento(id, motivazione));
    }

    @GetMapping("/membro/{membroId}")
    @Operation(summary = "Recupera documenti di un membro", description = "Restituisce una lista di documenti associati a un membro specificato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documenti recuperati con successo"),
            @ApiResponse(responseCode = "404", description = "Membro non trovato")
    })
    public ResponseEntity<List<Documenti>> getDocumentiMembro(@PathVariable Long membroId) {
        return ResponseEntity.ok(documentoService.getDocumentiMembro(membroId));
    }
}
