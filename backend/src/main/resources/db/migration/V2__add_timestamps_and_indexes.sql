-- Add timestamps to membri table
ALTER TABLE membri 
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add timestamps to attivita table
ALTER TABLE attivita 
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add timestamps to partecipazioni table
ALTER TABLE partecipazioni 
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add indexes for frequently queried columns
CREATE INDEX idx_membri_email ON membri(email);
CREATE INDEX idx_membri_codice_fiscale ON membri(codice_fiscale);
CREATE INDEX idx_attivita_data_ora ON attivita(data_ora);
CREATE INDEX idx_partecipazioni_membro_id ON partecipazioni(membro_id);
CREATE INDEX idx_partecipazioni_attivita_id ON partecipazioni(attivita_id);

-- Convert status fields to enum types
CREATE TYPE member_category_enum AS ENUM ('STAFF', 'VOLONTARIO', 'PASSIVO');
CREATE TYPE member_status_enum AS ENUM ('ATTIVO', 'INATTIVO', 'ESCLUSO');

-- Update membri table to use enum types
ALTER TABLE membri 
ALTER COLUMN categoria TYPE member_category_enum USING categoria::member_category_enum,
ALTER COLUMN stato TYPE member_status_enum USING stato::member_status_enum;

-- Add trigger for updating updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for all tables
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