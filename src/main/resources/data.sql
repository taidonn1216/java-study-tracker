
-- 既存ユーザー (GENERAL)
INSERT INTO USERS (username, password, enabled, role) VALUES('user', '$2b$10$8gl4P.2H1sEOH0mU8moubOHvLxrfqr6AKtJ326H3CbgJ2dikD9/ma', TRUE, 'GENERAL');
INSERT INTO USERS (username, password, enabled, role) VALUES('user2', '$2a$08$FtqN7OP9/OE9HKI243/ft.GFmdK3nfsqE/Yk9EXopYeU7QRBtrjhO', TRUE, 'GENERAL');

-- 管理者ユーザー (ADMIN)
INSERT INTO USERS (username, password, enabled, role) VALUES('admin','$2y$10$bn2HkNx8BZpYrbc9nhm05e0VMKjHBDixsCwxwcTN3UdCmTMU2L6tm', TRUE, 'ADMIN');

-- Insert sample subjects
INSERT INTO SUBJECT (name, user_id) VALUES ('Java',1);
INSERT INTO SUBJECT (name, user_id) VALUES ('データベース',1);
INSERT INTO SUBJECT (name, user_id) VALUES ('Spring Framework',2);

-- Insert sample tasks for Java (subject_id = 1, user_id = 1)
INSERT INTO TASK (subject_id, title, status, deadline, reflection) VALUES (1, '第1章: Java基礎を読む', 'DONE' , '2026-02-26', '');
INSERT INTO TASK (subject_id, title, status, deadline, reflection) VALUES (1, '第2章: オブジェクト指向を学ぶ', 'DONE', '2026-02-26', '');
INSERT INTO TASK (subject_id, title, status, deadline, reflection) VALUES (1, '第3章: コレクションフレームワーク', 'DONE', '2026-02-26', '');

-- Insert sample tasks for データベース (subject_id = 2, user_id = 1)
INSERT INTO TASK (subject_id, title, status, deadline, reflection) VALUES (2, 'SQL基礎: SELECT文を理解する', 'DONE', '2026-02-26', '');
INSERT INTO TASK (subject_id, title, status, deadline, reflection) VALUES (2, 'JOINの種類を学習する', 'DONE', '2026-02-26', '');
INSERT INTO TASK (subject_id, title, status, deadline, reflection) VALUES (2, 'トランザクション処理を理解する', 'DONE', '2026-02-26','');

-- Insert sample tasks for Spring Framework (subject_id = 3, user_id = 2)
INSERT INTO TASK (subject_id, title, status, deadline, reflection) VALUES (3, 'Spring Bootのセットアップ', 'DONE', '2026-02-26', '');
INSERT INTO TASK (subject_id, title, status, deadline, reflection) VALUES (3, 'DIコンテナの仕組みを学ぶ', 'DONE', '2026-02-26', '');
INSERT INTO TASK (subject_id, title, status, deadline, reflection) VALUES (3, 'Spring JDBCを実装する', 'DONE', '2026-02-26', ''); 