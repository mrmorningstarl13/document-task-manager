create table files (
                       id BIGSERIAL PRIMARY KEY,
                       name varchar(255) not null,
                       type varchar(255) not null,
                       size varchar(255) not null,
                       owner bigserial not null references users(id),
                       upload_date TIMESTAMP NOT NULL
);