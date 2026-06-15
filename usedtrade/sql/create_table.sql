-- 2. 사용자 테이블 (USERS)
CREATE TABLE users (
    user_id           NUMBER DEFAULT seq_user_id.NEXTVAL PRIMARY KEY,
    login_id          VARCHAR2(50) NOT NULL UNIQUE, 
    user_name         VARCHAR2(50) NOT NULL,        
    password          VARCHAR2(255) NOT NULL,
    nickname          VARCHAR2(50) NOT NULL,
    role              VARCHAR2(20) NOT NULL,             
    phone             VARCHAR2(30),
    created_at_user   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 게시판 관리 테이블 (BOARDS)
CREATE TABLE boards (
    board_id    NUMBER DEFAULT seq_board_id.NEXTVAL PRIMARY KEY,
    manager_id  NUMBER NOT NULL,             
    CONSTRAINT fk_boards_users FOREIGN KEY (manager_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 4. 중고거래 게시글 테이블 (POSTS)
CREATE TABLE posts (
    post_id           NUMBER DEFAULT seq_post_id.NEXTVAL PRIMARY KEY,
    user_id           NUMBER NOT NULL,        
    buyer_id          NUMBER,                
    board_id          NUMBER NOT NULL,
    title             VARCHAR2(200) NOT NULL,
    content_posts     CLOB NOT NULL,
    price             NUMBER NOT NULL,
    category          VARCHAR2(50),
    location          VARCHAR2(100),
    status            VARCHAR2(20) DEFAULT 'SELLING' NOT NULL,
    view_count        NUMBER DEFAULT 0 NOT NULL,
    created_at_posts  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_posts_users FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_posts_buyer FOREIGN KEY (buyer_id) REFERENCES users(user_id) ON DELETE SET NULL,
    CONSTRAINT fk_posts_boards FOREIGN KEY (board_id) REFERENCES boards(board_id) ON DELETE CASCADE
);

-- 5. 커뮤니티 게시글 테이블 (COMMUNITY_POSTS)
CREATE TABLE community_posts (
    community_post_id NUMBER DEFAULT seq_community_post_id.NEXTVAL PRIMARY KEY,
    user_id           NUMBER NOT NULL,
    title             VARCHAR2(200) NOT NULL,
    content           CLOB NOT NULL,
    category          VARCHAR2(20) NOT NULL CHECK (category IN ('FREE', 'LOCAL', 'REVIEW', 'QNA')),
    view_count        NUMBER DEFAULT 0 NOT NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP,

    CONSTRAINT fk_community_posts_users FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 6. 이미지 테이블 (POST_IMG)
CREATE TABLE post_img (
    image_id          NUMBER DEFAULT seq_image_id.NEXTVAL PRIMARY KEY,
    post_id           NUMBER, -- NULL 허용 (중고거래)
    community_post_id NUMBER, -- 추가 (커뮤니티)
    original_name     VARCHAR2(255),
    stored_name       VARCHAR2(255) NOT NULL,
    image_url         VARCHAR2(1000) NOT NULL,
    is_main           CHAR(1) DEFAULT 'N' CHECK (is_main IN ('Y', 'N')),
    created_at_img    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_post_img_posts FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    CONSTRAINT fk_post_img_community FOREIGN KEY (community_post_id) REFERENCES community_posts(community_post_id) ON DELETE CASCADE
);

-- 7. 댓글 테이블 (COMMENTS)
CREATE TABLE comments (
    comments_id       NUMBER DEFAULT seq_comments_id.NEXTVAL PRIMARY KEY,
    user_id           NUMBER NOT NULL,
    post_id           NUMBER, -- NULL 허용 (중고거래)
    community_post_id NUMBER, -- 추가 (커뮤니티)
    content_comments  CLOB NOT NULL,
    is_secret         CHAR(1) DEFAULT 'N' CHECK (is_secret IN ('Y', 'N')),
    created_at_comments TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at_comments TIMESTAMP,

    CONSTRAINT fk_comments_users FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_posts FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_community FOREIGN KEY (community_post_id) REFERENCES community_posts(community_post_id) ON DELETE CASCADE
);

-- 8. 커뮤니티 추천 테이블 (COMMUNITY_POST_LIKES)
CREATE TABLE community_post_likes (
    like_id           NUMBER DEFAULT seq_like_id.NEXTVAL PRIMARY KEY,
    community_post_id NUMBER NOT NULL,
    user_id           NUMBER NOT NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_like_post FOREIGN KEY (community_post_id) REFERENCES community_posts(community_post_id) ON DELETE CASCADE,
    CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uq_like UNIQUE (community_post_id, user_id)
);

-- 9. 중고거래 찜 테이블 (POST_FAVORITE)
CREATE TABLE post_favorite (
    favorite_id       NUMBER DEFAULT seq_favorite_id.NEXTVAL PRIMARY KEY,
    post_id           NUMBER NOT NULL,
    user_id           NUMBER NOT NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_favorite_post FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uq_favorite_user_post UNIQUE (user_id, post_id)
);