-- Inserimento dati di esempio per test
INSERT INTO membri (nome, cognome, email, telefono, categoria, stato, codice_fiscale, permesso_soggiorno, passaporto, data_iscrizione)
VALUES 
('Admin', 'System', 'admin@unisp.com', '+39123456789', 'staff', 'attivo', 'ADMN12345678901', true, true, CURRENT_DATE),
('Mario', 'Rossi', 'mario.rossi@email.com', '+39987654321', 'volontario', 'attivo', 'RSSMRA80A01H501U', true, true, CURRENT_DATE),
('Anna', 'Verdi', 'anna.verdi@email.com', '+39456789123', 'passivo', 'attivo', 'VRDNNA85B02H501V', true, true, CURRENT_DATE);

INSERT INTO attivita (titolo, descrizione, data_ora, luogo, num_max_partecipanti)
VALUES 
('Distribuzione Gennaio', 'Prima distribuzione del 2024', '2024-01-15 19:00:00', 'Sede UNISP', 300),
('Distribuzione Febbraio', 'Distribuzione mensile', '2024-02-15 19:00:00', 'Sede UNISP', 300);