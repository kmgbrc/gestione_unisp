package it.unisp.service;

import it.unisp.enums.CategoriaMembro;
import it.unisp.enums.StatoDocumento;
import it.unisp.exception.MissingDocumentException;
import it.unisp.model.Documenti;
import it.unisp.model.Membri;
import it.unisp.repository.DocumentiRepository;
import it.unisp.util.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentoService {
    private final DocumentiRepository documentiRepository;
    private final MembriService membriService;
    private final EmailSender emailSender;
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
        }
        return savedDocument;
    }

    public List<Documenti> getDocumentiMembro(Long membroId) {
        return documentiRepository.findByMembroIdAndIsDeletedFalse(membroId);
    }

    @Transactional
    public Documenti approvaDocumento(Long documentoId) {

        return documentiRepository.findById(documentoId)
                .map(doc -> {
                    doc.setStato(StatoDocumento.APPROVATO);

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
                    return documentiRepository.save(doc);
                })
                .orElseThrow(() -> new MissingDocumentException("Documento non trovato"));
    }

    @Transactional
    public Documenti rifiutaDocumento(Long documentoId, String motivazione) {
        return documentiRepository.findById(documentoId)
                .map(doc -> {
                    doc.setStato(StatoDocumento.RIFIUTATO);
                    doc.setNote(motivazione);

                    // Invia l'email al membro
                    String oggetto = "Documento " + doc.getStato().toString();
                    String messaggio = "Il documento di tipo " + doc.getTipo() + " è stato " + doc.getStato() +
                                " per motivo: " + doc.getNote() + ". \nSei pregato di mandarci un documento valido.";

                    emailSender.inviaEmailGenerico(
                            doc.getMembro().getEmail(),
                            doc.getMembro().getNome(),
                            oggetto,
                            messaggio,
                            null,
                            null
                    );
                    return documentiRepository.save(doc);
                })
                .orElseThrow(() -> new MissingDocumentException("Documento non trovato"));
    }
}
