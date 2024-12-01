package it.unisp.controller;

import it.unisp.model.Prenotazione;
import it.unisp.services.PrenotazioneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prenotazioni")
@RequiredArgsConstructor
public class PrenotazioneController {
    private final PrenotazioneService prenotazioneService;

    @PostMapping("/prenota")
    public ResponseEntity<Prenotazione> prenotaPosto(
            @RequestParam Long membroId,
            @RequestParam Long attivitaId) {
        return ResponseEntity.ok(prenotazioneService.prenotaPosto(membroId, attivitaId));
    }

    @PostMapping("/valida/{id}")
    public ResponseEntity<Prenotazione> validaPrenotazione(
            @PathVariable Long id,
            @RequestParam String qrCode) {
        return ResponseEntity.ok(prenotazioneService.validaPrenotazione(id, qrCode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> annullaPrenotazione(@PathVariable Long id) {
        prenotazioneService.annullaPrenotazione(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/membro/{membroId}/attiva")
    public ResponseEntity<Prenotazione> getPrenotazioneAttiva(
            @PathVariable Long membroId,
            @RequestParam Long attivitaId) {
        return ResponseEntity.ok(prenotazioneService.getPrenotazioneAttiva(membroId, attivitaId));
    }
}
