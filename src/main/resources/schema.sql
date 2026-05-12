-- Drop tables if they exist (for clean restart)
DROP TABLE IF EXISTS TASK;
DROP TABLE IF EXISTS SUBJECT;
DROP TABLE IF EXISTS USERS;

-- Create Users table
CREATE TABLE USERS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(20) NOT NULL DEFAULT 'GENERAL'
);

-- Create SUBJECT table
CREATE TABLE SUBJECT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES USERS (id) ON DELETE CASCADE
);

-- Create TASK table
CREATE TABLE TASK (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL,
    deadline DATE,
    reflection TEXT,
    FOREIGN KEY (subject_id) REFERENCES SUBJECT(id) ON DELETE CASCADE
);

CREATE INDEX idx_subject_id ON SUBJECT(user_id);
CREATE INDEX idx_task_id ON TASK(subject_id);
