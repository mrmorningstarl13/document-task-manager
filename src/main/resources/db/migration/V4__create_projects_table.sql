create table projects(
    id bigserial primary key,
    name varchar(100) not null,
    description text,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    owner bigserial not null references users(id),
    status varchar(100) not null
);