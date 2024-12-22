package it.unisp.auth;

import it.unisp.enums.CategoriaMembro;
import it.unisp.enums.StatoMembro;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Cognome è obbligatorio")
    private String cognome;

    @NotBlank(message = "Email è obbligatoria")
    @Email(message = "Email non valida")
    private String email;

    private String password;

    private String telefono;

    @Enumerated(EnumType.STRING)
    private CategoriaMembro categoria;

    @Enumerated(EnumType.STRING)
    private StatoMembro stato;

    @NotBlank(message = "Codice Fiscale è obbligatorio")
    private String codiceFiscale;

    private boolean permessoSoggiorno;

    private boolean passaporto;

    private boolean certificatoStudente;

    private boolean dichiarazioneIsee;

    private LocalDate dataCreazione;

    private LocalDate annoScadenzaIscrizione;
}
