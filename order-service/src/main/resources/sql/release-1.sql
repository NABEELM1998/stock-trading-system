--create database
CREATE DATABASE order_service_db
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;

--create required tables
-- Order related enums
CREATE TYPE order_side AS ENUM ('BUY', 'SELL');
CREATE TYPE order_type AS ENUM ('MARKET', 'LIMIT');
CREATE TYPE order_status AS ENUM ('PENDING', 'VALIDATED', 'FILLED', 'REJECTED', 'CANCELLED');

-- User related enums
CREATE TYPE user_role AS ENUM ('TRADER', 'ADMIN');

-- Wallet related enums
CREATE TYPE transaction_type AS ENUM (
    'DEPOSIT',
    'WITHDRAWAL',
    'ORDER_DEBIT',
    'ORDER_CREDIT',
    'REFUND'
);


CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(100) NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   TEXT NOT NULL,
    role            user_role NOT NULL DEFAULT 'TRADER',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);



CREATE TABLE orders (
    order_id        BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    symbol          VARCHAR(10) NOT NULL,
    side            order_side NOT NULL,
    quantity        INTEGER  NOT NULL CHECK (quantity > 0),
    order_type      order_type NOT NULL,
    limit_price     NUMERIC(18, 4),
    status          order_status NOT NULL DEFAULT 'PENDING',
    workflow_id     VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- MARKET orders must not have limit_price
    CHECK (
        (order_type = 'MARKET' AND limit_price IS NULL)
        OR
        (order_type = 'LIMIT' AND limit_price IS NOT NULL)
    )
);



CREATE TABLE order_history (
    id              BIGSERIAL PRIMARY KEY,
    order_id        BIGINT NOT NULL REFERENCES orders(order_id),
    status          order_status NOT NULL,
    reason          TEXT,
    changed_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);


CREATE TABLE user_wallet (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    balance         NUMERIC(18, 4) NOT NULL CHECK (balance >= 0),
    currency        VARCHAR(10) NOT NULL DEFAULT 'USD',
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    UNIQUE (user_id, currency)
);


CREATE TABLE user_wallet_history (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    transaction_type transaction_type NOT NULL,
    amount          NUMERIC(18, 4) NOT NULL CHECK (amount > 0),
    balance_before  NUMERIC(18, 4) NOT NULL,
    balance_after   NUMERIC(18, 4) NOT NULL,
    order_id        BIGINT REFERENCES orders(order_id),
    description     TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);







