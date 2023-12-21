CREATE TABLE IF NOT EXISTS admin
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(128)  NOT NULL,
    last_name  VARCHAR(128)  NOT NULL,
    phone      VARCHAR(32)   NOT NULL UNIQUE,
    email      VARCHAR(128)  NOT NULL UNIQUE,
    password   VARCHAR(64)   NOT NULL,
    role       VARCHAR(32)   NOT NULL,
    shift_rate NUMERIC(8, 2) NOT NULL,
    avatar     VARCHAR(128)
);
-- rollback DROP TABLE admin

CREATE TABLE IF NOT EXISTS orders
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    status         VARCHAR(64) NOT NULL,
    order_name     VARCHAR(64),
    client_name    VARCHAR(64),
    phone          VARCHAR(32) NOT NULL,
    request_source VARCHAR(512),
    created_at     TIMESTAMP   NOT NULL,
    admin_id       INT REFERENCES admin (id)
);
-- rollback DROP TABLE orders;

CREATE TABLE IF NOT EXISTS comment
(
    text     VARCHAR(256),
    added_at TIMESTAMP NOT NULL,
    order_id INT REFERENCES orders (id),
    PRIMARY KEY (added_at, order_id)
);
-- rollback DROP TABLE comment;

CREATE TABLE IF NOT EXISTS log_info
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    action_type VARCHAR(256),
    description VARCHAR(256),
    created_at  TIMESTAMP NOT NULL,
    admin_id    INT REFERENCES admin (id),
    order_id    INT REFERENCES orders (id)
);
--rollback DROP TABLE log_info;

CREATE TABLE IF NOT EXISTS subject
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);
-- rollback DROP TABLE subject;

CREATE TABLE IF NOT EXISTS student
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(128) NOT NULL,
    last_name  VARCHAR(128),
    phone      VARCHAR(32)  NOT NULL,
    email      VARCHAR(128) UNIQUE,
    password   VARCHAR(64)  NOT NULL,
    role       VARCHAR(32)  NOT NULL,
    subject_id INT REFERENCES subject (id),
    avatar     VARCHAR(128)
);
-- rollback DROP TABLE student;

CREATE TABLE IF NOT EXISTS abonement_type
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);
-- rollback DROP TABLE abonement_type;

CREATE TABLE IF NOT EXISTS teacher_salary
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(128),
    rate NUMERIC(10, 2) NOT NULL
);
--rollback DROP TABLE teacher_salary;

CREATE TABLE IF NOT EXISTS abonement
(
    id                INT AUTO_INCREMENT PRIMARY KEY,
    number_of_lessons INT     NOT NULL,
    type_id           INT REFERENCES abonement_type (id),
    begin             DATE,
    expire            DATE,
    status            VARCHAR NOT NULL,
    student_id        INT REFERENCES student (id)
);
-- rollback DROP TABLE abonement;

CREATE TABLE IF NOT EXISTS teacher
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(128) NOT NULL,
    last_name       VARCHAR(128) NOT NULL,
    phone           VARCHAR(32)  NOT NULL,
    email           VARCHAR(128) NOT NULL,
    password        VARCHAR(64)  NOT NULL,
    role            VARCHAR(32)  NOT NULL,
    salary_per_hour INT REFERENCES teacher_salary (id),
    subject_id      INT REFERENCES subject (id),
    status          VARCHAR(32)  NOT NULL,
    avatar          VARCHAR(128)
);
-- rollback DROP TABLE teacher;

CREATE TABLE IF NOT EXISTS lesson
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    student_id  INT         NOT NULL REFERENCES student (id),
    teacher_id  INT         NOT NULL REFERENCES teacher (id),
    date_time   TIMESTAMP   NOT NULL,
    duration    INT         NOT NULL,
    subject_id  INT         NOT NULL REFERENCES subject (id),
    status      VARCHAR(64) NOT NULL,
    type        VARCHAR(64),
    description VARCHAR(128),
    cost        NUMERIC(10, 2)
);
--rollback DROP TABLE lesson;

CREATE TABLE IF NOT EXISTS teachers_subject
(
    teacher_id INT REFERENCES teacher (id),
    subject_id INT REFERENCES subject (id),
    PRIMARY KEY (teacher_id, subject_id)
);
--rollback DROP TABLE teachers_subject;