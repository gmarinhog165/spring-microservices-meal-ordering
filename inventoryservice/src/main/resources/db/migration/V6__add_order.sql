CREATE TABLE orders (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    total_price DECIMAL(10, 2) NOT NULL,
    placed_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES customer(id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurant(id)
);

CREATE TABLE order_items (
    order_id     BIGINT       NOT NULL,
    item_id      BIGINT       NOT NULL,
    quantity     INT          NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (order_id, item_id),
    CONSTRAINT fk_order       FOREIGN KEY (order_id) REFERENCES orders(id)
);
