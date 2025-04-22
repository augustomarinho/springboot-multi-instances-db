-- Ensure the `pgcrypto` extension is enabled for UUID generation
CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";


CREATE TABLE users
(
    id          BIGSERIAL PRIMARY KEY,
    external_id UUID         NOT NULL DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    age         INT          NOT NULL
);

-- Add a unique constraint to the `external_id` column
ALTER TABLE users
    ADD CONSTRAINT unique_external_id UNIQUE (external_id);