-- Insert sample subjects
INSERT INTO SUBJECT (name) VALUES ('Java');
INSERT INTO SUBJECT (name) VALUES ('データベース');
INSERT INTO SUBJECT (name) VALUES ('Spring Framework');

-- Insert sample tasks for Java (subject_id = 1)
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (1, '第1章: Java基礎を読む', true,'完了','2026-02-26','記入してください');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (1, '第2章: オブジェクト指向を学ぶ', true,'完了','2026-02-26','記入してください');
<<<<<<< HEAD
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (1, '第3章: コレクションフレームワーク', true,'完了','2026-02-26','記入してください');

-- Insert sample tasks for データベース (subject_id = 2)
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (2, 'SQL基礎: SELECT文を理解する', true,'完了','2026-02-26','記入してください');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (2, 'JOINの種類を学習する', true,'完了','2026-02-26','記入してください');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (2, 'トランザクション処理を理解する', true,'完了','2026-02-26','記入してください');

-- Insert sample tasks for Spring Framework (subject_id = 3)
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (3, 'Spring Bootのセットアップ', true,'完了','2026-02-26','記入してください');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (3, 'DIコンテナの仕組みを学ぶ', true,'完了','2026-02-26','記入してください');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (3, 'Spring JDBCを実装する', true,'完了','2026-02-26','記入してください');
=======
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (1, '第3章: コレクションフレームワーク', false,'完了','2026-02-26','記入してください');

-- Insert sample tasks for データベース (subject_id = 2)
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (2, 'SQL基礎: SELECT文を理解する', true,'完了','2026-02-26','記入してください');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (2, 'JOINの種類を学習する', false,'完了','2026-02-26','記入してください');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (2, 'トランザクション処理を理解する', false,'完了','2026-02-26','記入してください');

-- Insert sample tasks for Spring Framework (subject_id = 3)
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (3, 'Spring Bootのセットアップ', true,'完了','2026-02-26','記入してください');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (3, 'DIコンテナの仕組みを学ぶ', false,'完了','2026-02-26','記入してください');
INSERT INTO TASK (subject_id, title, completed,status,deadline,reflection) VALUES (3, 'Spring JDBCを実装する', false,'完了','2026-02-26','記入してください');
>>>>>>> 705b0506cd944f7ec037041f9375f41f1843f341
