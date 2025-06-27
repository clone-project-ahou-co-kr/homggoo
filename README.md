# homggoo
오늘의 집 클론(6~23)

```mariadb
create schema homggoo;

CREATE TABLE homggoo.users
(
    `email`                VARCHAR(50)                                   NOT NULL COMMENT '이메일',
    `profile`              LONGBLOB                                      NOT NULL COMMENT '유저 프로필 이미지',
    `password`             VARCHAR(255)                                  NOT NULL COMMENT '패스워드',
    `is_admin`             BOOLEAN                                       NOT NULL DEFAULT FALSE COMMENT '사용자 타입(사용자, 관리자)',
    `nickname`             VARCHAR(30)                                   NOT NULL UNIQUE COMMENT '유저 닉네임',
    `provider_type`        ENUM ('KAKAO', 'NAVER', 'ORIGIN')             NOT NULL DEFAULT 'ORIGIN' COMMENT '로그인 타입(KAKAO, NAVER, ORIGIN)',
    `provider_key`         VARCHAR(255)                                  NOT NULL UNIQUE COMMENT '로그인 타입에 따른 식별값',
    `created_at`           DATETIME                                      NOT NULL COMMENT '유저 회원가입 생성일',
    `modified_at`          DATETIME                                      NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 정보 수정일',
    `is_deleted`           BOOLEAN                                       NOT NULL DEFAULT FALSE COMMENT '회원탈퇴 여부',

    CONSTRAINT PRIMARY KEY (`email`),
    CONSTRAINT UNIQUE (`provider_type`, `provider_key`)
);

CREATE TABLE `homggoo`.`boards`
(
    `board_id` varchar(50) not null,
    `display_text` varchar(50) not null,
    `is_admin_only` boolean not null,
    `type_order` tinyint not null,
    CONSTRAINT PRIMARY KEY (`board_id`)
);


create table `homggoo`.`article`
(
    `id` int(10) unsigned not null auto_increment,
    `board_id` varchar(10) not null,
    `user_email` varchar(50) not null,
    `title` varchar(100) not null,
    `content` longtext not null,
    `view` int(10) unsigned not null,
    `created_at` datetime not null default now(),
    `modified_at` datetime not null default now(),
    `is_deleted` boolean not null,
    CONSTRAINT PRIMARY KEY (`id`)
);

create table `homggoo`.`article_user_likes`
(
    `article_id` int unsigned not null,
    `user_email`  varchar(50)  not null,
    `created_at`  datetime     not null default now(),
    constraint primary key (`article_id`, `user_email`),
    constraint foreign key (`article_id`) references `homggoo`.`article` (`id`)
        on delete cascade
        on update cascade,
    constraint foreign key (`user_email`) references `homggoo`.`users` (`email`)
        on delete cascade
        on update cascade
);
```