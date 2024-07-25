CREATE TABLE IF NOT EXISTS clients_data
(
    id             SERIAL PRIMARY KEY,
    client_id      VARCHAR(255) NOT NULL,
    first_name     VARCHAR(255) NOT NULL,
    last_name      VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL,
    phone_number   VARCHAR(255) NOT NULL,
    address        VARCHAR(255) NOT NULL,
    card_numbers   JSONB        NOT NULL,
    saved_contacts JSONB        NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_client_id ON clients_data (client_id);