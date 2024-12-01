package it.unisp.service;

import it.unisp.model.Membri;
import it.unisp.repository.MembriRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembriService {
    private final MembriRepository membriRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Membri registraMembro(Membri membro) {
        membro.setPassword(passwordEncoder.encode(membro.getPassword()));
        membro.setDataIscrizione(LocalDate.now());
        membro.setDataUltimoRinnovo(LocalDate.now());
        return membriRepository.save(membro);
    }

    public List<Membri> getAllMembri() {
        return membriRepository.findAll();
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

}
