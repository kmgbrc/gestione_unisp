package it.unisp.service;

import ch.qos.logback.classic.Logger;
import it.unisp.enums.CategoriaMembro;
import it.unisp.enums.StatoDocumento;
import it.unisp.exception.MissingDocumentException;
import it.unisp.model.Documenti;
import it.unisp.model.Membri;
import it.unisp.model.Sessione;
import it.unisp.repository.DocumentiRepository;
import it.unisp.repository.SessioneRepository;
import it.unisp.util.EmailSender;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentoService {
    private final DocumentiRepository documentiRepository;
    private final MembriService membriService;
    private final EmailSender emailSender;
    private  final SessioneRepository sessioneRepository;
    private  final NotificheService notificheService;
    Logger logger = null;
    private static final String UPLOAD_DIR = "documenti/uploads/";

    @Transactional
    public Documenti caricaDocumento(Long membroId, String tipo, MultipartFile file) throws IOException {
        String fileName = String.format("%d_%s_%s", membroId, tipo, file.getOriginalFilename());
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        Documenti documento = Documenti.builder()
                .membro(membriService.findByMembroIdAndIsDeletedFalse(membroId))
                .tipo(tipo)
                .filePath(fileName)
                .stato(StatoDocumento.PENDENTE)
                .dataCaricamento(LocalDateTime.now())
                .note("")
                .build();

        Documenti savedDocument = documentiRepository.save(documento);

        // Invia l'email al membro
        String oggetto = "Caricamento Documento - " + tipo;
        String messaggio = "Il documento di tipo " + tipo + " è stato caricato con successo pero con stato: " +
                savedDocument.getStato() + ". \nRiceverai una mail appena verrà approvato o rifiutato";

        emailSender.inviaEmailGenerico(
                savedDocument.getMembro().getEmail(),
                savedDocument.getMembro().getNome(),
                oggetto,
                messaggio,
                null,
                null
        );

        // Crea notifica
        notificheService.creaNotifiche(savedDocument.getMembro().getId(), messaggio, oggetto);

        // Invia l'email agli admin
        List<Membri> allAdmin = membriService.getMembriAdmin();
        for (Membri ogniAdmin : allAdmin) {
            oggetto = "CONTROLLO DOCUMENTO - " + tipo;
            messaggio = "Il documento di tipo " + tipo + " è stato caricato con successo dal membro: " +
                    savedDocument.getMembro().getNome() + " " + savedDocument.getMembro().getCognome() +
                    ". \nDevi dare un occhiata per controllare che i dati corrispondono, e poi approvare o rifiutare il documento.";

            emailSender.inviaEmailGenerico(
                    ogniAdmin.getEmail(),
                    ogniAdmin.getNome(),
                    oggetto,
                    messaggio,
                    null,
                    null
            );

            // Crea notifica
            notificheService.creaNotifiche(ogniAdmin.getId(), messaggio, oggetto);
        }
        return savedDocument;
    }

    public List<Documenti> getDocumentiMembro(Long membroId) {
        return documentiRepository.findByMembroIdAndIsDeletedFalse(membroId);
    }

    @Transactional
    public Documenti approvaDocumento(Long documentoId, HttpServletRequest request) {
        try {
            // Recupera il token dai cookie
            String token = Arrays.stream(request.getCookies())
                    .filter(cookie -> "tokenUnisp".equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElseThrow(() -> new RuntimeException("Token non trovato"));

            Sessione sessione = sessioneRepository.findByToken(token);

            return documentiRepository.findById(documentoId)
                    .map(doc -> {
                        doc.setStato(StatoDocumento.APPROVATO);
                        doc.setNote("Approvato da: " + sessione.getMembro().getNome() + " " + sessione.getMembro().getCognome());

                        // Invia l'email al membro
                        String oggetto = "Documento " + doc.getStato().toString();
                        String messaggio = "Il documento di tipo " + doc.getTipo() + " è stato " +
                                doc.getStato().toString();

                        emailSender.inviaEmailGenerico(
                                doc.getMembro().getEmail(),
                                doc.getMembro().getNome(),
                                oggetto,
                                messaggio,
                                null,
                                null
                        );

                        // Crea notifica
                        notificheService.creaNotifiche(doc.getMembro().getId(), messaggio, oggetto);

                        return documentiRepository.save(doc);
                    })
                    .orElseThrow(() -> new MissingDocumentException("Documento non trovato"));
        } catch (MissingDocumentException e) {
            // Log dell'errore
            logger.error("Errore durante l'approvazione del documento: {}", e.getMessage());
            throw e; // Rilancia l'eccezione per gestirla a livello superiore
        } catch (Exception e) {
            // Gestione di altre eccezioni generali
            logger.error("Errore imprevisto durante l'approvazione del documento: {}", e.getMessage());
            throw new RuntimeException("Errore durante l'approvazione del documento", e);
        }
    }


    @Transactional
    public Documenti rifiutaDocumento(Long documentoId, String motivazione, HttpServletRequest request) {

        // Recupera il token dai cookie
        String token = Arrays.stream(request.getCookies())
                .filter(cookie -> "tokenUnisp".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("Token non trovato"));

        Sessione sessione = sessioneRepository.findByToken(token);

        return documentiRepository.findById(documentoId)
                .map(doc -> {
                    doc.setStato(StatoDocumento.RIFIUTATO);
                    doc.setNote("Rifiutato da: " + sessione.getMembro().getNome() + " " + sessione.getMembro().getCognome());
                    doc.setDeleted(true);

                    // Invia l'email al membro
                    String oggetto = "Documento " + doc.getStato().toString();
                    String messaggio = "Il documento di tipo " + doc.getTipo() + " è stato " + doc.getStato() +
                                " per motivo: " + motivazione + ". \nSei pregato di mandarci un documento valido.";

                    emailSender.inviaEmailGenerico(
                            doc.getMembro().getEmail(),
                            doc.getMembro().getNome(),
                            oggetto,
                            messaggio,
                            null,
                            null
                    );
                    // Crea notifica
                    notificheService.creaNotifiche(doc.getMembro().getId(), messaggio, oggetto);

                    return documentiRepository.save(doc);
                })
                .orElseThrow(() -> new MissingDocumentException("Documento non trovato"));
    }
}
