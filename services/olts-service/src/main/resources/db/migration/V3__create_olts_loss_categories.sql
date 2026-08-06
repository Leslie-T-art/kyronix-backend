CREATE TABLE IF NOT EXISTS olts_loss_categories (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL
);
