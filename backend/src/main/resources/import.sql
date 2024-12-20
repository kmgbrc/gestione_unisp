-- Inserimento dei Membri
INSERT INTO membri (id, nome, cognome, email, telefono, categoria, stato, codice_fiscale, permesso_soggiorno, passaporto, certificato_studente, dichiarazione_isee, data_creazione, password, is_deleted)
VALUES
    (1, 'Mario', 'Rossi', 'mario.rossi@example.com', '1234567890', 'ADMIN', 'ATTIVO', 'RSSMRA85M01H501Z', FALSE, TRUE, FALSE, FALSE, CURRENT_DATE, 'password1', FALSE),
    (2, 'Luigi', 'Bianchi', 'luigi.bianchi@example.com', '2345678901', 'STAFF', 'ATTIVO', 'BNCGLU90M01H501Z', TRUE, FALSE, TRUE, FALSE, CURRENT_DATE, 'password2', FALSE),
    (3, 'Anna', 'Verdi', 'anna.verdi@example.com', '3456789012', 'VOLONTARIO', 'INATTIVO', 'VRDNNA85M01H501Z', FALSE, FALSE, TRUE, TRUE, CURRENT_DATE, 'password3', FALSE),
    (4, 'Giulia', 'Neri', 'giulia.neri@example.com', '4567890123', 'PASSIVO', 'ATTIVO', 'NRIGUL80D01H501Z', TRUE, TRUE, FALSE, FALSE, CURRENT_DATE, 'password4', FALSE),
    (5, 'Marco', 'Gialli', 'marco.gialli@example.com', '5678901234', 'STAFF', 'ESCLUSO', 'GLMRMC85M01H501Z', FALSE, FALSE, TRUE, TRUE, CURRENT_DATE, 'password5', FALSE),
    (6, 'Francesca', 'Bluetti', 'francesca.bluetti@example.com', '6789012345', 'VOLONTARIO', 'ATTIVO', 'BLUFNC85F01H501Z', TRUE, TRUE, FALSE, FALSE, CURRENT_DATE, 'password6', FALSE),
    (7, 'Antonio', 'Grigi', 'antonio.grigi@example.com', '7890123456', 'ADMIN', 'INATTIVO', 'GRGNTN85M01H501Z', FALSE, FALSE, TRUE, TRUE, CURRENT_DATE, 'password7', FALSE),
    (8, 'Elena', 'Rossiello', 'elena.rossiello@example.com', '8901234567', 'STAFF', 'ATTIVO', 'RSLNLA90F01H501Z', TRUE, TRUE, FALSE, FALSE, CURRENT_DATE, 'password8', FALSE),
    (9,'Simone','Ferri','simone.ferri@example.com','9012345678','VOLONTARIO','ESCLUSO','FRRSMS80D01H501Z','FALSE','FALSE','TRUE','TRUE',CURRENT_DATE,'password9','FALSE'),
    (10,'Chiara','Verdegaio','chiara.verdegaio@example.com','0123456789','PASSIVO','ATTIVO','VRDGHR85F01H501Z','FALSE','TRUE','FALSE','FALSE',CURRENT_DATE,'password10','FALSE');

-- Inserimento delle Attività
INSERT INTO attivita (id, titolo, descrizione, data_ora, luogo, is_deleted)
VALUES
    (1, 'Corso di Formazione A1', 'Corso di formazione per volontari', '2024-01-15 10:00:00', 'Sala Conferenze A', FALSE),
    (2, 'Evento di Raccolta Fondi', 'Raccolta fondi per beneficenza', '2024-02-20 18:00:00', 'Piazza Centrale', FALSE),
    (3, 'Giornata di Pulizia', 'Pulizia del parco cittadino', '2024-03-05 09:00:00', 'Parco Nazionale', FALSE),
    (4, 'Seminario sulla Salute Mentale', 'Discussione sui temi della salute mentale', '2024-04-10 14:00:00', 'Auditorium Comunale', FALSE),
    (5, 'Festa di Primavera', 'Festa per celebrare l’arrivo della primavera', '2024-05-15 12:00:00', 'Giardino Pubblico', FALSE);

-- Inserimento dei Documenti
INSERT INTO documenti (id, membro_id, tipo, stato, file_path, is_deleted, data_caricamento)
VALUES
    (1, 4, 'certificato_studente', 'APPROVATO', '/path/to/certificato1.pdf', FALSE, CURRENT_DATE),
    (2, 1, 'permesso_soggiorno', 'PENDENTE', '/path/to/permesso1.pdf', FALSE, CURRENT_DATE),
    (3, 1, 'passaporto', 'ARCHIVIATO', '/path/to/passaporto1.pdf', FALSE, CURRENT_DATE),
    (4, 6, 'certificato_studente', 'RIFIUTATO', '/path/to/certificato2.pdf', FALSE, CURRENT_DATE),
    (5, 2, 'certificato_studente', 'APPROVATO', '/path/to/certificato3.pdf', FALSE, CURRENT_DATE);

-- Inserimento delle Notifiche
INSERT INTO notifiche (id, membro_id, contenuto, letto, is_deleted, data_invio)
VALUES
    (1, 4, 'Benvenuto nel programma di volontariato!', FALSE, FALSE, CURRENT_DATE),
    (2, 3, 'Hai un nuovo evento in programma.', FALSE, FALSE, CURRENT_DATE),
    (3, 7, 'Il tuo documento è stato approvato.', FALSE, FALSE, CURRENT_DATE),
    (4, 5, 'Attenzione! Il tuo permesso sta per scadere.', FALSE, FALSE, CURRENT_DATE),
    (5, 1, 'Ricordati di partecipare alla riunione di domani.', FALSE, FALSE, CURRENT_DATE);


-- Inserimento dei Pagamenti
INSERT INTO pagamenti (id,membro_id , tipo_pagamento , importo , transazione_id, is_deleted)
VALUES
    (1 , 4,'ISCRIZIONE' , 50.00 ,'TRANS123456', FALSE),
    (2 , 1,'DONAZIONE' , 100.00 ,'TRANS123457', FALSE),
    (3 , 8,'RINNOVO' , 30.00 ,'TRANS123458', FALSE),
    (4 , 8,'ISCRIZIONE' , 50.00 ,'TRANS123459', FALSE),
    (5 , 2,'DONAZIONE' , 75.00 ,'TRANS123460', FALSE);

-- Inserimento delle Partecipazioni
INSERT INTO partecipazioni (id, membro_id , attivita_id , presente , stato , data_partecipazione, is_deleted)
VALUES
    (1 , 4 , 4 , TRUE ,'ATTIVA' , CURRENT_DATE, FALSE),
    (2 , 8 , 3 , TRUE ,'ATTIVA' , CURRENT_DATE, FALSE),
    (3 , 4 , 1 , FALSE ,'ANNULLATA' , CURRENT_DATE, FALSE),
    (4 , 1 , 5 , TRUE ,'VALIDATA' , CURRENT_DATE, FALSE),
    (5 , 2 , 1 , TRUE ,'ATTIVA' , CURRENT_DATE, FALSE);

-- Inserimento delle Prenotazioni
INSERT INTO prenotazioni (id,numero,membro_id , attivita_id , stato, is_deleted)
VALUES
    (1 , 1 , 2 , 3,'ATTIVA', FALSE ),
    (2 , 2 , 4 , 4, 'ANNULLATA', FALSE),
    (3 , 3 , 8 , 1, 'VALIDATA', FALSE ),
    (4 , 4 , 1 , 5, 'ATTIVA', FALSE ),
    (5 , 5 , 4 , 2, 'ATTIVA', FALSE );

-- Inserimento delle Sessioni
INSERT INTO sessioni (id, membro_id , token)
VALUES
    (1 ,2 , 'EN1234567890' ),
    (2 ,4 , 'TOKEN1234567891' ),
    (3 ,8 , 'TOKEN1234567892' ),
    (4 ,1 , 'TOKEN1234567893' ),
    (5 ,7 ,'TOKEN1234567894' );
