-- V1__Initial_schema.sql
-- This script creates the initial tables for the chat application AND seeds required data.

CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY,
    mobile_number VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255),
    created_at TIMESTAMP
);

-- Create the table correctly with all columns from the beginning
CREATE TABLE message_statuses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    status_order INTEGER NOT NULL
);

-- Seed the initial statuses right after the table is created.
INSERT INTO message_statuses(name, status_order) VALUES
('UNSENT', 0),
('SENT', 1),
('DELIVERED', 2),
('READ', 3);


CREATE TABLE messages (
    id VARCHAR(255) PRIMARY KEY,
    sender_id VARCHAR(255) REFERENCES users(id),
    receiver_id VARCHAR(255) REFERENCES users(id),
    content VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP,
    status_id BIGINT REFERENCES message_statuses(id)
);

CREATE TABLE conversations (
    id BIGSERIAL PRIMARY KEY,
    user1_id VARCHAR(255) NOT NULL REFERENCES users(id),
    user2_id VARCHAR(255) NOT NULL REFERENCES users(id),
    last_message_id VARCHAR(255) REFERENCES messages(id),
    last_updated_at TIMESTAMP,
    UNIQUE (user1_id, user2_id)
);