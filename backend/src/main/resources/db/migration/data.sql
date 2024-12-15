INSERT INTO membri (id, nome, cognome, email, password, telefono, categoria, stato, codice_fiscale, permesso_soggiorno, passaporto, certificato_studente, dichiarazione_isee, data_creazione, is_deleted)
VALUES
    (1, 'Luca', 'Rossi', 'luca.rossi@example.com', 'luca', '1234567890', 'staff', 'attivo', 'RSSLCU80A01H501U', true, true, false, true, '2023-01-10', false),
    (2, 'Maria', 'Bianchi', 'maria.bianchi@example.com', 'maria', '1234567891', 'volontario', 'attivo', 'BNCMRA85B01H501H', false, false, true, false, '2023-02-15', false),
    (3, 'Giovanni', 'Verdi', 'giovanni.verdi@example.com', 'giovanni', '1234567892', 'passivo', 'inattivo', 'VRDGNN70C01H501I', false, false, false, false, '2022-12-05', false),
    (4, 'Sara', 'Neri', 'sara.neri@example.com', 'sara', '1234567893', 'staff', 'attivo', 'NRISRA90D01H501L', true, true, false, true, '2023-03-20', false),
    (5, 'Elena', 'Rossi', 'elena.rossi@example.com', 'elena', '1234567894', 'volontario', 'attivo', 'RSSLEN88E01H501M', false, false, true, false, '2023-01-25', false),
    (6, 'Marco', 'Gialli', 'marco.gialli@example.com', 'marco', '1234567895', 'staff', 'attivo', 'GLLMRC78F01H501N', true, true, true, true, '2023-02-01', false),
    (7, 'Laura', 'Turchi', 'laura.turchi@example.com', 'laura', '1234567896', 'volontario', 'attivo', 'TRCLRA82G01H501O', false, false, true, true, '2023-01-15', false),
    (8, 'Paolo', 'Blu', 'paolo.blu@example.com', 'paolo', '1234567897', 'passivo', 'escluso', 'BLUPLO75H01H501P', false, false, false, false, '2021-09-05', true),
    (9, 'Chiara', 'Verdi', 'chiara.verdi@example.com', 'chiara', '1234567898', 'staff', 'attivo', 'VRDCHR92I01H501Q', true, false, true, true, '2023-04-10', false),
    (10, 'Simone', 'Neri', 'simone.neri@example.com', 'simone', '1234567899', 'volontario', 'attivo', 'NRSMON90L01H501R', false, true, false, false, '2022-10-20', false);

INSERT INTO attivita (id, titolo, descrizione, data_ora, luogo, num_max_partecipanti, is_deleted)
VALUES
    (1, 'Corso di Primo Soccorso', 'Introduzione al primo soccorso per i volontari.', '2024-01-15 10:00:00', 'Sala Formazione 1', 50, false),
    (2, 'Pulizia Spiagge', 'Attività ecologica di pulizia delle spiagge locali.', '2024-02-10 09:00:00', 'Spiaggia Centrale', 200, false),
    (3, 'Cena di Beneficenza', 'Evento annuale per raccolta fondi.', '2024-03-05 20:00:00', 'Hotel Plaza', 100, false),
    (4, 'Assemblea Generale', 'Riunione annuale per tutti i membri.', '2024-03-25 17:00:00', 'Sala Conferenze', 300, false),
    (5, 'Marcia della Pace', 'Evento per promuovere la pace e la solidarietà.', '2024-04-10 15:00:00', 'Centro Storico', 500, false);

INSERT INTO partecipazioni (id, membro_id, attivita_id, presente, stato, delegato_id, data_partecipazione, is_deleted)
VALUES
    (1, 1, 1, true, 'presente', NULL, '2024-01-15 10:00:00', false),
    (2, 2, 1, false, 'assente', NULL, '2024-01-15 10:00:00', false),
    (3, 3, 2, false, 'delegato', 4, '2024-02-10 09:00:00', false),
    (4, 5, 3, true, 'presente', NULL, '2024-03-05 20:00:00', false),
    (5, 6, 4, true, 'presente', NULL, '2024-03-25 17:00:00', false);

INSERT INTO pagamenti (id, membro_id, tipo_pagamento, importo, transazione_id, is_deleted)
VALUES
    (1, 1, 'iscrizione', 50.00, 'txn_001', false),
    (2, 2, 'donazione', 100.00, 'txn_002', false),
    (3, 4, 'iscrizione', 50.00, 'txn_003', false),
    (4, 6, 'donazione', 200.00, 'txn_004', false),
    (5, 7, 'iscrizione', 50.00, 'txn_005', false);

INSERT INTO notifiche (id, membro_id, contenuto, letto, is_deleted)
VALUES
    (1, 1, 'Benvenuto al sistema gestionale.', false, false),
    (2, 2, 'La tua iscrizione è stata approvata.', true, false),
    (3, 4, 'Non dimenticare la prossima riunione.', false, false),
    (4, 5, 'Grazie per la tua donazione!', true, false),
    (5, 7, 'Aggiorna i tuoi documenti ISEE.', false, false);

INSERT INTO sessioni (id, membro_id, token, data_scadenza)
VALUES
    (1, 1, 'token_abc123', '2024-01-20 23:59:59'),
    (2, 2, 'token_xyz456', '2024-01-25 23:59:59'),
    (3, 5, 'token_def789', '2024-01-30 23:59:59');

INSERT INTO prenotazioni (id, numero, membro_id, attivita_id, stato, qr_code, ora_prenotazione, is_deleted)
VALUES
    (1, 1, 1, 1, 'attiva', 'qr_001', '2024-01-01 10:00:00', false),
    (2, 2, 2, 2, 'attiva', 'qr_002', '2024-01-05 11:00:00', false),
    (3, 3, 3, 3, 'annullata', 'qr_003', '2024-01-10 12:00:00', true),
    (4, 4, 5, 4, 'validata', 'qr_004', '2024-01-15 09:30:00', false),
    (5, 5, 6, 5, 'attiva', 'qr_005', '2024-01-20 08:45:00', false);
