package it.unisp.service;

import it.unisp.model.Membri;
import it.unisp.repository.MembriRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembriService {
    private final MembriRepository membriRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private static final Logger logger = LoggerFactory.getLogger(MembriService.class);

    @Transactional
    public Membri registraMembro(Membri membro) {
        logger.info("Inizio della registrazione del membro: {}", membro.getEmail());

        // Controllo se l'email esiste già
        if (membriRepository.existsByEmail(membro.getEmail())) {
            logger.error("Tentativo di registrazione fallito: l'email {} esiste già.", membro.getEmail());
            throw new RuntimeException("L'email esiste già.");
        }

        // Validazione dei dati del membro
        Set<ConstraintViolation<Membri>> violations = validator.validate(membro);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            logger.error("Errore di validazione durante la registrazione del membro: {}", errorMessage);
            throw new RuntimeException("Errore di validazione: " + errorMessage);
        }

        // Codifica della password
        membro.setPassword(passwordEncoder.encode(membro.getPassword()));
        logger.info("Password per l'email {} è stata codificata.", membro.getEmail());

        // Imposta le date di iscrizione e ultimo rinnovo
        membro.setDataIscrizione(LocalDate.now());
        membro.setDataUltimoRinnovo(LocalDate.now());
        logger.info("Data di iscrizione e ultimo rinnovo impostate per il membro: {}", membro.getEmail());

        try {
            Membri savedMembro = membriRepository.save(membro);
            logger.info("Membro registrato con successo: {}", savedMembro.getEmail());
            return savedMembro;
        } catch (Exception e) {
            logger.error("Errore durante il salvataggio del membro: {}", e.getMessage());
            throw new RuntimeException("Errore di database.");
        }
    }

    public List<Membri> getAllMembri() {
        return membriRepository.findAll();
    }
    public List<Membri> findByIsDeletedFalse() {
        return membriRepository.findByIsDeletedFalse();
    }

    public Membri getMembroById(Long membroId) {
        return membriRepository.findByIdAndIsDeletedFalse(membroId);
    }

    @Transactional
    public Membri updateMembro(Long id, Membri membro) {
        return membriRepository.findById(id)
            .map(esistente -> {
                esistente.setNome(membro.getNome());
                esistente.setCognome(membro.getCognome());
                esistente.setTelefono(membro.getTelefono());
                esistente.setStato(membro.getStato());
                return membriRepository.save(esistente);
            })
            .orElseThrow(() -> new RuntimeException("Membro non trovato"));
    }

    @Transactional
    public void deleteMembro(Long id) {
        membriRepository.findById(id)
            .ifPresent(membro -> {
                membro.setDeleted(true);
                membriRepository.save(membro);
            });
    }

    public Membri findByMembroIdAndIsDeletedFalse(Long membroId) {
        return membriRepository.findByIdAndIsDeletedFalse(membroId);
    }

    public List<Membri> getMembriConIscrizioneInScadenza() {
        LocalDate oggi = LocalDate.now();
        LocalDate dataScadenza = oggi.plusDays(30); // Consideriamo le iscrizioni in scadenza entro 30 giorni

        return membriRepository.findByDataUltimoRinnovoBetween(oggi, dataScadenza);
    }

    public List<Membri> getMembriConDocumentiMancanti() {
        // Recupera tutti i membri
        List<Membri> tuttiIMembri = membriRepository.findAll();

        // Filtra i membri che hanno almeno un documento mancante
        return tuttiIMembri.stream()
                .filter(membro ->
                        !membro.isPermessoSoggiorno() ||
                                !membro.isPassaporto() ||
                                !membro.isCertificatoStudente() ||
                                !membro.isDichiarazioneIsee()
                )
                .collect(Collectors.toList());
    }
}
