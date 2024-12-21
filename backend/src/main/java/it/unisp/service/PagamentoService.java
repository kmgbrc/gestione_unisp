package it.unisp.service;

import it.unisp.enums.TipoPagamento;
import it.unisp.model.Membri;
import it.unisp.model.Pagamenti;
import it.unisp.enums.StatoMembro;
import it.unisp.repository.PagamentiRepository;
import it.unisp.util.EmailSender;
import it.unisp.util.PDFGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagamentoService {
    private final PagamentiRepository pagamentiRepository;
    private final NotificheService notificheService;
    private final PDFGenerator pdfGenerator;
    private final EmailSender emailSender;

    public List<Pagamenti> getAllPagamentiNonCancellati() {
        return pagamentiRepository.findByIsDeletedFalse();
    }

    public List<Pagamenti> getAllPagamenti() {
        return pagamentiRepository.findAll();
    }

    @Transactional
    public void cancellaPagamentiPassati() {
        // Ottieni la data e ora attuale
        LocalDateTime now = LocalDateTime.now();

        // Estrai l'anno corrente
        int currentYear = now.getYear();

        // Trova tutti i pagamenti con dataPagamento in anni precedenti e non cancellati
        List<Pagamenti> pagamentiPassati = pagamentiRepository.findByDataPagamentoBeforeAndIsDeletedFalse(now);

        for (Pagamenti pagamento : pagamentiPassati) {
            if (pagamento.getDataPagamento().getYear() < currentYear) {
                pagamento.setDeleted(true);
                pagamentiRepository.save(pagamento);
            }
        }
    }


    @Transactional
    public Pagamenti processaPagamento(Membri membro, BigDecimal importo, String transazioneId, TipoPagamento tipo) {
        Pagamenti pagamento = new Pagamenti();
        pagamento.setMembro(membro);
        pagamento.setTipoPagamento(tipo);
        pagamento.setImporto(importo);
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setTransazioneId(transazioneId);

        Pagamenti saved = pagamentiRepository.save(pagamento);

        membro.setStato(StatoMembro.ATTIVO);

        // Genera ricevuta PDF
        byte[] ricevutaPdf = pdfGenerator.generaRicevutaPagamento(saved);

        // Invia email con ricevuta
        String oggetto;
        String messaggio;

        if(tipo.equals(TipoPagamento.ISCRIZIONE) || tipo.equals(TipoPagamento.RINNOVO)){
            oggetto = "Ricevuta di Pagamento Iscrizione";
            messaggio = "In allegato trovi la ricevuta del tuo pagamento di iscrizione.";
        } else if(tipo.equals(TipoPagamento.DONAZIONE)) {
            oggetto = "Grazie per la Sua Donnazione";
            messaggio = "In allegato trova la ricevuta del suo pagamento. Unisp La ringrazzia di coure!.";
        } else {
            oggetto = "Ricevuta di Pagamento";
            messaggio = "In allegato trovi la ricevuta del tuo pagamento.";
        }

        emailSender.inviaEmailGenerico(
                membro.getEmail(),
                membro.getNome(),
                oggetto,
                messaggio,
                ricevutaPdf,
                "ricevuta.pdf" // Nome dell'allegato
        );

        // Crea notifica
        notificheService.creaNotifiche(membro.getId(), messaggio);

        return saved;
    }


/*    @Transactional
    public Pagamenti processaDonazione(Membri membro, BigDecimal importo, String transazioneId) {
        Pagamenti pagamento = new Pagamenti();
        pagamento.setMembro(membro);
        pagamento.setTipoPagamento(TipoPagamento.DONAZIONE);
        pagamento.setImporto(importo);
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setTransazioneId(transazioneId);
        
        return pagamentiRepository.save(pagamento);
    }*/

    public List<Pagamenti> getPagamentiMembro(Long membroId) {
        return pagamentiRepository.findByMembroIdAndIsDeletedFalseOrderByDataPagamentoDesc(membroId);
    }

    public boolean verificaPagamentoIscrizione(Long membroId, int anno) {
        return pagamentiRepository.existsByMembroIdAndTipoAndAnno(membroId, TipoPagamento.ISCRIZIONE.name(), anno);
    }
}
