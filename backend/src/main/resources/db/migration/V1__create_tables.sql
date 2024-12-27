-- Table: attivita
CREATE TABLE attivita (
                          id BIGSERIAL PRIMARY KEY,
                          titolo VARCHAR NOT NULL,
                          descrizione VARCHAR,
                          data_ora TIMESTAMP NOT NULL,
                          luogo VARCHAR,
                          num_max_partecipanti INTEGER DEFAULT 300,
                          is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Table: membri
CREATE TABLE membri (
                        id BIGSERIAL PRIMARY KEY,
                        nome VARCHAR NOT NULL,
                        cognome VARCHAR NOT NULL,
                        email VARCHAR UNIQUE NOT NULL,
                        telefono VARCHAR,
                        categoria VARCHAR,
                        stato VARCHAR,
                        codice_fiscale VARCHAR NOT NULL,
                        permesso_soggiorno BOOLEAN NOT NULL,
                        passaporto BOOLEAN,
                        certificato_studente BOOLEAN,
                        dichiarazione_isee BOOLEAN,
                        data_creazione DATE NOT NULL DEFAULT CURRENT_DATE,
                        anno_scadenza_iscrizione INTEGER,
                        is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                        password VARCHAR NOT NULL
);

-- Table: documenti
CREATE TABLE documenti (
                           id BIGSERIAL PRIMARY KEY,
                           membro_id BIGINT NOT NULL REFERENCES membri(id) ON DELETE CASCADE,
                           tipo VARCHAR,
                           stato VARCHAR,
                           file_path VARCHAR NOT NULL,
                           data_caricamento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           note VARCHAR,
                           is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Table: notifiche
CREATE TABLE notifiche (
                           id BIGSERIAL PRIMARY KEY,
                           membro_id BIGINT NOT NULL REFERENCES membri(id) ON DELETE CASCADE,
                           contenuto VARCHAR NOT NULL,
                           titolo VARCHAR NOT NULL,
                           data_invio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           letto BOOLEAN NOT NULL DEFAULT FALSE,
                           is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Table: pagamenti
CREATE TABLE pagamenti (
                           id BIGSERIAL PRIMARY KEY,
                           membro_id BIGINT NOT NULL REFERENCES membri(id) ON DELETE CASCADE,
                           tipo_pagamento VARCHAR NOT NULL,
                           importo NUMERIC(10, 2),
                           data_pagamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           transazione_id VARCHAR UNIQUE,
                           is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Table: partecipazioni
CREATE TABLE partecipazioni (
                                id BIGSERIAL PRIMARY KEY,
                                membro_id BIGINT NOT NULL REFERENCES membri(id) ON DELETE CASCADE,
                                attivita_id BIGINT NOT NULL REFERENCES attivita(id) ON DELETE CASCADE,
                                presente BOOLEAN,
                                delegato_id BIGINT REFERENCES membri(id),
                                data_partecipazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Table: prenotazioni
CREATE TABLE prenotazioni (
                              id BIGSERIAL PRIMARY KEY,
                              numero INTEGER,
                              membro_id BIGINT NOT NULL REFERENCES membri(id) ON DELETE CASCADE,
                              attivita_id BIGINT NOT NULL REFERENCES attivita(id) ON DELETE CASCADE,
                              delegato_id BIGINT REFERENCES membri(id),
                              stato VARCHAR,
                              qr_code VARCHAR,
                              ora_prenotazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Table: sessioni
CREATE TABLE sessioni (
                          id BIGSERIAL PRIMARY KEY,
                          membro_id BIGINT NOT NULL REFERENCES membri(id) ON DELETE CASCADE,
                          token VARCHAR NOT NULL,
                          data_scadenza TIMESTAMP
);
