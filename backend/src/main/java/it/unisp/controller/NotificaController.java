package it.unisp.controller;

import it.unisp.model.Notifica;
import it.unisp.service.NotificaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifiche")
@RequiredArgsConstructor
public class NotificaController {
    private final NotificaService notificaService;

    @GetMapping("/membro/{membroId}")
    public ResponseEntity<List<Notifica>> getNotificheMembro(@PathVariable Long membroId) {
        return ResponseEntity.ok(notificaService.getNotificheMembro(membroId));
    }

    @GetMapping("/non-lette/{membroId}")
    public ResponseEntity<List<Notifica>> getNotificheNonLette(@PathVariable Long membroId) {
        return ResponseEntity.ok(notificaService.getNotificheNonLette(membroId));
    }

    @PutMapping("/{id}/segna-letta")
    public ResponseEntity<Notifica> segnaComeLetta(@PathVariable Long id) {
        return ResponseEntity.ok(notificaService.segnaComeLetta(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaNotifica(@PathVariable Long id) {
        notificaService.eliminaNotifica(id);
        return ResponseEntity.ok().build();
    }
}
