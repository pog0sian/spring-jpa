CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_roles UNIQUE (user_id, role_id)
);

INSERT INTO roles(name) VALUES ('ROLE_USER')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles(name) VALUES ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;