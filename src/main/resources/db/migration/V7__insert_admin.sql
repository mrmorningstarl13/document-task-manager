INSERT INTO users (email, password, first_name, last_name, role, is_active, created_at, updated_at)
VALUES (
           'admin@gmail.com',
           '$2a$10$ptPrMszbq.rkcgNL/oA53Obm1dBT9NH45ZRjOxSh9ZG72.Lru.3jW',
           'Admin',
           'Admin',
           'ADMIN',
           true,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );