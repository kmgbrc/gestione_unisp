package it.unisp.controller;

import it.unisp.model.Notifiche;
import it.unisp.service.NotificheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifiche")
@RequiredArgsConstructor
public class NotificaController {
    private final NotificheService notificaService;

    @GetMapping("/membro/{membroId}")
    public ResponseEntity<List<Notifiche>> getNotificheMembro(@PathVariable Long membroId) {
        return ResponseEntity.ok(notificaService.getNotificheMembro(membroId));
    }

    @GetMapping
    public ResponseEntity<List<Notifiche>> getAllNotifiche() {
        return ResponseEntity.ok(notificaService.getAllNotifiche());
    }

    @GetMapping("/{Id}")
    public ResponseEntity<Optional> getNotificaById(@PathVariable Long id) {
        return ResponseEntity.ok(notificaService.getNotificaById(id));
    }

    @GetMapping("/non-lette/{membroId}")
    public ResponseEntity<List<Notifiche>> getNotificheNonLette(@PathVariable Long membroId) {
        return ResponseEntity.ok(notificaService.getNotificheNonLette(membroId));
    }

    @PutMapping("/{id}/segna-letta")
    public ResponseEntity<Notifiche> segnaComeLetta(@PathVariable Long id) {
        return ResponseEntity.ok(notificaService.segnaComeLetta(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaNotifiche(@PathVariable Long id) {
        notificaService.eliminaNotifiche(id);
        return ResponseEntity.ok().build();
    }
}
