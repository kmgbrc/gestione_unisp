package it.unisp.controller;

import it.unisp.model.Prenotazioni;
import it.unisp.service.PrenotazioneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prenotazioni")
@RequiredArgsConstructor
public class PrenotazioneController {
    private final PrenotazioneService prenotazioneService;

    @PostMapping("/prenota")
    public ResponseEntity<String> prenota(@RequestParam Long membroId, @RequestParam Long attivitaId) {
        prenotazioneService.processaPrenotazione(membroId, attivitaId);
        return ResponseEntity.ok("Richiesta di prenotazione inviata.");
    }
/*    @PostMapping("/prenota")
    public ResponseEntity<Prenotazioni> prenotaPosto(
            @RequestParam Long membroId,
            @RequestParam Long attivitaId) {
        return ResponseEntity.ok(prenotazioneService.prenotaNumero(membroId, attivitaId));
    }*/

    @PostMapping("/valida/{id}")
    public ResponseEntity<Prenotazioni> validaPrenotazione(
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
    public ResponseEntity<Prenotazioni> getPrenotazioneAttiva(
            @PathVariable Long membroId,
            @RequestParam Long attivitaId) {
        return ResponseEntity.ok(prenotazioneService.getPrenotazioneAttiva(membroId, attivitaId));
    }
}
