-- Drop tables if they exist (for clean restart)
DROP TABLE IF EXISTS TASK;
DROP TABLE IF EXISTS SUBJECT;
DROP TABLE IF EXISTS COMPLETE;

-- Create SUBJECT table
CREATE TABLE SUBJECT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create COMPLETE table
CREATE TABLE COMPLETE (
    completed_id INT AUTO_INCREMENT PRIMARY KEY,
    completed VARCHAR(20)
);

-- Create TASK table
CREATE TABLE TASK (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    completed_id INT DEFAULT 1,
    deadline DATE,
    comment VARCHAR(255),
    FOREIGN KEY (subject_id) REFERENCES SUBJECT(id) ON DELETE CASCADE,
    FOREIGN KEY (completed_id) REFERENCES COMPLETE(completed_id)
);

