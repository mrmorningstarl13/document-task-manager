create table projects(
    id bigserial primary key,
    name varchar(100) not null,
    description text,
    owner_id bigint not null references users(id),
    status varchar(100) not null default 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);