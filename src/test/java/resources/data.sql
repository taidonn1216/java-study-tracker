-- Insert sample subjects
INSERT INTO SUBJECT (name) VALUES ('Java');
INSERT INTO SUBJECT (name) VALUES ('データベース');
INSERT INTO SUBJECT (name) VALUES ('Spring Framework');

INSERT INTO COMPLETE (completed) VALUES ('未完了');
INSERT INTO COMPLETE (completed) VALUES ('進行中');
INSERT INTO COMPLETE (completed) VALUES ('完了');

-- Insert sample tasks for Java (subject_id = 1)
INSERT INTO TASK (subject_id, title, completed_id) VALUES (1, '第1章: Java基礎を読む', 3);
INSERT INTO TASK (subject_id, title, completed_id) VALUES (1, '第2章: オブジェクト指向を学ぶ', 3);
INSERT INTO TASK (subject_id, title, completed_id) VALUES (1, '第3章: コレクションフレームワーク', 1);

-- Insert sample tasks for データベース (subject_id = 2)
INSERT INTO TASK (subject_id, title, completed_id) VALUES (2, 'SQL基礎: SELECT文を理解する', 3);
INSERT INTO TASK (subject_id, title, completed_id) VALUES (2, 'JOINの種類を学習する', 1);
INSERT INTO TASK (subject_id, title, completed_id) VALUES (2, 'トランザクション処理を理解する', 2);

-- Insert sample tasks for Spring Framework (subject_id = 3)
INSERT INTO TASK (subject_id, title, completed_id) VALUES (3, 'Spring Bootのセットアップ', 3);
INSERT INTO TASK (subject_id, title, completed_id) VALUES (3, 'DIコンテナの仕組みを学ぶ', 1);
INSERT INTO TASK (subject_id, title, completed_id) VALUES (3, 'Spring JDBCを実装する', 1);
