package it.unisp.dto.request;

public class PrenotazioneRequest {

    private Long membroId;
    private Long attivitaId;
    private Long delegatoId;

    // Costruttore senza argomenti richiesto da Jackson
    public PrenotazioneRequest() {
    }

    public PrenotazioneRequest(Long membroId, Long attivitaId, Long delegatoId) {
        this.membroId = membroId;
        this.attivitaId = attivitaId;
        this.delegatoId = delegatoId;
    }

    public Long getMembroId() {
        return membroId;
    }

    public void setMembroId(Long membroId) {
        this.membroId = membroId;
    }

    public Long getAttivitaId() {
        return attivitaId;
    }

    public void setAttivitaId(Long attivitaId) {
        this.attivitaId = attivitaId;
    }

    public Long getDelegatoId() {
        return delegatoId;
    }

    public void setDelegatoId(Long delegatoId) {
        this.delegatoId = delegatoId;
    }

    @Override
    public String toString() {
        return "PrenotazioneRequest{" +
                "membroId=" + membroId +
                ", attivitaId=" + attivitaId +
                ", delegatoId=" + delegatoId +
                '}';
    }
}
