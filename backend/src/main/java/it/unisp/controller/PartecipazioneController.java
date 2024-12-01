package it.unisp.controller;

import com.unisp.gestione.models.Partecipazione;
import com.unisp.gestione.services.PartecipazioneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partecipazioni")
@RequiredArgsConstructor
public class PartecipazioneController {
    private final PartecipazioneService partecipazioneService;

    @PostMapping("/registra")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Partecipazione> registraPartecipazione(
            @RequestParam Long membroId,
            @RequestParam Long attivitaId,
            @RequestParam boolean presente) {
        return ResponseEntity.ok(partecipazioneService.registraPartecipazione(membroId, attivitaId, presente));
    }

    @PostMapping("/delega")
    public ResponseEntity<Partecipazione> registraDelega(
            @RequestParam Long membroId,
            @RequestParam Long attivitaId,
            @RequestParam Long delegatoId) {
        return ResponseEntity.ok(partecipazioneService.registraDelega(membroId, attivitaId, delegatoId));
    }

    @GetMapping("/membro/{membroId}")
    public ResponseEntity<List<Partecipazione>> getPartecipazioniMembro(@PathVariable Long membroId) {
        return ResponseEntity.ok(partecipazioneService.getPartecipazioniMembro(membroId));
    }

    @GetMapping("/attivita/{attivitaId}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Partecipazione>> getPartecipazioniAttivita(@PathVariable Long attivitaId) {
        return ResponseEntity.ok(partecipazioneService.getPartecipazioniAttivita(attivitaId));
    }
}
