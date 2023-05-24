CREATE TABLE admin
(
    id         INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    login_id   VARCHAR(50)                           NOT NULL COMMENT '로그인 아이디',
    password   VARCHAR(60)                           NOT NULL COMMENT '비밀번호',
    state      ENUM ('ACTIVE', 'BANNED', 'DELETED')  NOT NULL COMMENT '상태',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP( ) NOT NULL COMMENT '생성 일시'
)
    COMMENT '관리자';

CREATE TABLE banner
(
    id          INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    state       ENUM ('ACTIVE', 'INACTIVE')                     NOT NULL COMMENT '상태',
    type        ENUM ('NONE', 'CURATION', 'NOTICE', 'CATEGORY') NOT NULL COMMENT '타입',
    image       MEDIUMTEXT                                      NOT NULL COMMENT '이미지',
    curation_id INT                                             NULL COMMENT '큐레이션 아이디',
    notice_id   INT                                             NULL COMMENT '공지사항 아이디',
    category_id INT                                             NULL COMMENT '카테고리 아이디'
)
    COMMENT '배너' CHARSET = utf8mb4;

CREATE TABLE category
(
    id          INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    category_id INT         NULL COMMENT '부모 카테고리',
    image       MEDIUMTEXT  NULL COMMENT '이미지',
    name        VARCHAR(20) NOT NULL COMMENT '이름',
    CONSTRAINT FK_category_category_id_category_id
        FOREIGN KEY ( category_id ) REFERENCES category ( id )
)
    COMMENT '카테고리' CHARSET = utf8mb4;

CREATE TABLE curation
(
    id          INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    image       LONGTEXT                                NOT NULL COMMENT '이미지(S타입)',
    short_name  VARCHAR(20)                             NOT NULL COMMENT '약어(S타입)',
    title       VARCHAR(100)                            NOT NULL COMMENT '제목(L타입)',
    description VARCHAR(200)                            NOT NULL COMMENT '설명(L타입)',
    type        ENUM ('SQUARE', 'S_SLIDER', 'L_SLIDER') NOT NULL COMMENT '표출 타입',
    sort_no     INT                                     NOT NULL COMMENT '정렬 순서'
)
    COMMENT '큐레이션' CHARSET = utf8mb4;

CREATE TABLE notice
(
    id         INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    title      VARCHAR(200)                          NULL COMMENT '제목',
    content    MEDIUMTEXT                            NULL COMMENT '내용',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP( ) NULL COMMENT '생성 일시'
)
    COMMENT '공지사항' CHARSET = utf8mb4;

CREATE TABLE search_keyword
(
    keyword   VARCHAR(100) NOT NULL COMMENT '키워드'
        PRIMARY KEY,
    amount    INT          NOT NULL COMMENT '횟수',
    prev_rank INT          NULL COMMENT '이전 순위'
)
    COMMENT '검색어' CHARSET = utf8mb4;

CREATE TABLE site_information
(
    id          VARCHAR(30)  NOT NULL COMMENT '아이디. 아이디'
        PRIMARY KEY,
    type        ENUM ('ALL') NOT NULL COMMENT '타입. 타입',
    description VARCHAR(100) NOT NULL COMMENT '설명. 설명',
    content     MEDIUMTEXT   NOT NULL COMMENT '값. 값'
)
    COMMENT '사이트 정보 테이블. 사이트 정보 테이블' CHARSET = utf8mb4;

CREATE TABLE store
(
    id       INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    state    ENUM ('ACTIVE', 'BANNED', 'DELETED')  NOT NULL COMMENT '상태',
    login_id VARCHAR(50)                           NOT NULL COMMENT '로그인 아이디',
    password VARCHAR(60)                           NOT NULL COMMENT '비밀번호',
    join_at  DATETIME DEFAULT CURRENT_TIMESTAMP( ) NOT NULL COMMENT '가입 일시'
)
    COMMENT '상점' CHARSET = utf8mb4;

CREATE TABLE product
(
    id                 INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    store_id           INT                                    NOT NULL COMMENT '상점 아이디',
    category_id        INT                                    NOT NULL COMMENT '카테고리 아이디',
    state              ENUM ('ACTIVE', 'SOLD_OUT', 'DELETED') NOT NULL COMMENT '상태',
    images             MEDIUMTEXT                             NOT NULL COMMENT '이미지',
    title              VARCHAR(100)                           NOT NULL COMMENT '제목',
    origin_price       INT                                    NOT NULL COMMENT '원가',
    discount_rate      INT                                    NOT NULL COMMENT '할인률',
    delivery_info      MEDIUMTEXT                             NOT NULL COMMENT '배송안내',
    description_images MEDIUMTEXT                             NOT NULL COMMENT '상품 상세 이미지',
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP( )  NOT NULL COMMENT '생성한 일시',
    CONSTRAINT FK_product_category_id_category_id
        FOREIGN KEY ( category_id ) REFERENCES category ( id ),
    CONSTRAINT FK_product_store_id_store_id
        FOREIGN KEY ( store_id ) REFERENCES store ( id )
)
    COMMENT '상품' CHARSET = utf8mb4;

CREATE TABLE curation_product_map
(
    id          INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    curation_id INT NOT NULL COMMENT '큐레이션 아이디',
    product_id  INT NOT NULL COMMENT '상품 아이디',
    CONSTRAINT FK_curation_product_map_curation_id_curation_id
        FOREIGN KEY ( curation_id ) REFERENCES curation ( id ),
    CONSTRAINT FK_curation_product_map_product_id_product_id
        FOREIGN KEY ( product_id ) REFERENCES product ( id )
)
    COMMENT '큐레이션 상품 매핑' CHARSET = utf8mb4;

CREATE TABLE `option`
(
    id          INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    product_id  INT          NOT NULL COMMENT '상품 아이디',
    is_needed   TINYINT(1)   NOT NULL COMMENT '필수 여부',
    description VARCHAR(200) NOT NULL COMMENT '설명',
    CONSTRAINT FK_option_product_id_product_id
        FOREIGN KEY ( product_id ) REFERENCES product ( id )
)
    COMMENT '옵션' CHARSET = utf8mb4;

CREATE TABLE option_item
(
    id        INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    option_id INT          NOT NULL COMMENT '옵션 아이디',
    name      VARCHAR(100) NOT NULL COMMENT '이름',
    price     INT          NOT NULL COMMENT '가격',
    CONSTRAINT FK_option_item_option_id_option_id
        FOREIGN KEY ( option_id ) REFERENCES `option` ( id )
)
    COMMENT '옵션 아이템' CHARSET = utf8mb4;

CREATE TABLE store_info
(
    id              INT AUTO_INCREMENT COMMENT '상점 아이디'
        PRIMARY KEY,
    backgroud_image MEDIUMTEXT  NOT NULL COMMENT '배경 이미지',
    profile_image   MEDIUMTEXT  NOT NULL COMMENT '프로필 이미지',
    name            VARCHAR(50) NOT NULL COMMENT '이름',
    location        VARCHAR(50) NOT NULL COMMENT '위치',
    keyword         MEDIUMTEXT  NOT NULL COMMENT '키워드',
    CONSTRAINT FK_store_info_id_store_id
        FOREIGN KEY ( id ) REFERENCES store ( id )
)
    COMMENT '상점 정보' CHARSET = utf8mb4;

CREATE TABLE tip
(
    id          INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    title       VARCHAR(100)                          NOT NULL COMMENT '제목',
    description VARCHAR(200)                          NOT NULL COMMENT '설명',
    image       MEDIUMTEXT                            NOT NULL COMMENT '이미지',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP( ) NOT NULL COMMENT '생성 일시'
)
    COMMENT '알아두면 좋은 정보' CHARSET = utf8mb4;

CREATE TABLE top_bar
(
    id   INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    name VARCHAR(20) NOT NULL COMMENT '이름'
)
    COMMENT '메인 탑바 카테고리' CHARSET = utf8mb4;

CREATE TABLE top_bar_product_map
(
    id         INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    top_bar_id INT NOT NULL COMMENT '탑바 아이디',
    product_id INT NOT NULL COMMENT '상품 아이디',
    CONSTRAINT FK_top_bar_product_map_product_id_product_id
        FOREIGN KEY ( product_id ) REFERENCES product ( id ),
    CONSTRAINT FK_top_bar_product_map_top_bar_id_top_bar_id
        FOREIGN KEY ( top_bar_id ) REFERENCES top_bar ( id )
)
    COMMENT '탑바 상품 매핑' CHARSET = utf8mb4;

CREATE TABLE user
(
    id      INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    state   ENUM ('ACTIVE', 'BANNED', 'DELETED') NOT NULL COMMENT '상태',
    join_at DATETIME                             NOT NULL COMMENT '가입 일시'
)
    COMMENT '유저' CHARSET = utf8mb4;

CREATE TABLE basket_product_info
(
    id           INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    user_id      INT NOT NULL COMMENT '유저 아이디',
    product_id   INT NOT NULL COMMENT '상품 아이디',
    amount       INT NOT NULL COMMENT '갯수',
    delivery_fee INT NOT NULL COMMENT '배송비',
    CONSTRAINT FK_basket_product_info_product_id_product_id
        FOREIGN KEY ( product_id ) REFERENCES product ( id ),
    CONSTRAINT FK_basket_product_info_user_id_user_id
        FOREIGN KEY ( user_id ) REFERENCES user ( id )
)
    COMMENT '장바구니 상품 정보' CHARSET = utf8mb4;

CREATE TABLE basket_product_option
(
    id               INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    order_product_id INT NOT NULL COMMENT '주문 상품 아이디',
    option_id        INT NOT NULL COMMENT '옵션 아이디',
    CONSTRAINT FK_basket_product_option_option_id_option_item_id
        FOREIGN KEY ( option_id ) REFERENCES option_item ( id ),
    CONSTRAINT FK_basket_product_option_order_product_id_basket_product_info_id
        FOREIGN KEY ( order_product_id ) REFERENCES basket_product_info ( id )
)
    COMMENT '주문 상품 옵션' CHARSET = utf8mb4;

CREATE TABLE compare_set
(
    id         INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    user_id    INT      NOT NULL COMMENT '유저 아이디',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    CONSTRAINT FK_compare_set_user_id_user_id
        FOREIGN KEY ( user_id ) REFERENCES user ( id )
)
    COMMENT '비교하기 셋트' CHARSET = utf8mb4;

CREATE TABLE compare_item
(
    compare_set_id INT NOT NULL COMMENT '비교하기 세트 아이디',
    product_id     INT NOT NULL COMMENT '상품 아이디',
    PRIMARY KEY ( compare_set_id, product_id ),
    CONSTRAINT FK_compare_item_compare_set_id_compare_set_id
        FOREIGN KEY ( compare_set_id ) REFERENCES compare_set ( id ),
    CONSTRAINT FK_compare_item_product_id_product_id
        FOREIGN KEY ( product_id ) REFERENCES product ( id )
)
    COMMENT '비교하기 아이템' CHARSET = utf8mb4;

CREATE TABLE inquiry
(
    id          INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    type        ENUM ('PRODUCT', 'DELIVERY', 'CANCEL', 'ETC') NOT NULL COMMENT '타입',
    is_secret   TINYINT(1)                                    NOT NULL COMMENT '비밀글 여부',
    product_id  INT                                           NOT NULL COMMENT '상품 아이디',
    user_id     INT                                           NOT NULL COMMENT '유저 아이디',
    title       VARCHAR(200)                                  NOT NULL COMMENT '제목',
    content     MEDIUMTEXT                                    NOT NULL COMMENT '내용',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP( )         NOT NULL COMMENT '생성일시',
    answeted_at DATETIME                                      NULL COMMENT '답변 일시',
    CONSTRAINT FK_inquiry_product_id_product_id
        FOREIGN KEY ( product_id ) REFERENCES product ( id ),
    CONSTRAINT FK_inquiry_user_id_user_id
        FOREIGN KEY ( user_id ) REFERENCES user ( id )
)
    COMMENT '문의' CHARSET = utf8mb4;

CREATE TABLE notification
(
    id         INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    user_id    INT                                            NOT NULL COMMENT '유저 아이디',
    type       ENUM ('DELIVERY', 'ADMIN', 'SYSTEM', 'COUPON') NOT NULL COMMENT '타입',
    title      VARCHAR(100)                                   NOT NULL COMMENT '제목',
    content    VARCHAR(300)                                   NOT NULL COMMENT '내용',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP( )          NOT NULL COMMENT '생성 일시',
    CONSTRAINT FK_notification_user_id_user_id
        FOREIGN KEY ( user_id ) REFERENCES user ( id )
)
    COMMENT '알림' CHARSET = utf8mb4;

CREATE TABLE `order`
(
    id          VARCHAR(20)                           NOT NULL COMMENT '아이디'
        PRIMARY KEY,
    user_id     INT                                   NOT NULL COMMENT '유저 아이디',
    state       ENUM ('PAID_DONE', 'CANCELED')        NOT NULL COMMENT '상태',
    total_price VARCHAR(45)                           NOT NULL COMMENT '총 금액',
    ordered_at  DATETIME DEFAULT CURRENT_TIMESTAMP( ) NOT NULL COMMENT '주문 일시',
    CONSTRAINT FK_order_user_id_user_id
        FOREIGN KEY ( user_id ) REFERENCES user ( id )
)
    COMMENT '주문' CHARSET = latin1;

CREATE TABLE order_product_info
(
    id           INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    order_id     VARCHAR(20) NOT NULL COMMENT '주문 아이디',
    product_id   INT         NOT NULL COMMENT '상품 아이디',
    price        INT         NOT NULL COMMENT '상품 가격',
    amount       INT         NOT NULL COMMENT '갯수',
    delivery_fee INT         NOT NULL COMMENT '배송비',
    CONSTRAINT FK_order_product_info_order_id_order_id
        FOREIGN KEY ( order_id ) REFERENCES `order` ( id ),
    CONSTRAINT FK_order_product_info_product_id_product_id
        FOREIGN KEY ( product_id ) REFERENCES product ( id )
)
    COMMENT '주문 상품 정보' CHARSET = latin1;

CREATE TABLE order_product_option
(
    id               INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    order_product_id INT          NOT NULL COMMENT '주문 상품 아이디',
    name             VARCHAR(100) NOT NULL COMMENT '이름',
    price            INT          NOT NULL COMMENT '가격',
    CONSTRAINT FK_order_product_option_order_product_id_order_product_info_id
        FOREIGN KEY ( order_product_id ) REFERENCES order_product_info ( id )
)
    COMMENT '주문 상품 옵션' CHARSET = utf8mb4;

CREATE TABLE product_like
(
    product_id INT NOT NULL COMMENT '상품 아이디',
    user_id    INT NOT NULL COMMENT '유저 아이디',
    PRIMARY KEY ( product_id, user_id ),
    CONSTRAINT FK_product_like_product_id_product_id
        FOREIGN KEY ( product_id ) REFERENCES product ( id ),
    CONSTRAINT FK_product_like_user_id_user_id
        FOREIGN KEY ( user_id ) REFERENCES user ( id )
)
    COMMENT '상품 좋아요' CHARSET = utf8mb4;

CREATE TABLE review
(
    id         INT AUTO_INCREMENT COMMENT '아이디'
        PRIMARY KEY,
    product_id INT                                                   NOT NULL COMMENT '상품 아이디',
    store_id   INT                                                   NOT NULL COMMENT '상점 아이디',
    user_id    INT                                                   NOT NULL COMMENT '유저 아이디',
    evaluation ENUM ('TASTE', 'FRESH', 'PRICE', 'PACKAGING', 'SIZE') NOT NULL COMMENT '평가 타입',
    images     MEDIUMTEXT                                            NOT NULL COMMENT '이미지',
    content    MEDIUMTEXT                                            NOT NULL COMMENT '내용',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP( )                 NOT NULL COMMENT '생성한 일시',
    CONSTRAINT FK_review_product_id_product_id
        FOREIGN KEY ( product_id ) REFERENCES product ( id ),
    CONSTRAINT FK_review_store_id_store_id
        FOREIGN KEY ( store_id ) REFERENCES store ( id ),
    CONSTRAINT FK_review_user_id_user_id
        FOREIGN KEY ( user_id ) REFERENCES user ( id )
)
    COMMENT '리뷰' CHARSET = utf8mb4;

CREATE TABLE store_scrap
(
    store_id INT NOT NULL COMMENT '상점아이디',
    user_id  INT NOT NULL COMMENT '유저 아이디',
    PRIMARY KEY ( store_id, user_id ),
    CONSTRAINT FK_store_scrap_store_id_store_id
        FOREIGN KEY ( store_id ) REFERENCES store ( id ),
    CONSTRAINT FK_store_scrap_user_id_user_id
        FOREIGN KEY ( user_id ) REFERENCES user ( id )
)
    COMMENT '상점 스크랩' CHARSET = utf8mb4;

CREATE TABLE user_info
(
    id       INT         NOT NULL COMMENT '유저 아이디'
        PRIMARY KEY,
    nickname VARCHAR(50) NOT NULL COMMENT '닉네임',
    CONSTRAINT FK_user_info_id_user_id
        FOREIGN KEY ( id ) REFERENCES user ( id )
)
    COMMENT '유저 정보' CHARSET = utf8mb4;

