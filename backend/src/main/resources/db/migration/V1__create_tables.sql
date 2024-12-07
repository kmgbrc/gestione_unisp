-- Corriger le type des clés primaires et des références
create table membri
(
    id BIGSERIAL PRIMARY KEY,  -- Passage à BIGSERIAL
    nome                 varchar(50)               not null,
    cognome              varchar(50)               not null,
    email                varchar(100)              not null
        unique,
    password             varchar(255)              not null,
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
                          id BIGSERIAL PRIMARY KEY,  -- Passage à BIGSERIAL
                          titolo VARCHAR(100) NOT NULL,
                          descrizione TEXT,
                          data_ora TIMESTAMP NOT NULL,
                          luogo VARCHAR(100),
                          num_max_partecipanti INT DEFAULT 300,
                          is_deleted BOOLEAN DEFAULT false
);

CREATE TABLE partecipazioni (
                                id BIGSERIAL PRIMARY KEY,  -- Passage à BIGSERIAL
                                membro_id BIGINT REFERENCES membri(id) ON DELETE CASCADE,  -- BIGINT pour clé étrangère
                                attivita_id BIGINT REFERENCES attivita(id) ON DELETE CASCADE,  -- BIGINT pour clé étrangère
                                presente BOOLEAN DEFAULT FALSE,
                                stato VARCHAR(20),
                                delegato_id BIGINT REFERENCES membri(id),  -- BIGINT pour clé étrangère
                                data_partecipazione TIMESTAMP NOT NULL,
                                data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                is_deleted BOOLEAN DEFAULT false
);

CREATE TABLE pagamenti (
                           id BIGSERIAL PRIMARY KEY,  -- Passage à BIGSERIAL
                           membro_id BIGINT REFERENCES membri(id) ON DELETE CASCADE,  -- BIGINT pour clé étrangère
                           tipo_pagamento VARCHAR(20) CHECK (tipo_pagamento IN ('iscrizione', 'donazione')),
                           importo NUMERIC(10, 2) NOT NULL,
                           data_pagamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           transazione_id VARCHAR(100) UNIQUE NOT NULL,
                           is_deleted BOOLEAN DEFAULT false
);

CREATE TABLE notifiche (
                           id BIGSERIAL PRIMARY KEY,  -- Passage à BIGSERIAL
                           membro_id BIGINT REFERENCES membri(id) ON DELETE CASCADE,  -- BIGINT pour clé étrangère
                           contenuto TEXT NOT NULL,
                           data_invio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           letto BOOLEAN DEFAULT FALSE,
                           is_deleted BOOLEAN DEFAULT false
);

CREATE TABLE sessioni (
                          id BIGSERIAL PRIMARY KEY,  -- Passage à BIGSERIAL
                          membro_id BIGINT REFERENCES membri(id) ON DELETE CASCADE,  -- BIGINT pour clé étrangère
                          token VARCHAR(255) NOT NULL,
                          data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          data_scadenza TIMESTAMP
);

CREATE TABLE documenti (
                           id BIGSERIAL PRIMARY KEY,  -- Passage à BIGSERIAL
                           membro_id BIGINT NOT NULL REFERENCES membri (id) ON DELETE CASCADE,  -- BIGINT pour clé étrangère
                           tipo VARCHAR(50) NOT NULL,
                           stato VARCHAR(20) DEFAULT 'pendente',
                           file_path TEXT NOT NULL,
                           data_caricamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           note TEXT,
                           is_deleted BOOLEAN DEFAULT FALSE,
                           CONSTRAINT tipo_documento_check CHECK (tipo IN
                                                                  ('permesso_soggiorno', 'passaporto', 'certificato_studente', 'isee', 'carta_identita'))
);

CREATE TABLE prenotazioni (
                              id BIGSERIAL PRIMARY KEY,  -- Passage à BIGSERIAL
                              numero INT NOT NULL,
                              membro_id BIGINT NOT NULL REFERENCES membri(id) ON DELETE CASCADE,  -- BIGINT pour clé étrangère
                              attivita_id BIGINT NOT NULL REFERENCES attivita(id) ON DELETE CASCADE,  -- BIGINT pour clé étrangère
                              stato VARCHAR(20) NOT NULL CHECK (stato IN ('attiva', 'annullata', 'validata')),
                              qr_code VARCHAR(255) NOT NULL,
                              ora_prenotazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              is_deleted BOOLEAN DEFAULT FALSE
);

-- Ajouter des index pour les performances
CREATE INDEX idx_membri_email ON membri(email);
CREATE INDEX idx_membri_codice_fiscale ON membri(codice_fiscale);
CREATE INDEX idx_attivita_data_ora ON attivita(data_ora);
CREATE INDEX idx_partecipazioni_membro_id ON partecipazioni(membro_id);
CREATE INDEX idx_partecipazioni_attivita_id ON partecipazioni(attivita_id);
CREATE INDEX idx_pagamenti_membro_id ON pagamenti(membro_id);
CREATE INDEX idx_documenti_membro_id ON documenti(membro_id);
CREATE INDEX idx_prenotazioni_attivita_id ON prenotazioni(attivita_id);
