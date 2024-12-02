package it.unisp.dto.request;
public class PrenotazioneRequest {
    private Long membroId;
    private Long attivitaId;

    // Costruttori, getters e setters

    public PrenotazioneRequest(Long membroId, Long attivitaId) {
        this.membroId = membroId;
        this.attivitaId = attivitaId;
    }

    public Long getMembroId() {
        return membroId;
    }

    public Long getAttivitaId() {
        return attivitaId;
    }
}
