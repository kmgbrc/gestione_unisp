-- Tabella Membri
CREATE TABLE membri (
                        id BIGSERIAL PRIMARY KEY,
                        nome VARCHAR(50) NOT NULL,
                        cognome VARCHAR(50) NOT NULL,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        telefono VARCHAR(20),
                        categoria VARCHAR(50),
                        stato VARCHAR(50),
                        codice_fiscale VARCHAR(16) NOT NULL,
                        permesso_soggiorno BOOLEAN DEFAULT FALSE,
                        passaporto BOOLEAN DEFAULT FALSE,
                        certificato_studente BOOLEAN DEFAULT FALSE,
                        dichiarazione_isee BOOLEAN DEFAULT FALSE,
                        data_creazione DATE DEFAULT CURRENT_DATE, -- Valore predefinito
                        data_ultimo_rinnovo DATE,
                        is_deleted BOOLEAN DEFAULT FALSE, -- Valore predefinito
                        password VARCHAR(255) NOT NULL
);

-- Tabella Attivita
CREATE TABLE attivita (
                          id BIGSERIAL PRIMARY KEY,
                          titolo VARCHAR(100) NOT NULL,
                          descrizione TEXT,
                          data_ora TIMESTAMP NOT NULL,
                          luogo VARCHAR(100),
                          num_max_partecipanti INT DEFAULT 300, -- Valore predefinito
                          is_deleted BOOLEAN DEFAULT FALSE -- Valore predefinito
);

-- Tabella Documenti
CREATE TABLE documenti (
                           id BIGSERIAL PRIMARY KEY,
                           membro_id BIGINT REFERENCES membri(id) ON DELETE CASCADE NOT NULL,
                           tipo VARCHAR(50),
                           stato VARCHAR(50),
                           file_path TEXT NOT NULL,
                           data_caricamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Valore predefinito
                           note TEXT,
                           is_deleted BOOLEAN DEFAULT FALSE -- Valore predefinito
);

-- Tabella Notifiche
CREATE TABLE notifiche (
                           id BIGSERIAL PRIMARY KEY,
                           membro_id BIGINT REFERENCES membri(id) ON DELETE CASCADE NOT NULL,
                           contenuto TEXT NOT NULL,
                           data_invio TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Valore predefinito
                           letto BOOLEAN DEFAULT FALSE, -- Valore predefinito
                           is_deleted BOOLEAN DEFAULT FALSE -- Valore predefinito
);

-- Tabella Pagamenti
CREATE TABLE pagamenti (
                           id BIGSERIAL PRIMARY KEY,
                           membro_id BIGINT REFERENCES membri(id) ON DELETE CASCADE NOT NULL,
                           tipo_pagamento VARCHAR(50),
                           importo NUMERIC(10, 2) NOT NULL,
                           data_pagamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Valore predefinito
                           transazione_id VARCHAR(100) UNIQUE NOT NULL,
                           is_deleted BOOLEAN DEFAULT FALSE -- Valore predefinito
);

-- Tabella Partecipazioni
CREATE TABLE partecipazioni (
                                id BIGSERIAL PRIMARY KEY,
                                membro_id BIGINT REFERENCES membri(id) ON DELETE CASCADE NOT NULL,
                                attivita_id BIGINT REFERENCES attivita(id) ON DELETE CASCADE NOT NULL,
                                presente BOOLEAN DEFAULT FALSE, -- Valore predefinito
                                stato VARCHAR(20),
                                delegato_id BIGINT REFERENCES membri(id),
                                data_partecipazione TIMESTAMP NOT NULL,
                                data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Valore predefinito
                                is_deleted BOOLEAN DEFAULT FALSE -- Valore predefinito
);

-- Tabella Prenotazioni
CREATE TABLE prenotazioni (
                              id BIGSERIAL PRIMARY KEY,
                              numero INT NOT NULL,
                              membro_id BIGINT REFERENCES membri(id) ON DELETE CASCADE NOT NULL,
                              attivita_id BIGINT REFERENCES attivita(id) ON DELETE CASCADE NOT NULL,
                              delegato_id BIGINT REFERENCES membri(id),
                              stato VARCHAR(50),
                              qr_code VARCHAR(255),
                              ora_prenotazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Valore predefinito
                              is_deleted BOOLEAN DEFAULT FALSE -- Valore predefinito
);

-- Tabella Sessioni
CREATE TABLE sessioni (
                          id BIGSERIAL PRIMARY KEY,
                          membro_id BIGINT REFERENCES membri(id) ON DELETE CASCADE NOT NULL,
                          token VARCHAR(255) NOT NULL,
                          data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Valore predefinito
                          data_scadenza TIMESTAMP
);
