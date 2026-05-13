INSERT INTO projects (name, description, status, owner_id, created_at, updated_at)
VALUES (
           'Sample Project',
           'This is a sample project for testing purposes',
           'ACTIVE',
           1,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO tasks (title, description, priority, status, deadline, created_by, project_id, created_at, updated_at)
VALUES
    (
        'Set up CI/CD pipeline',
        'Configure GitHub Actions for automated testing and deployment',
        'HIGH',
        'TODO',
        '2026-06-01 12:00:00',
        1,
        1,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Design database schema',
        'Create ERD and define all tables and relationships',
        'MEDIUM',
        'IN_PROGRESS',
        '2026-05-20 18:00:00',
        1,
        1,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Write unit tests',
        'Write unit tests for all service classes',
        'MEDIUM',
        'TODO',
        '2026-06-10 18:00:00',
        1,
        1,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'Write project documentation',
        'Document all API endpoints and setup instructions in README',
        'LOW',
        'TODO',
        '2026-06-15 18:00:00',
        1,
        1,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );