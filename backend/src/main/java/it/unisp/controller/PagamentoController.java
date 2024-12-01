package it.unisp.controller;

import com.unisp.gestione.models.Pagamento;
import com.unisp.gestione.services.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/pagamenti")
@RequiredArgsConstructor
public class PagamentoController {
    private final PagamentoService pagamentoService;

    @PostMapping("/iscrizione")
    public ResponseEntity<Pagamento> processaPagamentoIscrizione(
            @RequestParam Long membroId,
            @RequestParam String transazioneId) {
        return ResponseEntity.ok(pagamentoService.processaPagamentoIscrizione(
                membroService.getMembro(membroId),
                transazioneId
        ));
    }

    @PostMapping("/donazione")
    public ResponseEntity<Pagamento> processaDonazione(
            @RequestParam Long membroId,
            @RequestParam BigDecimal importo,
            @RequestParam String transazioneId) {
        return ResponseEntity.ok(pagamentoService.processaDonazione(
                membroService.getMembro(membroId),
                importo,
                transazioneId
        ));
    }

    @GetMapping("/membro/{membroId}")
    public ResponseEntity<List<Pagamento>> getPagamentiMembro(@PathVariable Long membroId) {
        return ResponseEntity.ok(pagamentoService.getPagamentiMembro(membroId));
    }

    @GetMapping("/verifica-iscrizione/{membroId}")
    public ResponseEntity<Boolean> verificaPagamentoIscrizione(
            @PathVariable Long membroId,
            @RequestParam int anno) {
        return ResponseEntity.ok(pagamentoService.verificaPagamentoIscrizione(membroId, anno));
    }
}
