package it.unisp.controller;

import it.unisp.model.Notifiche;
import it.unisp.service.NotificheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifiche")
@RequiredArgsConstructor
public class NotificaController {
    private final NotificheService notificaService;

    @GetMapping("/membro/{membroId}")
    @Operation(summary = "Recupera le notifiche di un membro", description = "Restituisce una lista di notifiche per un membro specificato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifiche recuperate con successo"),
            @ApiResponse(responseCode = "404", description = "Membro non trovato")
    })
    public ResponseEntity<List<Notifiche>> getNotificheMembro(@PathVariable Long membroId) {
        return ResponseEntity.ok(notificaService.getNotificheMembro(membroId));
    }

    @GetMapping
    @Operation(summary = "Recupera tutte le notifiche", description = "Restituisce una lista di tutte le notifiche.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista delle notifiche recuperata con successo")
    })
    public ResponseEntity<List<Notifiche>> getAllNotifiche() {
        return ResponseEntity.ok(notificaService.getAllNotifiche());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recupera una notifica per ID", description = "Restituisce i dettagli di una notifica specificata dall'ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifica recuperata con successo"),
            @ApiResponse(responseCode = "404", description = "Notifica non trovata")
    })
    public ResponseEntity<Optional<Notifiche>> getNotificaById(@PathVariable Long id) {
        return ResponseEntity.ok(notificaService.getNotificaById(id));
    }

    @GetMapping("/non-lette/{membroId}")
    @Operation(summary = "Recupera le notifiche non lette di un membro", description = "Restituisce una lista di notifiche non lette per un membro specificato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifiche non lette recuperate con successo"),
            @ApiResponse(responseCode = "404", description = "Membro non trovato")
    })
    public ResponseEntity<List<Notifiche>> getNotificheNonLette(@PathVariable Long membroId) {
        return ResponseEntity.ok(notificaService.getNotificheNonLette(membroId));
    }

    @PutMapping("/{id}/segna-letta")
    @Operation(summary = "Segna una notifica come letta", description = "Aggiorna lo stato di una notifica specificata dall'ID per segnarla come letta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifica segnata come letta con successo"),
            @ApiResponse(responseCode = "404", description = "Notifica non trovata")
    })
    public ResponseEntity<Notifiche> segnaComeLetta(@PathVariable Long id) {
        return ResponseEntity.ok(notificaService.segnaComeLetta(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una notifica", description = "Elimina una notifica specificata dall'ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notifica eliminata con successo"),
            @ApiResponse(responseCode = "404", description = "Notifica non trovata")
    })
    public ResponseEntity<Void> eliminaNotifiche(@PathVariable Long id) {
        notificaService.eliminaNotifiche(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Crea una nuova notifica", description = "Crea e salva una nuova notifica nel sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notifica creata con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida")
    })
    public ResponseEntity<Void> creaNotifica(@RequestParam Long  membroId, @RequestParam String messaggio) {
        notificaService.creaNotifiche(membroId, messaggio);
        return ResponseEntity.status(201).build();
    }

}
