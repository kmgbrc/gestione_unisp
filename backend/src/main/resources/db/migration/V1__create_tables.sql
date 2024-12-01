-- Already provided in the database schema

-- Additional indexes for performance
CREATE INDEX idx_membri_email ON membri(email);
CREATE INDEX idx_membri_codice_fiscale ON membri(codice_fiscale);
CREATE INDEX idx_attivita_data_ora ON attivita(data_ora);
CREATE INDEX idx_partecipazioni_membro_id ON partecipazioni(membro_id);
CREATE INDEX idx_partecipazioni_attivita_id ON partecipazioni(attivita_id);
CREATE INDEX idx_pagamenti_membro_id ON pagamenti(membro_id);
CREATE INDEX idx_documenti_membro_id ON documenti(membro_id);
CREATE INDEX idx_prenotazioni_attivita_id ON prenotazioni(attivita_id);