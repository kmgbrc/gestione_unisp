package it.unisp.controller;

import it.unisp.model.Pagamenti;
import it.unisp.service.MembriService;
import it.unisp.service.PagamentoService;
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
    private final MembriService membriService;

    @PostMapping("/iscrizione")
    public ResponseEntity<Pagamenti> processaPagamentoIscrizione(
            @RequestParam Long membroId,
            @RequestParam String transazioneId) {
        return ResponseEntity.ok(pagamentoService.processaPagamentoIscrizione(
                membriService.findByMembroIdAndIsDeletedFalse(membroId),
                transazioneId
        ));
    }

    @PostMapping("/donazione")
    public ResponseEntity<Pagamenti> processaDonazione(
            @RequestParam Long membroId,
            @RequestParam BigDecimal importo,
            @RequestParam String transazioneId) {
        return ResponseEntity.ok(pagamentoService.processaDonazione(
                membriService.findByMembroIdAndIsDeletedFalse(membroId),
                importo,
                transazioneId
        ));
    }

    @GetMapping("/membro/{membroId}")
    public ResponseEntity<List<Pagamenti>> getPagamentiMembro(@PathVariable Long membroId) {
        return ResponseEntity.ok(pagamentoService.getPagamentiMembro(membroId));
    }

    @GetMapping("/verifica-iscrizione/{membroId}")
    public ResponseEntity<Boolean> verificaPagamentoIscrizione(
            @PathVariable Long membroId,
            @RequestParam int anno) {
        return ResponseEntity.ok(pagamentoService.verificaPagamentoIscrizione(membroId, anno));
    }
}
