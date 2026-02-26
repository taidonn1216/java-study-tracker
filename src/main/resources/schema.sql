-- Drop tables if they exist (for clean restart)
DROP TABLE IF EXISTS TASK;
DROP TABLE IF EXISTS SUBJECT;

-- Create SUBJECT table
CREATE TABLE SUBJECT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create TASK table
CREATE TABLE TASK (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    completed BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) NOT NULL,
    deadline DATE,
    reflection TEXT,
    FOREIGN KEY (subject_id) REFERENCES SUBJECT(id) ON DELETE CASCADE
);
