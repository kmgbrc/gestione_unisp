-- Already provided in the database schema
create table membri
(
    id                   bigserial
        primary key,
    nome                 varchar(50)               not null,
    cognome              varchar(50)               not null,
    email                varchar(100)              not null
        unique,
    password              varchar(255)               not null,
    telefono             varchar(20),
    categoria            varchar(20)
        constraint membri_categoria_check
            check ((categoria)::text = ANY
                   ((ARRAY ['staff'::character varying, 'volontario'::character varying, 'passivo'::character varying])::text[])),
    stato                varchar(20) default 'attivo'::character varying
        constraint membri_stato_check
            check ((stato)::text = ANY
                   ((ARRAY ['attivo'::character varying, 'inattivo'::character varying, 'escluso'::character varying])::text[])),
    codice_fiscale       varchar(16)               not null,
    permesso_soggiorno   boolean     default false not null,
    passaporto           boolean     default false not null,
    certificato_studente boolean     default false,
    dichiarazione_isee   boolean     default false,
    data_iscrizione      date        default CURRENT_DATE,
    data_ultimo_rinnovo  date,
    is_deleted           boolean     default false
);
CREATE TABLE attivita (
                          id SERIAL PRIMARY KEY,
                          titolo VARCHAR(100) NOT NULL,
                          descrizione TEXT,
                          data_ora TIMESTAMP NOT NULL,
                          luogo VARCHAR(100),
                          num_max_partecipanti INT DEFAULT 300,
                          is_deleted           boolean     default false
);
CREATE TABLE partecipazioni (
                                id SERIAL PRIMARY KEY,
                                membro_id INT REFERENCES membri(id) ON DELETE CASCADE,
                                attivita_id INT REFERENCES attivita(id) ON DELETE CASCADE,
                                presente BOOLEAN DEFAULT FALSE,  -- Rappresenta la presenza alla attività (true = presente, false = assente)
                                stato VARCHAR(20),  -- Stato della partecipazione: "presente", "assente", "delegato"
                                delegato_id INT REFERENCES membri(id),  -- Se delegato, fa riferimento al membro che riceve la delega
                                data_partecipazione TIMESTAMP NOT NULL,  -- Data della partecipazione (per tracciare l'anno di riferimento)
                                data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data di creazione della partecipazione
                                is_deleted           boolean     default false
);

CREATE TABLE pagamenti (
                           id SERIAL PRIMARY KEY,
                           membro_id INT REFERENCES membri(id) ON DELETE CASCADE,
                           tipo_pagamento VARCHAR(20) CHECK (tipo_pagamento IN ('iscrizione', 'donazione')),
                           importo NUMERIC(10, 2) NOT NULL,
                           data_pagamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           transazione_id VARCHAR(100) UNIQUE NOT NULL,
                           is_deleted           boolean     default false
);
CREATE TABLE notifiche (
                           id SERIAL PRIMARY KEY,
                           membro_id INT REFERENCES membri(id) ON DELETE CASCADE,
                           contenuto TEXT NOT NULL,
                           data_invio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           letto BOOLEAN DEFAULT FALSE,
                           is_deleted           boolean     default false
);
CREATE TABLE sessioni (
                          id SERIAL PRIMARY KEY,
                          membro_id INT REFERENCES membri(id) ON DELETE CASCADE,
                          token VARCHAR(255) NOT NULL,
                          data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          data_scadenza TIMESTAMP
);
CREATE TABLE documenti (
                           id SERIAL PRIMARY KEY,                    -- Identificativo unico del documento
                           membro_id BIGINT NOT NULL,                -- Collegamento al membro
                           tipo VARCHAR(50) NOT NULL,                -- Tipo di documento (es. passaporto, permesso di soggiorno)
                           stato VARCHAR(20) DEFAULT 'pendente',     -- Stato del documento: pendente, approvato, rifiutato
                           file_path TEXT NOT NULL,                  -- Percorso del file nel sistema di archiviazione
                           data_caricamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data di caricamento
                           note TEXT,                                -- Note aggiuntive (es. motivazioni rifiuto)
                           is_deleted BOOLEAN DEFAULT FALSE,         -- Soft delete
                           CONSTRAINT fk_membro FOREIGN KEY (membro_id)
                               REFERENCES membri (id) ON DELETE CASCADE,
                           CONSTRAINT tipo_documento_check CHECK (tipo IN
                                                                  ('permesso_soggiorno', 'passaporto', 'certificato_studente', 'isee', 'carta_identita'))
);

CREATE TABLE prenotazioni (
                              id SERIAL PRIMARY KEY,
                              numero INT NOT NULL,
                              membro_id INT NOT NULL,
                              attivita_id INT NOT NULL,
                              stato VARCHAR(20) NOT NULL CHECK (stato IN ('attiva', 'annullata', 'validata')), -- Contrainte CHECK
                              qr_code VARCHAR(255) NOT NULL,
                              ora_prenotazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              is_deleted BOOLEAN DEFAULT FALSE,
                              CONSTRAINT fk_membro FOREIGN KEY (membro_id) REFERENCES membri(id) ON DELETE CASCADE,
                              CONSTRAINT fk_attivita FOREIGN KEY (attivita_id) REFERENCES attivita(id) ON DELETE CASCADE
);
-- Additional indexes for performance
CREATE INDEX idx_membri_email ON membri(email);
CREATE INDEX idx_membri_codice_fiscale ON membri(codice_fiscale);
CREATE INDEX idx_attivita_data_ora ON attivita(data_ora);
CREATE INDEX idx_partecipazioni_membro_id ON partecipazioni(membro_id);
CREATE INDEX idx_partecipazioni_attivita_id ON partecipazioni(attivita_id);
CREATE INDEX idx_pagamenti_membro_id ON pagamenti(membro_id);
CREATE INDEX idx_documenti_membro_id ON documenti(membro_id);
CREATE INDEX idx_prenotazioni_attivita_id ON prenotazioni(attivita_id);
