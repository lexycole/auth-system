CREATE TABLE IF NOT EXISTS users (
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT      NOT NULL,
    role    VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Seed: password is BCrypt of "password123"
-- Use DO block or separate INSERT with ON CONFLICT for PostgreSQL
INSERT INTO users (username, password) 
VALUES 
    ('alice', ''),
    ('admin', '')
ON CONFLICT (username) DO NOTHING;

-- For user_roles, you need to get the actual IDs after insert
-- Option 1: Use CTE to get IDs
WITH inserted_users AS (
    SELECT id, username FROM users WHERE username IN ('alice', 'admin')
)
INSERT INTO user_roles (user_id, role)
SELECT id, role FROM inserted_users, (VALUES ('ROLE_USER'), ('ROLE_USER'), ('ROLE_ADMIN')) AS roles(role)
WHERE username = 'alice' AND role = 'ROLE_USER'
   OR username = 'admin' AND role IN ('ROLE_USER', 'ROLE_ADMIN')
ON CONFLICT DO NOTHING;