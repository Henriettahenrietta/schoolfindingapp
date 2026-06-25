-- School Finder — initial schema

CREATE TABLE app_user (
    id           BIGSERIAL PRIMARY KEY,
    firebase_uid VARCHAR(128) UNIQUE NOT NULL,
    email        VARCHAR(255),
    display_name VARCHAR(255),
    role         VARCHAR(20)  NOT NULL DEFAULT 'STUDENT',  -- STUDENT | ADMIN
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE school (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    category        VARCHAR(30)  NOT NULL,        -- PRIMARY | SECONDARY | HIGH_SCHOOL | VOCATIONAL | UNIVERSITY
    description     TEXT,
    city            VARCHAR(120),
    region          VARCHAR(120),
    address         VARCHAR(255),
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    tuition_fee     NUMERIC(12,2),
    currency        VARCHAR(8)   NOT NULL DEFAULT 'XAF',
    website         VARCHAR(255),
    phone           VARCHAR(60),
    email           VARCHAR(255),
    cover_image_url VARCHAR(500),
    approved        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_school_city     ON school (city);
CREATE INDEX idx_school_category ON school (category);
CREATE INDEX idx_school_name     ON school (lower(name));

CREATE TABLE program (
    id              BIGSERIAL PRIMARY KEY,
    school_id       BIGINT NOT NULL REFERENCES school(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    level           VARCHAR(60),                 -- e.g. Bachelor, Diploma, A-Level
    duration_months INT,
    tuition_fee     NUMERIC(12,2)
);

CREATE INDEX idx_program_school ON program (school_id);

CREATE TABLE school_image (
    id        BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES school(id) ON DELETE CASCADE,
    url       VARCHAR(500) NOT NULL,
    caption   VARCHAR(255)
);

CREATE INDEX idx_school_image_school ON school_image (school_id);

CREATE TABLE review (
    id         BIGSERIAL PRIMARY KEY,
    school_id  BIGINT NOT NULL REFERENCES school(id)   ON DELETE CASCADE,
    user_id    BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    rating     SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment    TEXT,
    status     VARCHAR(20) NOT NULL DEFAULT 'APPROVED', -- PENDING | APPROVED | REJECTED
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_review_user_school UNIQUE (user_id, school_id)
);

CREATE INDEX idx_review_school ON review (school_id);

CREATE TABLE favorite (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    school_id  BIGINT NOT NULL REFERENCES school(id)   ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_favorite_user_school UNIQUE (user_id, school_id)
);

CREATE INDEX idx_favorite_user ON favorite (user_id);
