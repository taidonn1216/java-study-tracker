-- Insert sample subjects
INSERT INTO SUBJECT (name) VALUES ('Java');
INSERT INTO SUBJECT (name) VALUES ('データベース');
INSERT INTO SUBJECT (name) VALUES ('Spring Framework');
INSERT INTO USERS (username, password) VALUES(
    'user',
    '$2b$10$8gl4P.2H1sEOH0mU8moubOHvLxrfqr6AKtJ326H3CbgJ2dikD9/ma'
);

-- Insert sample tasks for Java (subject_id = 1)
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (1, '第1章: Java基礎を読む', true,'DONE','2026-02-26','');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (1, '第2章: オブジェクト指向を学ぶ', true,'DONE','2026-02-26','');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (1, '第3章: コレクションフレームワーク', true,'DONE','2026-02-26','');

-- Insert sample tasks for データベース (subject_id = 2)
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (2, 'SQL基礎: SELECT文を理解する', true,'DONE','2026-02-26','');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (2, 'JOINの種類を学習する', true,'DONE','2026-02-26','');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (2, 'トランザクション処理を理解する', true,'DONE','2026-02-26','');

-- Insert sample tasks for Spring Framework (subject_id = 3)
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (3, 'Spring Bootのセットアップ', true,'DONE','2026-02-26','');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (3, 'DIコンテナの仕組みを学ぶ', true,'DONE','2026-02-26','');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (3, 'Spring JDBCを実装する', true,'DONE','2026-02-26','');