# LINK_Fit Admin — DB 테이블 정의

MariaDB 기준 DDL. 문자셋: `utf8mb4`, 콜레이션: `utf8mb4_unicode_ci`.

---

## 실행 순서

외래키 의존성 순서대로 실행:

1. `admin_user`
2. `member`
3. `staff`
4. `product`
5. `membership`
6. `member_freeze`
7. `class_session`
8. `class_attendee`
9. `attendance`
10. `consult`
11. `message`
12. `message_recipient`
13. `sale`

---

## DDL

```sql
-- ─────────────────────────────────────────────────────────────
-- 1. admin_user  (어드민 로그인 계정)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE admin_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL,
    password    VARCHAR(255) NOT NULL,           -- BCrypt 해시
    name        VARCHAR(50)  NOT NULL,
    role        VARCHAR(20)  NOT NULL            -- SUPER_ADMIN, ADMIN, TRAINER
                    CHECK (role IN ('SUPER_ADMIN','ADMIN','TRAINER')),
    active      TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_admin_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- 2. member  (회원)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE member (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    name            VARCHAR(50)  NOT NULL,
    phone           VARCHAR(20)  NOT NULL,
    gender          VARCHAR(10)  NOT NULL            -- MALE, FEMALE
                        CHECK (gender IN ('MALE','FEMALE')),
    birth_date      DATE,
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
                        CHECK (status IN ('ACTIVE','EXPIRED','SUSPENDED')),
    join_date       DATE         NOT NULL,
    membership_end  DATE,
    memo            TEXT,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_member_status (status),
    KEY idx_member_name   (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- 3. staff  (직원 / 트레이너)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE staff (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(100),
    role        VARCHAR(20)  NOT NULL
                    CHECK (role IN ('SUPER_ADMIN','ADMIN','TRAINER')),
    hire_date   DATE         NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
                    CHECK (status IN ('ACTIVE','INACTIVE')),
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_staff_role   (role),
    KEY idx_staff_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- 4. product  (상품)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE product (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL,
    type            VARCHAR(20)  NOT NULL
                        CHECK (type IN ('MEMBERSHIP','GROUP','PT','LOCKER','ITEM')),
    price           INT          NOT NULL DEFAULT 0,
    duration_days   INT          NOT NULL DEFAULT 0,    -- 0: 기간 없음
    description     TEXT,
    active          TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_product_type   (type),
    KEY idx_product_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- 5. membership  (회원권 구매 이력)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE membership (
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    member_id   BIGINT   NOT NULL,
    product_id  BIGINT,
    type        VARCHAR(20)  NOT NULL
                    CHECK (type IN ('MEMBERSHIP','GROUP','PT','LOCKER','ITEM')),
    start_date  DATE     NOT NULL,
    end_date    DATE,
    price       INT      NOT NULL DEFAULT 0,    -- 실결제액 (할인 적용 후)
    memo        VARCHAR(255),
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_membership_member (member_id),
    CONSTRAINT fk_membership_member  FOREIGN KEY (member_id)  REFERENCES member  (id) ON DELETE CASCADE,
    CONSTRAINT fk_membership_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- 6. member_freeze  (유증 기록)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE member_freeze (
    id           BIGINT   NOT NULL AUTO_INCREMENT,
    member_id    BIGINT   NOT NULL,
    freeze_start DATE     NOT NULL,
    freeze_end   DATE     NOT NULL,
    reason       VARCHAR(255),
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_freeze_member (member_id),
    CONSTRAINT fk_freeze_member FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- 7. class_session  (수업)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE class_session (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    title        VARCHAR(100) NOT NULL,
    type         VARCHAR(10)  NOT NULL
                     CHECK (type IN ('GROUP','PT','OT')),
    category     VARCHAR(20)  NOT NULL
                     CHECK (category IN ('헬스','필라테스','골프','기타')),
    trainer_id   BIGINT,
    trainer_name VARCHAR(50),
    class_date   DATE         NOT NULL,
    start_time   TIME         NOT NULL,
    end_time     TIME         NOT NULL,
    capacity     INT          NOT NULL DEFAULT 1,
    enrolled     INT          NOT NULL DEFAULT 0,
    status       VARCHAR(20)  NOT NULL DEFAULT 'OPEN'
                     CHECK (status IN ('OPEN','CANCELLED')),
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_class_date     (class_date),
    KEY idx_class_trainer  (trainer_id),
    KEY idx_class_status   (status),
    CONSTRAINT fk_class_trainer FOREIGN KEY (trainer_id) REFERENCES staff (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- 8. class_attendee  (수업 신청자)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE class_attendee (
    id               BIGINT   NOT NULL AUTO_INCREMENT,
    class_session_id BIGINT   NOT NULL,
    member_id        BIGINT   NOT NULL,
    registered_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    attended         TINYINT(1) NOT NULL DEFAULT 0,   -- 실제 출석 여부
    PRIMARY KEY (id),
    UNIQUE KEY uq_class_attendee (class_session_id, member_id),
    KEY idx_attendee_member (member_id),
    CONSTRAINT fk_attendee_class  FOREIGN KEY (class_session_id) REFERENCES class_session (id) ON DELETE CASCADE,
    CONSTRAINT fk_attendee_member FOREIGN KEY (member_id)        REFERENCES member         (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- 9. attendance  (출석 체크)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE attendance (
    id             BIGINT   NOT NULL AUTO_INCREMENT,
    member_id      BIGINT   NOT NULL,
    type           VARCHAR(20)  NOT NULL
                       CHECK (type IN ('MEMBERSHIP','GROUP','PT')),
    attend_date    DATE     NOT NULL,
    check_in_time  TIME     NOT NULL,
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_attendance_member (member_id),
    KEY idx_attendance_date   (attend_date),
    CONSTRAINT fk_attendance_member FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- 10. consult  (상담)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE consult (
    id            BIGINT   NOT NULL AUTO_INCREMENT,
    type          VARCHAR(20)  NOT NULL
                      CHECK (type IN ('NEW','EXISTING')),
    name          VARCHAR(50)  NOT NULL,
    phone         VARCHAR(20),
    gender        VARCHAR(10)
                      CHECK (gender IN ('MALE','FEMALE')),
    member_id     BIGINT,                               -- EXISTING 상담인 경우 연결
    interest      VARCHAR(100),
    content       TEXT,
    result        VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                      CHECK (result IN ('REGISTERED','PENDING','NO_SHOW')),
    consult_date  DATE     NOT NULL,
    staff_id      BIGINT,
    staff_name    VARCHAR(50),
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_consult_date   (consult_date),
    KEY idx_consult_result (result),
    CONSTRAINT fk_consult_member FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE SET NULL,
    CONSTRAINT fk_consult_staff  FOREIGN KEY (staff_id)  REFERENCES staff  (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- 11. message  (메시지 발송)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE message (
    id               BIGINT   NOT NULL AUTO_INCREMENT,
    title            VARCHAR(200) NOT NULL,
    content          TEXT         NOT NULL,
    target_type      VARCHAR(20)  NOT NULL
                         CHECK (target_type IN ('ALL','MEMBER','INDIVIDUAL')),
    status           VARCHAR(20)  NOT NULL DEFAULT 'SCHEDULED'
                         CHECK (status IN ('SENT','SCHEDULED','FAILED')),
    sent_at          DATETIME,
    recipient_count  INT          NOT NULL DEFAULT 0,
    sender_id        BIGINT,
    sender_name      VARCHAR(50),
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_message_status  (status),
    KEY idx_message_sent_at (sent_at),
    CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES admin_user (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- 12. message_recipient  (개별 수신자, target_type=INDIVIDUAL)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE message_recipient (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    message_id  BIGINT NOT NULL,
    member_id   BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_msg_recipient (message_id, member_id),
    CONSTRAINT fk_msg_recipient_message FOREIGN KEY (message_id) REFERENCES message (id) ON DELETE CASCADE,
    CONSTRAINT fk_msg_recipient_member  FOREIGN KEY (member_id)  REFERENCES member  (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- 13. sale  (매출 내역)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE sale (
    id            BIGINT   NOT NULL AUTO_INCREMENT,
    member_id     BIGINT,
    membership_id BIGINT,
    product_id    BIGINT,
    product_type  VARCHAR(20)  NOT NULL
                      CHECK (product_type IN ('MEMBERSHIP','GROUP','PT','LOCKER','ITEM')),
    amount        INT      NOT NULL,
    payment_method VARCHAR(20) NOT NULL DEFAULT 'CARD'
                      CHECK (payment_method IN ('CARD','CASH','TRANSFER')),
    sale_date     DATE     NOT NULL,
    memo          VARCHAR(255),
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_sale_date   (sale_date),
    KEY idx_sale_member (member_id),
    CONSTRAINT fk_sale_member     FOREIGN KEY (member_id)     REFERENCES member     (id) ON DELETE SET NULL,
    CONSTRAINT fk_sale_membership FOREIGN KEY (membership_id) REFERENCES membership (id) ON DELETE SET NULL,
    CONSTRAINT fk_sale_product    FOREIGN KEY (product_id)    REFERENCES product    (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 초기 데이터

```sql
-- 기본 슈퍼어드민 계정 (비밀번호: admin123 → BCrypt)
INSERT INTO admin_user (username, password, name, role)
VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '관리자', 'SUPER_ADMIN');
```

---

## 테이블 관계 요약

```
admin_user
    └─ message (sender_id)

member
    ├─ membership (member_id)  ──▶ product
    ├─ member_freeze (member_id)
    ├─ attendance (member_id)
    ├─ class_attendee (member_id)  ──▶ class_session ──▶ staff (trainer_id)
    ├─ consult (member_id)  ──▶ staff (staff_id)
    └─ message_recipient (member_id)  ──▶ message

sale
    ├─ member (member_id)
    ├─ membership (membership_id)
    └─ product (product_id)
```

---

## MyBatis 매퍼 쿼리

각 도메인별 Mapper 인터페이스 메서드와 대응하는 SQL 쿼리 정의.

---

### MemberMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.linkfit.admin.mapper.MemberMapper">

    <resultMap id="memberResultMap" type="Member">
        <id     property="id"            column="id"/>
        <result property="name"          column="name"/>
        <result property="phone"         column="phone"/>
        <result property="gender"        column="gender"/>
        <result property="birthDate"     column="birth_date"/>
        <result property="status"        column="status"/>
        <result property="joinDate"      column="join_date"/>
        <result property="membershipEnd" column="membership_end"/>
        <result property="memo"          column="memo"/>
    </resultMap>

    <!-- 목록 (검색 + 상태 필터 + 페이지네이션) -->
    <select id="findAll" resultMap="memberResultMap">
        SELECT * FROM member
        <where>
            <if test="keyword != null and keyword != ''">
                AND (name LIKE CONCAT('%', #{keyword}, '%')
                  OR phone LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="status != null and status != ''">
                AND status = #{status}
            </if>
        </where>
        ORDER BY id DESC
        LIMIT #{size} OFFSET #{offset}
    </select>

    <select id="count" resultType="long">
        SELECT COUNT(*) FROM member
        <where>
            <if test="keyword != null and keyword != ''">
                AND (name LIKE CONCAT('%', #{keyword}, '%')
                  OR phone LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="status != null and status != ''">
                AND status = #{status}
            </if>
        </where>
    </select>

    <select id="findById" resultMap="memberResultMap">
        SELECT * FROM member WHERE id = #{id}
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO member (name, phone, gender, birth_date, status, join_date, membership_end, memo)
        VALUES (#{name}, #{phone}, #{gender}, #{birthDate}, #{status}, #{joinDate}, #{membershipEnd}, #{memo})
    </insert>

    <update id="update">
        UPDATE member
        SET name=#{name}, phone=#{phone}, gender=#{gender},
            birth_date=#{birthDate}, membership_end=#{membershipEnd}, memo=#{memo}
        WHERE id = #{id}
    </update>

    <delete id="delete">
        DELETE FROM member WHERE id = #{id}
    </delete>

    <update id="updateStatus">
        UPDATE member SET status = #{status} WHERE id = #{id}
    </update>

    <!-- 유증 등록 -->
    <insert id="insertFreeze">
        INSERT INTO member_freeze (member_id, freeze_start, freeze_end, reason)
        VALUES (#{memberId}, #{freezeStart}, #{freezeEnd}, #{reason})
    </insert>

    <!-- 유증 목록 (회원별) -->
    <select id="findFreezeByMemberId" resultType="map">
        SELECT id, freeze_start, freeze_end, reason, created_at
        FROM member_freeze
        WHERE member_id = #{memberId}
        ORDER BY freeze_start DESC
    </select>

    <!-- 회원권 구매 이력 (회원별) -->
    <select id="findMembershipsByMemberId" resultType="map">
        SELECT ms.id, ms.type, ms.start_date, ms.end_date, ms.price, ms.memo,
               p.name AS product_name
        FROM membership ms
        LEFT JOIN product p ON ms.product_id = p.id
        WHERE ms.member_id = #{memberId}
        ORDER BY ms.start_date DESC
    </select>

    <!-- 회원권 등록 -->
    <insert id="insertMembership" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO membership (member_id, product_id, type, start_date, end_date, price, memo)
        VALUES (#{memberId}, #{productId}, #{type}, #{startDate}, #{endDate}, #{price}, #{memo})
    </insert>

</mapper>
```

---

### StaffMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.linkfit.admin.mapper.StaffMapper">

    <resultMap id="staffResultMap" type="Staff">
        <id     property="id"       column="id"/>
        <result property="name"     column="name"/>
        <result property="phone"    column="phone"/>
        <result property="email"    column="email"/>
        <result property="role"     column="role"/>
        <result property="hireDate" column="hire_date"/>
        <result property="status"   column="status"/>
    </resultMap>

    <select id="findAll" resultMap="staffResultMap">
        SELECT * FROM staff
        <where>
            <if test="role != null and role != ''">
                AND role = #{role}
            </if>
        </where>
        ORDER BY id DESC
        LIMIT #{size} OFFSET #{offset}
    </select>

    <select id="count" resultType="long">
        SELECT COUNT(*) FROM staff
        <where>
            <if test="role != null and role != ''">
                AND role = #{role}
            </if>
        </where>
    </select>

    <select id="findById" resultMap="staffResultMap">
        SELECT * FROM staff WHERE id = #{id}
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO staff (name, phone, email, role, hire_date, status)
        VALUES (#{name}, #{phone}, #{email}, #{role}, #{hireDate}, #{status})
    </insert>

    <update id="update">
        UPDATE staff
        SET name=#{name}, phone=#{phone}, email=#{email}, role=#{role}
        WHERE id = #{id}
    </update>

    <delete id="delete">
        DELETE FROM staff WHERE id = #{id}
    </delete>

    <update id="updateRole">
        UPDATE staff SET role = #{role} WHERE id = #{id}
    </update>

</mapper>
```

---

### ClassMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.linkfit.admin.mapper.ClassMapper">

    <resultMap id="classResultMap" type="ClassSession">
        <id     property="id"          column="id"/>
        <result property="title"       column="title"/>
        <result property="type"        column="type"/>
        <result property="category"   column="category"/>
        <result property="trainerId"   column="trainer_id"/>
        <result property="trainerName" column="trainer_name"/>
        <result property="classDate"   column="class_date"/>
        <result property="startTime"   column="start_time"/>
        <result property="endTime"     column="end_time"/>
        <result property="capacity"    column="capacity"/>
        <result property="enrolled"    column="enrolled"/>
        <result property="status"      column="status"/>
    </resultMap>

    <!-- 목록 (type 필터 + 날짜 필터 + 페이지네이션) -->
    <select id="findAll" resultMap="classResultMap">
        SELECT * FROM class_session
        <where>
            <if test="type != null and type != ''">
                AND type = #{type}
            </if>
            <if test="date != null and date != ''">
                AND class_date = #{date}
            </if>
        </where>
        ORDER BY class_date DESC, start_time ASC
        LIMIT #{size} OFFSET #{offset}
    </select>

    <select id="count" resultType="long">
        SELECT COUNT(*) FROM class_session
        <where>
            <if test="type != null and type != ''">
                AND type = #{type}
            </if>
            <if test="date != null and date != ''">
                AND class_date = #{date}
            </if>
        </where>
    </select>

    <select id="findById" resultMap="classResultMap">
        SELECT * FROM class_session WHERE id = #{id}
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO class_session
            (title, type, category, trainer_id, trainer_name,
             class_date, start_time, end_time, capacity, enrolled, status)
        VALUES
            (#{title}, #{type}, #{category}, #{trainerId}, #{trainerName},
             #{classDate}, #{startTime}, #{endTime}, #{capacity}, 0, 'OPEN')
    </insert>

    <update id="update">
        UPDATE class_session
        SET title=#{title}, type=#{type}, category=#{category},
            trainer_id=#{trainerId}, trainer_name=#{trainerName},
            class_date=#{classDate}, start_time=#{startTime}, end_time=#{endTime},
            capacity=#{capacity}
        WHERE id = #{id}
    </update>

    <!-- 수업 취소 -->
    <update id="cancel">
        UPDATE class_session SET status = 'CANCELLED' WHERE id = #{id}
    </update>

    <!-- 수업 신청 -->
    <insert id="enroll">
        INSERT INTO class_attendee (class_session_id, member_id)
        VALUES (#{classId}, #{memberId})
    </insert>

    <!-- enrolled 카운트 증가 -->
    <update id="incrementEnrolled">
        UPDATE class_session SET enrolled = enrolled + 1 WHERE id = #{id}
    </update>

    <!-- 수업 신청 취소 -->
    <delete id="cancelEnrollment">
        DELETE FROM class_attendee
        WHERE class_session_id = #{classId} AND member_id = #{memberId}
    </delete>

    <!-- enrolled 카운트 감소 -->
    <update id="decrementEnrolled">
        UPDATE class_session SET enrolled = enrolled - 1 WHERE id = #{id}
    </update>

    <!-- 수업 신청자 목록 -->
    <select id="findAttendees" resultType="map">
        SELECT ca.id, ca.member_id, m.name AS member_name, m.phone, m.gender,
               ca.registered_at, ca.attended
        FROM class_attendee ca
        JOIN member m ON ca.member_id = m.id
        WHERE ca.class_session_id = #{classId}
        ORDER BY ca.registered_at ASC
    </select>

</mapper>
```

---

### AttendanceMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.linkfit.admin.mapper.AttendanceMapper">

    <resultMap id="attendanceResultMap" type="Attendance">
        <id     property="id"           column="id"/>
        <result property="memberId"     column="member_id"/>
        <result property="memberName"   column="member_name"/>
        <result property="memberGender" column="gender"/>
        <result property="type"         column="type"/>
        <result property="attendDate"   column="attend_date"/>
        <result property="checkInTime"  column="check_in_time"/>
    </resultMap>

    <!-- 출석 목록 (날짜/기간별) -->
    <select id="findAll" resultMap="attendanceResultMap">
        SELECT a.id, a.member_id, m.name AS member_name, m.gender,
               a.type, a.attend_date, a.check_in_time
        FROM attendance a
        JOIN member m ON a.member_id = m.id
        <where>
            <choose>
                <when test="period == 'weekly'">
                    AND a.attend_date BETWEEN DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY)
                                          AND DATE_ADD(DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY), INTERVAL 6 DAY)
                </when>
                <when test="period == 'monthly'">
                    AND DATE_FORMAT(a.attend_date, '%Y-%m') = DATE_FORMAT(#{date}, '%Y-%m')
                </when>
                <otherwise>
                    AND a.attend_date = #{date}
                </otherwise>
            </choose>
        </where>
        ORDER BY a.attend_date DESC, a.check_in_time DESC
    </select>

    <!-- 출석 체크인 -->
    <insert id="checkIn" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO attendance (member_id, type, attend_date, check_in_time)
        VALUES (#{memberId}, #{type}, #{attendDate}, #{checkInTime})
    </insert>

    <!-- 출석 취소 -->
    <delete id="cancel">
        DELETE FROM attendance WHERE id = #{id}
    </delete>

    <!-- 유증 중인 회원 출석 조회 (freeze 기간 내 날짜 기준) -->
    <select id="findFrozen" resultType="map">
        SELECT m.id AS member_id, m.name, m.gender,
               f.freeze_start, f.freeze_end, f.reason
        FROM member_freeze f
        JOIN member m ON f.member_id = m.id
        WHERE #{date} BETWEEN f.freeze_start AND f.freeze_end
        ORDER BY m.name ASC
    </select>

    <!-- 대시보드: 날짜/기간별 출석 통계 -->
    <select id="countStats" resultType="map">
        SELECT
            COUNT(*) AS total,
            SUM(CASE WHEN m.gender = 'MALE'   THEN 1 ELSE 0 END) AS male,
            SUM(CASE WHEN m.gender = 'FEMALE' THEN 1 ELSE 0 END) AS female
        FROM attendance a
        JOIN member m ON a.member_id = m.id
        <where>
            <choose>
                <when test="period == 'weekly'">
                    AND a.attend_date BETWEEN DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY)
                                          AND DATE_ADD(DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY), INTERVAL 6 DAY)
                </when>
                <when test="period == 'monthly'">
                    AND DATE_FORMAT(a.attend_date, '%Y-%m') = DATE_FORMAT(#{date}, '%Y-%m')
                </when>
                <otherwise>
                    AND a.attend_date = #{date}
                </otherwise>
            </choose>
            <if test="type != null and type != ''">
                AND a.type = #{type}
            </if>
        </where>
    </select>

    <!-- 대시보드: 날짜 기준 유증 회원 수 -->
    <select id="countFrozen" resultType="map">
        SELECT
            COUNT(*) AS frozen,
            SUM(CASE WHEN m.gender = 'MALE'   THEN 1 ELSE 0 END) AS frozenMale,
            SUM(CASE WHEN m.gender = 'FEMALE' THEN 1 ELSE 0 END) AS frozenFemale
        FROM member_freeze f
        JOIN member m ON f.member_id = m.id
        WHERE #{date} BETWEEN f.freeze_start AND f.freeze_end
    </select>

</mapper>
```

---

### ConsultMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.linkfit.admin.mapper.ConsultMapper">

    <resultMap id="consultResultMap" type="Consult">
        <id     property="id"          column="id"/>
        <result property="type"        column="type"/>
        <result property="name"        column="name"/>
        <result property="phone"       column="phone"/>
        <result property="gender"      column="gender"/>
        <result property="memberId"    column="member_id"/>
        <result property="interest"    column="interest"/>
        <result property="content"     column="content"/>
        <result property="result"      column="result"/>
        <result property="consultDate" column="consult_date"/>
        <result property="staffName"   column="staff_name"/>
    </resultMap>

    <select id="findAll" resultMap="consultResultMap">
        SELECT * FROM consult
        <where>
            <if test="type != null and type != ''">
                AND type = #{type}
            </if>
        </where>
        ORDER BY consult_date DESC, id DESC
        LIMIT #{size} OFFSET #{offset}
    </select>

    <select id="count" resultType="long">
        SELECT COUNT(*) FROM consult
        <where>
            <if test="type != null and type != ''">
                AND type = #{type}
            </if>
        </where>
    </select>

    <select id="findById" resultMap="consultResultMap">
        SELECT * FROM consult WHERE id = #{id}
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO consult
            (type, name, phone, gender, member_id, interest, content, result, consult_date, staff_id, staff_name)
        VALUES
            (#{type}, #{name}, #{phone}, #{gender}, #{memberId}, #{interest}, #{content}, #{result}, #{consultDate}, #{staffId}, #{staffName})
    </insert>

    <update id="update">
        UPDATE consult
        SET type=#{type}, name=#{name}, phone=#{phone}, gender=#{gender},
            interest=#{interest}, content=#{content}, result=#{result},
            consult_date=#{consultDate}, staff_name=#{staffName}
        WHERE id = #{id}
    </update>

    <delete id="delete">
        DELETE FROM consult WHERE id = #{id}
    </delete>

    <!-- 대시보드: 날짜/기간별 상담 통계 -->
    <select id="countStats" resultType="map">
        SELECT
            SUM(CASE WHEN type = 'NEW'      THEN 1 ELSE 0 END) AS newConsult,
            SUM(CASE WHEN type = 'EXISTING' THEN 1 ELSE 0 END) AS existingConsult
        FROM consult
        <where>
            <choose>
                <when test="period == 'weekly'">
                    AND consult_date BETWEEN DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY)
                                        AND DATE_ADD(DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY), INTERVAL 6 DAY)
                </when>
                <when test="period == 'monthly'">
                    AND DATE_FORMAT(consult_date, '%Y-%m') = DATE_FORMAT(#{date}, '%Y-%m')
                </when>
                <otherwise>
                    AND consult_date = #{date}
                </otherwise>
            </choose>
        </where>
    </select>

</mapper>
```

---

### ProductMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.linkfit.admin.mapper.ProductMapper">

    <resultMap id="productResultMap" type="Product">
        <id     property="id"           column="id"/>
        <result property="name"         column="name"/>
        <result property="type"         column="type"/>
        <result property="price"        column="price"/>
        <result property="durationDays" column="duration_days"/>
        <result property="description"  column="description"/>
        <result property="active"       column="active"/>
    </resultMap>

    <select id="findAll" resultMap="productResultMap">
        SELECT * FROM product
        <where>
            <if test="type != null and type != ''">
                AND type = #{type}
            </if>
        </where>
        ORDER BY type ASC, name ASC
        LIMIT #{size} OFFSET #{offset}
    </select>

    <select id="count" resultType="long">
        SELECT COUNT(*) FROM product
        <where>
            <if test="type != null and type != ''">
                AND type = #{type}
            </if>
        </where>
    </select>

    <select id="findById" resultMap="productResultMap">
        SELECT * FROM product WHERE id = #{id}
    </select>

    <!-- 활성 상품만 조회 (회원권 등록 폼용) -->
    <select id="findAllActive" resultMap="productResultMap">
        SELECT * FROM product WHERE active = 1 ORDER BY type ASC, name ASC
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO product (name, type, price, duration_days, description, active)
        VALUES (#{name}, #{type}, #{price}, #{durationDays}, #{description}, #{active})
    </insert>

    <update id="update">
        UPDATE product
        SET name=#{name}, type=#{type}, price=#{price},
            duration_days=#{durationDays}, description=#{description}, active=#{active}
        WHERE id = #{id}
    </update>

    <delete id="delete">
        DELETE FROM product WHERE id = #{id}
    </delete>

</mapper>
```

---

### MessageMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.linkfit.admin.mapper.MessageMapper">

    <resultMap id="messageResultMap" type="Message">
        <id     property="id"             column="id"/>
        <result property="title"          column="title"/>
        <result property="content"        column="content"/>
        <result property="targetType"     column="target_type"/>
        <result property="status"         column="status"/>
        <result property="sentAt"         column="sent_at"/>
        <result property="recipientCount" column="recipient_count"/>
        <result property="senderName"     column="sender_name"/>
    </resultMap>

    <select id="findAll" resultMap="messageResultMap">
        SELECT * FROM message
        ORDER BY created_at DESC
        LIMIT #{size} OFFSET #{offset}
    </select>

    <select id="count" resultType="long">
        SELECT COUNT(*) FROM message
    </select>

    <select id="findById" resultMap="messageResultMap">
        SELECT * FROM message WHERE id = #{id}
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO message
            (title, content, target_type, status, sent_at, recipient_count, sender_id, sender_name)
        VALUES
            (#{title}, #{content}, #{targetType}, #{status}, #{sentAt}, #{recipientCount}, #{senderId}, #{senderName})
    </insert>

    <!-- 개별 수신자 일괄 등록 -->
    <insert id="insertRecipients">
        INSERT INTO message_recipient (message_id, member_id) VALUES
        <foreach collection="memberIds" item="memberId" separator=",">
            (#{messageId}, #{memberId})
        </foreach>
    </insert>

    <delete id="delete">
        DELETE FROM message WHERE id = #{id}
    </delete>

</mapper>
```

---

### DashboardMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.linkfit.admin.mapper.DashboardMapper">

    <!-- 회원 현황 (상태별 + 성별 집계) -->
    <select id="memberStats" resultType="map">
        SELECT
            SUM(CASE WHEN status = 'ACTIVE'    THEN 1 ELSE 0 END) AS active,
            SUM(CASE WHEN status = 'EXPIRED'   THEN 1 ELSE 0 END) AS expired,
            SUM(CASE WHEN status = 'SUSPENDED' THEN 1 ELSE 0 END) AS suspended,
            SUM(CASE WHEN status = 'ACTIVE'   AND gender = 'MALE'   THEN 1 ELSE 0 END) AS maleActive,
            SUM(CASE WHEN status = 'ACTIVE'   AND gender = 'FEMALE' THEN 1 ELSE 0 END) AS femaleActive,
            SUM(CASE WHEN status = 'EXPIRED'  AND gender = 'MALE'   THEN 1 ELSE 0 END) AS maleExpired,
            SUM(CASE WHEN status = 'EXPIRED'  AND gender = 'FEMALE' THEN 1 ELSE 0 END) AS femaleExpired
        FROM member
    </select>

    <!-- 기간별 신규/재등록 회원 수 -->
    <select id="memberJoinStats" resultType="map">
        SELECT
            COUNT(*) AS newJoin
        FROM member
        <where>
            <choose>
                <when test="period == 'weekly'">
                    AND join_date BETWEEN DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY)
                                     AND DATE_ADD(DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY), INTERVAL 6 DAY)
                </when>
                <when test="period == 'monthly'">
                    AND DATE_FORMAT(join_date, '%Y-%m') = DATE_FORMAT(#{date}, '%Y-%m')
                </when>
                <otherwise>
                    AND join_date = #{date}
                </otherwise>
            </choose>
        </where>
    </select>

    <!-- 수업 카테고리별 통계 -->
    <select id="classStats" resultType="map">
        SELECT
            category,
            COUNT(*) AS cnt,
            SUM(enrolled) AS enrolled
        FROM class_session
        <where>
            AND status = 'OPEN'
            <choose>
                <when test="period == 'weekly'">
                    AND class_date BETWEEN DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY)
                                      AND DATE_ADD(DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY), INTERVAL 6 DAY)
                </when>
                <when test="period == 'monthly'">
                    AND DATE_FORMAT(class_date, '%Y-%m') = DATE_FORMAT(#{date}, '%Y-%m')
                </when>
                <otherwise>
                    AND class_date = #{date}
                </otherwise>
            </choose>
        </where>
        GROUP BY category
    </select>

    <!-- 매출 요약 (상품 타입별 합계) -->
    <select id="revenueStats" resultType="map">
        SELECT
            SUM(CASE WHEN product_type = 'MEMBERSHIP' THEN amount ELSE 0 END) AS membership,
            SUM(CASE WHEN product_type = 'GROUP'      THEN amount ELSE 0 END) AS groupClass,
            SUM(CASE WHEN product_type = 'PT'         THEN amount ELSE 0 END) AS pt,
            SUM(CASE WHEN product_type = 'LOCKER'     THEN amount ELSE 0 END) AS locker,
            SUM(CASE WHEN product_type = 'ITEM'       THEN amount ELSE 0 END) AS items,
            SUM(amount) AS total
        FROM sale
        <where>
            <choose>
                <when test="period == 'weekly'">
                    AND sale_date BETWEEN DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY)
                                     AND DATE_ADD(DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY), INTERVAL 6 DAY)
                </when>
                <when test="period == 'monthly'">
                    AND DATE_FORMAT(sale_date, '%Y-%m') = DATE_FORMAT(#{date}, '%Y-%m')
                </when>
                <otherwise>
                    AND sale_date = #{date}
                </otherwise>
            </choose>
        </where>
    </select>

    <!-- 매출 카테고리별 상세 내역 -->
    <select id="revenueDetail" resultType="map">
        SELECT s.id, s.amount, s.payment_method, s.sale_date, s.memo,
               m.name AS member_name, p.name AS product_name
        FROM sale s
        LEFT JOIN member m ON s.member_id = m.id
        LEFT JOIN product p ON s.product_id = p.id
        WHERE s.product_type = #{category}
        <choose>
            <when test="period == 'weekly'">
                AND s.sale_date BETWEEN DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY)
                                    AND DATE_ADD(DATE_SUB(#{date}, INTERVAL WEEKDAY(#{date}) DAY), INTERVAL 6 DAY)
            </when>
            <when test="period == 'monthly'">
                AND DATE_FORMAT(s.sale_date, '%Y-%m') = DATE_FORMAT(#{date}, '%Y-%m')
            </when>
            <otherwise>
                AND s.sale_date = #{date}
            </otherwise>
        </choose>
        ORDER BY s.sale_date DESC, s.id DESC
    </select>

</mapper>
```
