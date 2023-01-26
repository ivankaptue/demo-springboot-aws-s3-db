-- Stores
CREATE TABLE s3_stores
(
    id     VARCHAR(255) NOT NULL,
    name   VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    CONSTRAINT pk_s3_stores PRIMARY KEY (id)
);

ALTER TABLE s3_stores
    ADD CONSTRAINT uc_s3_stores_id UNIQUE (id);

ALTER TABLE s3_stores
    ADD CONSTRAINT uc_s3_stores_name UNIQUE (name);

-- Sales
CREATE TABLE s3_sales
(
    id       VARCHAR(255) NOT NULL,
    product  VARCHAR(255) NOT NULL,
    quantity SMALLINT     NOT NULL,
    price    DECIMAL      NOT NULL,
    store_id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_s3_sales PRIMARY KEY (id)
);

ALTER TABLE s3_sales
    ADD CONSTRAINT uc_s3_sales_id UNIQUE (id);

ALTER TABLE s3_sales
    ADD CONSTRAINT FK_S3_SALES_ON_STORE FOREIGN KEY (store_id) REFERENCES s3_stores (id);
