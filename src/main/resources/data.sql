-- Insert sample subjects
INSERT INTO SUBJECT (name) VALUES ('Java');
INSERT INTO SUBJECT (name) VALUES ('データベース');
INSERT INTO SUBJECT (name) VALUES ('Spring Framework');

-- Insert sample tasks for Java (subject_id = 1)
INSERT INTO TASK (subject_id, title, completed) VALUES (1, '第1章: Java基礎を読む', true);
INSERT INTO TASK (subject_id, title, completed) VALUES (1, '第2章: オブジェクト指向を学ぶ', true);
INSERT INTO TASK (subject_id, title, completed) VALUES (1, '第3章: コレクションフレームワーク', false);

-- Insert sample tasks for データベース (subject_id = 2)
INSERT INTO TASK (subject_id, title, completed) VALUES (2, 'SQL基礎: SELECT文を理解する', true);
INSERT INTO TASK (subject_id, title, completed) VALUES (2, 'JOINの種類を学習する', false);
INSERT INTO TASK (subject_id, title, completed) VALUES (2, 'トランザクション処理を理解する', false);

-- Insert sample tasks for Spring Framework (subject_id = 3)
INSERT INTO TASK (subject_id, title, completed) VALUES (3, 'Spring Bootのセットアップ', true);
INSERT INTO TASK (subject_id, title, completed) VALUES (3, 'DIコンテナの仕組みを学ぶ', false);
INSERT INTO TASK (subject_id, title, completed) VALUES (3, 'Spring JDBCを実装する', false);
