-- 초기 데이터 입력
INSERT INTO users (user_id, login_id, user_name, password, nickname, role, phone) 
VALUES (seq_user_id.NEXTVAL, 'test', '테스트유저', '1234', '테스터', 'USER', '010-0000-0000');

INSERT INTO boards (board_id, manager_id) 
VALUES (seq_board_id.NEXTVAL, 1);

COMMIT;