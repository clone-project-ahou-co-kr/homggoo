# homggoo
오늘의 집 클론(6~23)

```mariadb
create schema homggoo;

/*순서 중요!!
users
board_category
boards
notice
images
article_category
article
article_user_likes ← 🔥 반드시 마지막*/
CREATE TABLE homggoo.users
(
    `email`                VARCHAR(50)                                   NOT NULL COMMENT '이메일',
    `profile`              LONGBLOB                                      NOT NULL COMMENT '유저 프로필 이미지',
    `password`             VARCHAR(255)                                  NOT NULL COMMENT '패스워드',
    `is_admin`             BOOLEAN                                       NOT NULL DEFAULT FALSE COMMENT '사용자 타입(사용자, 관리자)',
    `nickname`             VARCHAR(30)                                   NOT NULL UNIQUE COMMENT '유저 닉네임',
    `provider_type`        ENUM ('KAKAO', 'NAVER', 'ORIGIN')             NOT NULL DEFAULT 'ORIGIN' COMMENT '로그인 타입(KAKAO, NAVER, ORIGIN)',
    `provider_key`         VARCHAR(255)                                  NULL UNIQUE COMMENT '로그인 타입에 따른 식별값',
    `created_at`           DATETIME                                      NOT NULL COMMENT '유저 회원가입 생성일',
    `modified_at`          DATETIME                                      NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 정보 수정일',
    `is_deleted`           BOOLEAN                                      NOT NULL DEFAULT FALSE COMMENT '회원탈퇴 여부',
    `image_url`            VARCHAR(512)                                  NULL COMMENT '이미지 URL',

    CONSTRAINT PRIMARY KEY (`email`),
    CONSTRAINT UNIQUE (`provider_type`, `provider_key`)
);


CREATE TABLE homggoo.board_category
(
    `category_id`   VARCHAR(50) NOT NULL COMMENT '카테고리 ID (PK)',
    `display_text`  VARCHAR(50) NOT NULL COMMENT '카테고리 이름',

    CONSTRAINT PRIMARY KEY (`category_id`)
);

CREATE TABLE homggoo.boards
(
    `board_id`      VARCHAR(50) NOT NULL COMMENT '게시판 ID',
    `display_text`  VARCHAR(50) NOT NULL COMMENT '게시판',

    CONSTRAINT PRIMARY KEY (`board_id`)
);


CREATE TABLE homggoo.article
(
    `id`           INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '게시글 ID',
    `board_id`     VARCHAR(50)      NOT NULL COMMENT '게시판 ID (FK)',
    `category_id`  VARCHAR(50)      NOT NULL COMMENT '게시판 종류',
    `user_email`   VARCHAR(50)      NOT NULL COMMENT '작성자 이메일 (FK)',
    `title`        VARCHAR(100)     NOT NULL COMMENT '제목',
    `content`      LONGTEXT         NOT NULL COMMENT '내용',
    `view`         INT(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '조회수',
    `created_at`   DATETIME         NOT NULL DEFAULT NOW() COMMENT '작성일',
    `modified_at`  DATETIME         NOT NULL DEFAULT NOW() COMMENT '수정일',
    `is_deleted`   BOOLEAN          NOT NULL DEFAULT FALSE COMMENT '삭제 여부',

    CONSTRAINT PRIMARY KEY (`id`),
    CONSTRAINT FOREIGN KEY (`board_id`) REFERENCES homggoo.boards(`board_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT FOREIGN KEY (`category_id`) REFERENCES homggoo.article_category(`category_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT FOREIGN KEY (`user_email`) REFERENCES homggoo.users(`email`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE homggoo.article_category
(
    `category_id` VARCHAR(50) NOT NULL COMMENT '게시글 종류',
    `board_id` VARCHAR(50) NOT NULL COMMENT '어디 게시판',
    `display_text` VARCHAR(50) NOT NULL,

    CONSTRAINT PRIMARY KEY (`category_id`),
    CONSTRAINT FOREIGN KEY (`board_id`) REFERENCES homggoo.boards(`board_id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE homggoo.article_user_likes
(
    `article_id`   INT UNSIGNED   NOT NULL COMMENT '게시글 ID (FK)',
    `user_email`   VARCHAR(50)    NOT NULL COMMENT '좋아요 누른 유저 이메일 (FK)',
    `created_at`   DATETIME       NOT NULL DEFAULT NOW(),

    CONSTRAINT PRIMARY KEY (`article_id`, `user_email`),
    CONSTRAINT FOREIGN KEY (`article_id`) REFERENCES homggoo.article(`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT FOREIGN KEY (`user_email`) REFERENCES homggoo.users(`email`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE `homggoo`.`comments`
(
    `id` INT UNSIGNED NOT NULL COMMENT '댓글 ID (PK)',
    `article_id` INT UNSIGNED NOT NULL COMMENT '게시글 ID(FK)',
    `user_email` VARCHAR(50) NOT NULL COMMENT '유저 테이블 PK (FK)',
    `comment_id` INT UNSIGNED NOT NULL COMMENT '답글 ID',
    `content` VARCHAR(1000) NOT NULL COMMENT '내용',
    `is_deleted` boolean NOT NULL DEFAULT FALSE COMMENT '삭제여부',

    CONSTRAINT PRIMARY KEY (`id`),
    CONSTRAINT FOREIGN  KEY (`article_id`) REFERENCES `homggoo`.`article`(`id`),
    CONSTRAINT FOREIGN KEY (`user_email`) REFERENCES `homggoo`.`users` (`email`)
);
create table `homggoo`.`images`
(
    `index`         int unsigned not null auto_increment,
    `notice_index`  int unsigned null,
    `article_index` int unsigned null,
    `name`          varchar(255) not null,
    `content_type`  varchar(50)  not null,
    `data`          longblob     not null, #longblob은 binary 데이터를 쓰기위함.
    `created_at`    datetime     not null default now(),
    constraint primary key (`index`)
);
```