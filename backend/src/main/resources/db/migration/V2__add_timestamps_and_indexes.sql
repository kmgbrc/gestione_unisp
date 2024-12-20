-- Aggiunta di colonne per i timestamp e creazione degli indici

-- Tabella Membri
ALTER TABLE membri
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Indici per la tabella Membri
CREATE INDEX idx_membri_categoria ON membri(categoria);
CREATE INDEX idx_membri_stato ON membri(stato);
CREATE INDEX idx_membri_data_creazione ON membri(data_creazione);

-- Tabella Attivita
ALTER TABLE attivita
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Indici per la tabella Attivita
CREATE INDEX idx_attivita_data_ora ON attivita(data_ora);
CREATE INDEX idx_attivita_luogo ON attivita(luogo);

-- Tabella Partecipazioni
ALTER TABLE partecipazioni
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Indici per la tabella Partecipazioni
CREATE INDEX idx_partecipazioni_membro_id ON partecipazioni(membro_id);
CREATE INDEX idx_partecipazioni_attivita_id ON partecipazioni(attivita_id);
CREATE INDEX idx_partecipazioni_data_partecipazione ON partecipazioni(data_partecipazione);

-- Tabella Pagamenti
ALTER TABLE pagamenti
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Indici per la tabella Pagamenti
CREATE INDEX idx_pagamenti_membro_id ON pagamenti(membro_id);
CREATE INDEX idx_pagamenti_data_pagamento ON pagamenti(data_pagamento);

-- Tabella Notifiche
ALTER TABLE notifiche
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Indici per la tabella Notifiche
CREATE INDEX idx_notifiche_membro_id ON notifiche(membro_id);
CREATE INDEX idx_notifiche_letto ON notifiche(letto);

-- Tabella Sessioni
ALTER TABLE sessioni
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Indici per la tabella Sessioni
CREATE INDEX idx_sessioni_membro_id ON sessioni(membro_id);

-- Tabella Documenti
ALTER TABLE documenti
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Indici per la tabella Documenti
CREATE INDEX idx_documenti_membro_id ON documenti(membro_id);
CREATE INDEX idx_documenti_tipo ON documenti(tipo);

-- Tabella Prenotazioni
ALTER TABLE prenotazioni
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Indici per la tabella Prenotazioni
CREATE INDEX idx_prenotazioni_membro_id ON prenotazioni(membro_id);
CREATE INDEX idx_prenotazioni_attivita_id ON prenotazioni(attivita_id);
CREATE INDEX idx_prenotazioni_stato ON prenotazioni(stato);

-- Funzione e trigger per aggiornare il timestamp di aggiornamento
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$
    LANGUAGE plpgsql;

-- Creazione dei trigger per tutte le tabelle
CREATE TRIGGER update_membri_updated_at
    BEFORE UPDATE ON membri
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_attivita_updated_at
    BEFORE UPDATE ON attivita
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_partecipazioni_updated_at
    BEFORE UPDATE ON partecipazioni
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_pagamenti_updated_at
    BEFORE UPDATE ON pagamenti
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_notifiche_updated_at
    BEFORE UPDATE ON notifiche
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_sessioni_updated_at
    BEFORE UPDATE ON sessioni
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_documenti_updated_at
    BEFORE UPDATE ON documenti
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_prenotazioni_updated_at
    BEFORE UPDATE ON prenotazioni
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
