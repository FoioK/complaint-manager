CREATE TABLE complaints
(
    id            BIGSERIAL PRIMARY KEY,
    product_id    BIGINT       NOT NULL,
    content       TEXT         NOT NULL,
    creation_date TIMESTAMP    NOT NULL,
    complainant   VARCHAR(255) NOT NULL,
    country       CHAR(16)     NOT NULL,
    claim_counter INT          NOT NULL
);

CREATE INDEX idx_complaints_product_id ON complaints (product_id);
