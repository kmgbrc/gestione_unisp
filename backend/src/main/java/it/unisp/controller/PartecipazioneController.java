package it.unisp.controller;

import it.unisp.model.Partecipazioni;
import it.unisp.service.PartecipazioniService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partecipazioni")
@RequiredArgsConstructor
public class PartecipazioneController {
    private final PartecipazioniService partecipazioniService;

    @PostMapping("/registra")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Partecipazioni> registraPartecipazione(
            @RequestParam Long membroId,
            @RequestParam Long attivitaId,
            @RequestParam Long delegatoId,
            @RequestParam boolean presente) {
        return ResponseEntity.ok(partecipazioniService.registraPartecipazione(membroId, attivitaId, presente, delegatoId));
    }

/*    @PostMapping("/delega")
    public ResponseEntity<Partecipazioni> registraDelega(
            @RequestParam Long membroId,
            @RequestParam Long attivitaId,
            @RequestParam Long delegatoId) {
        return ResponseEntity.ok(partecipazioniService.registraDelega(membroId, attivitaId, delegatoId));
    }*/

    @GetMapping("/membro/{membroId}")
    public ResponseEntity<List<Partecipazioni>> getPartecipazioniMembro(@PathVariable Long membroId) {
        return ResponseEntity.ok(partecipazioniService.getPartecipazioniByMembro(membroId));
    }

    @GetMapping("/attivita/{attivitaId}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Partecipazioni>> getPartecipazioniAttivita(@PathVariable Long attivitaId) {
        return ResponseEntity.ok(partecipazioniService.getPartecipazioniAttivita(attivitaId));
    }
}
