package it.unisp.service;

import it.unisp.model.Membri;
import it.unisp.model.Pagamenti;
import it.unisp.model.StatoMembro;
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
    public Pagamenti processaPagamentoIscrizione(Membri membro, String transazioneId) {
        Pagamenti pagamento = new Pagamenti();
        pagamento.setMembro(membro);
        pagamento.setTipoPagamento("iscrizione");
        pagamento.setImporto(new BigDecimal("5.00"));
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setTransazioneId(transazioneId);

        Pagamenti saved = pagamentiRepository.save(pagamento);

        membro.setStato(StatoMembro.valueOf("attivo"));

        // Genera ricevuta PDF
        byte[] ricevutaPdf = pdfGenerator.generaRicevutaPagamento(saved);

        // Salva la ricevuta nella cartella documenti/ricevute/
        pdfGenerator.salvaDocumentoInCartella(saved.getTransazioneId(), ricevutaPdf, "ricevuta");

        // Invia email con ricevuta
        String oggettoEmail = "Ricevuta di Pagamento Iscrizione";
        String contenutoEmail = "Gentile " + membro.getNome() + ",\n\nIn allegato trovi la ricevuta del tuo pagamento di iscrizione.";

        emailSender.inviaEmailConAllegato(
                membro.getEmail(),
                oggettoEmail,
                contenutoEmail,
                ricevutaPdf, // I byte della ricevuta PDF
                "ricevuta.pdf" // Nome dell'allegato
        );

        return saved;
    }


    @Transactional
    public Pagamenti processaDonazione(Membri membro, BigDecimal importo, String transazioneId) {
        Pagamenti pagamento = new Pagamenti();
        pagamento.setMembro(membro);
        pagamento.setTipoPagamento("donazione");
        pagamento.setImporto(importo);
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setTransazioneId(transazioneId);
        
        return pagamentiRepository.save(pagamento);
    }

    public List<Pagamenti> getPagamentiMembro(Long membroId) {
        return pagamentiRepository.findByMembroIdAndIsDeletedFalseOrderByDataPagamentoDesc(membroId);
    }

    public boolean verificaPagamentoIscrizione(Long membroId, int anno) {
        return pagamentiRepository.existsByMembroIdAndTipoAndAnno(membroId, "iscrizione", anno);
    }
}
