package it.unisp.service;

import it.unisp.model.Membri;
import it.unisp.model.Pagamenti;
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

    @Transactional
    public Pagamenti processaPagamentoIscrizione(Membri membro, String transazioneId) {
        Pagamenti pagamento = new Pagamenti();
        pagamento.setMembro(membro);
        pagamento.setTipoPagamento("iscrizione");
        pagamento.setImporto(new BigDecimal("5.00"));
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setTransazioneId(transazioneId);

        Pagamenti saved = pagamentiRepository.save(pagamento);
        
        // Genera ricevuta PDF
        byte[] ricevutaPdf = pdfGenerator.generaRicevutaPagamento(saved);
        
        // Invia email con ricevuta
        emailSender.inviaRicevutaPagamento(membro.getEmail(), ricevutaPdf);
        
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
