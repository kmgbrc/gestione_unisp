package it.unisp.controller;

import it.unisp.exception.MissingDocumentException;
import it.unisp.model.Documenti;
import it.unisp.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<Documenti> caricaDocumento(
            @RequestParam Long membroId,
            @RequestParam String tipo,
            @RequestParam MultipartFile file) throws MissingDocumentException, IOException {
        return ResponseEntity.ok(documentoService.caricaDocumento(membroId, tipo, file));
    }

    @PutMapping("/{id}/approva")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Documenti> approvaDocumento(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.approvaDocumento(id));
    }

    @PutMapping("/{id}/rifiuta")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Documenti> rifiutaDocumento(
            @PathVariable Long id,
            @RequestParam String motivazione) {
        return ResponseEntity.ok(documentoService.rifiutaDocumento(id, motivazione));
    }

    @GetMapping("/membro/{membroId}")
    public ResponseEntity<List<Documenti>> getDocumentiMembro(@PathVariable Long membroId) {
        return ResponseEntity.ok(documentoService.getDocumentiMembro(membroId));
    }
}
