package it.unisp.controller;

import com.unisp.gestioneunisp.model.Attivita;
import com.unisp.gestioneunisp.service.AttivitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attivita")
@RequiredArgsConstructor
public class AttivitaController {
    private final AttivitaService attivitaService;

    @GetMapping
    public ResponseEntity<List<Attivita>> getAllAttivita() {
        return ResponseEntity.ok(attivitaService.getAllAttivita());
    }

    @GetMapping("/range")
    public ResponseEntity<List<Attivita>> getAttivitaByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(attivitaService.getAttivitaByDateRange(start, end));
    }

    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Attivita> createAttivita(@RequestBody Attivita attivita) {
        return ResponseEntity.ok(attivitaService.createAttivita(attivita));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Attivita> updateAttivita(@PathVariable Long id, @RequestBody Attivita attivita) {
        return ResponseEntity.ok(attivitaService.updateAttivita(id, attivita));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Void> deleteAttivita(@PathVariable Long id) {
        attivitaService.deleteAttivita(id);
        return ResponseEntity.ok().build();
    }
}
