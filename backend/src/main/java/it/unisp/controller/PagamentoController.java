package it.unisp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.unisp.enums.TipoPagamento;
import it.unisp.model.Pagamenti;
import it.unisp.service.MembriService;
import it.unisp.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/pagamenti")
@RequiredArgsConstructor
public class PagamentoController {
    private final PagamentoService pagamentoService;
    private final MembriService membriService;

    @GetMapping
    @Operation(summary = "Recupera tutti i pagamenti non cancellati", description = "Restituisce una lista di tutti i pagamenti che non sono stati cancellati.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamenti recuperati con successo")
    })
    public ResponseEntity<List<Pagamenti>> getAllPagamentiNonCancellati() {
        return ResponseEntity.ok(pagamentoService.getAllPagamentiNonCancellati());
    }

    @GetMapping("/all")
    @Operation(summary = "Recupera tutti i pagamenti", description = "Restituisce una lista di tutti i pagamenti anche quilli che NON sono stati cancellati.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamenti recuperati con successo")
    })
    public ResponseEntity<List<Pagamenti>> getAllPagamenti() {
        return ResponseEntity.ok(pagamentoService.getAllPagamenti());
    }

    @PostMapping("/deleted")
    @Operation(summary = "Cancella pagamenti passati", description = "Imposta l'attributo isDeleted a true per i pagamenti il cui anno di data_pagamento è già passato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamenti passati cancellati con successo"),
            @ApiResponse(responseCode = "204", description = "Nessun pagamento passato trovato")
    })
    public ResponseEntity<Void> cancellaPagamentiPassati() {
        pagamentoService.cancellaPagamentiPassati();
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @Operation(summary = "Processa pagamento", description = "Elabora un pagamento per un membro specificato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento elaborato con successo"),
            @ApiResponse(responseCode = "404", description = "Membro non trovato")
    })
    public ResponseEntity<Pagamenti> processaPagamentoIscrizione(
            @RequestParam Long membroId,
            @RequestParam BigDecimal importo,
            @RequestParam String transazioneId,
            @RequestParam TipoPagamento tipo) {
        return ResponseEntity.ok(pagamentoService.processaPagamento(
                membriService.findByMembroIdAndIsDeletedFalse(membroId), importo, transazioneId, tipo
        ));
    }

    @GetMapping("/membro/{membroId}")
    @Operation(summary = "Recupera pagamenti di un membro", description = "Restituisce una lista di pagamenti effettuati da un membro specificato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamenti recuperati con successo"),
            @ApiResponse(responseCode = "404", description = "Membro non trovato")
    })
    public ResponseEntity<List<Pagamenti>> getPagamentiMembro(@PathVariable Long membroId) {
        return ResponseEntity.ok(pagamentoService.getPagamentiMembro(membroId));
    }

    @GetMapping("/verifica-iscrizione/{membroId}")
    @Operation(summary = "Verifica pagamento di iscrizione", description = "Controlla se un membro ha effettuato il pagamento di iscrizione per un anno specificato.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verifica completata con successo"),
            @ApiResponse(responseCode = "404", description = "Membro non trovato"),
            @ApiResponse(responseCode = "400", description = "Anno non valido")
    })
    public ResponseEntity<Boolean> verificaPagamentoIscrizione(
            @PathVariable Long membroId,
            @RequestParam int anno) {
        return ResponseEntity.ok(pagamentoService.verificaPagamentoIscrizione(membroId, anno));
    }
}
