CREATE DATABASE stocksim_db;
CREATE USER IF NOT EXISTS 'stocksim'@'%' IDENTIFIED BY '0584e79bb4c3632fd22b5f3da1136bb2620d1c55';
GRANT SELECT ON stocksim_db.* TO 'stocksim'@'%';
GRANT DELETE ON stocksim_db.* TO 'stocksim'@'%';
GRANT INSERT ON stocksim_db.* TO 'stocksim'@'%';
GRANT UPDATE ON stocksim_db.* TO 'stocksim'@'%';
GRANT CREATE TEMPORARY TABLES ON stocksim_db.* TO 'stocksim'@'%';
FLUSH PRIVILEGES;

USE stocksim_db;

CREATE TABLE copartnerships (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64),
    sale_price BIGINT,
    purchase_price BIGINT
);

CREATE UNIQUE INDEX copartnerships_name ON copartnerships(name);

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64),
    password VARCHAR(60),
    cash BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX user_username ON users(username);

CREATE TABLE stocks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ‘count‘ BIGINT DEFAULT 1,
    copartnership BIGINT,
    FOREIGN KEY (copartnership) REFERENCES copartnerships(id)
);

CREATE TABLE exchange_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user BIGINT,
    stock BIGINT NOT NULL,
    price BIGINT,
    FOREIGN KEY (user) REFERENCES users(id),
    FOREIGN KEY (stock) REFERENCES stocks(id)
);

CREATE INDEX exchange_history_user ON exchange_history(user);

CREATE TABLE offers (
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
     user BIGINT,
     copartnership BIGINT,
     price BIGINT,
     count BIGINT,
     is_sale_offer BOOLEAN DEFAULT FALSE,
     FOREIGN KEY (user) REFERENCES users(id),
     FOREIGN KEY (copartnership) REFERENCES copartnerships(id)
);

CREATE VIEW last_exchange_history AS
    SELECT
        MAX(id) AS exchange_id
    FROM
        exchange_history
    GROUP BY
        stock;

CREATE VIEW wallets AS
    SELECT
        CAST(ROUND(RAND()*1000000000000) AS INT) AS id,
        users.id AS user_id,
        copartnerships.id AS copartnership_id,
        COUNT(*) AS count,
        IFNULL(AVG(exchange_history.price), 0) AS aver_price
    FROM
        users
    JOIN
        exchange_history ON (users.id = exchange_history.user)
    JOIN
        last_exchange_history ON (last_exchange_history.exchange_id = exchange_history.id)
    JOIN
        stocks ON (exchange_history.stock = stocks.id)
    JOIN
        copartnerships ON (stocks.copartnership = copartnerships.id)
    GROUP BY
        copartnerships.id, users.id;

CREATE TABLE auth_tokens (
    hash varchar(128) PRIMARY KEY,
    user BIGINT,
    expire_time BIGINT DEFAULT NULL,
    FOREIGN KEY (user) REFERENCES users(id)
);

CREATE INDEX auth_tokens_expire_time ON auth_tokens(expire_time);


CREATE VIEW valid_tokens AS
    SELECT * FROM auth_tokens WHERE expire_time < UNIX_TIMESTAMP();

CREATE EVENT delete_old_tokens
    ON SCHEDULE EVERY 1 HOUR DO
    DELETE FROM auth_tokens WHERE UNIX_TIMESTAMP() < expire_time;

