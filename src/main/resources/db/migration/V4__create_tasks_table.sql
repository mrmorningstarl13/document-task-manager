create table tasks (
                       id bigserial PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       priority VARCHAR(50) NOT NULL,
                       status VARCHAR(50) NOT NULL,
                       deadline TIMESTAMPTZ NOT NULL,
                       ASSIGNED_TO bigint REFERENCES users(id),
                       CREATED_BY bigint not null references users(id),
                       project_id bigint NOT NULL REFERENCES projects(id),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);