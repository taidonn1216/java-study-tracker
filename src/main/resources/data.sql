-- Insert sample subjects
INSERT INTO SUBJECT (name) VALUES ('Java');
INSERT INTO SUBJECT (name) VALUES ('データベース');
INSERT INTO SUBJECT (name) VALUES ('Spring Framework');
--Insert sample complete
INSERT INTO COMPLETE (completed) VALUES ('未完了');
INSERT INTO COMPLETE (completed) VALUES ('進行中');
INSERT INTO COMPLETE (completed) VALUES ('完了');

-- Insert sample tasks for Java (subject_id = 1)
INSERT INTO TASK (subject_id, title, completed_id, deadline, comment) VALUES (1, '第1章: Java基礎を読む', 3,'2024-12-24', '期限切れ');
INSERT INTO TASK (subject_id, title, completed_id, deadline, comment) VALUES (1, '第2章: オブジェクト指向を学ぶ', 3, '2026-12-24', '');
INSERT INTO TASK (subject_id, title, completed_id, deadline) VALUES (1, '第3章: コレクションフレームワーク', 1, '2026-2-26');

-- Insert sample tasks for データベース (subject_id = 2)
INSERT INTO TASK (subject_id, title, completed_id, deadline, comment) VALUES (2, 'SQL基礎: SELECT文を理解する', 3, '2026-11-12','');
INSERT INTO TASK (subject_id, title, completed_id, deadline, comment) VALUES (2, 'JOINの種類を学習する', 1, '2026-1-12','期限切れ');
INSERT INTO TASK (subject_id, title, completed_id, deadline, comment) VALUES (2, 'トランザクション処理を理解する', 2, '2026-2-1', '期限切れ');

-- Insert sample tasks for Spring Framework (subject_id = 3)
INSERT INTO TASK (subject_id, title, completed_id, deadline, comment) VALUES (3, 'Spring Bootのセットアップ', 3, '2026-12-25', '');
INSERT INTO TASK (subject_id, title, completed_id, deadline, comment) VALUES (3, 'DIコンテナの仕組みを学ぶ', 1, '2026-6-21', '');
INSERT INTO TASK (subject_id, title, completed_id, deadline) VALUES (3, 'Spring JDBCを実装する', 1, '2026-4-24');
