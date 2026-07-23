CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    total_price DECIMAL(10, 2) NOT NULL,
    placed_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE order_items (
    order_id     BIGINT       NOT NULL,
    item_id      BIGINT       NOT NULL,
    quantity     INT          NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (order_id, item_id),
    CONSTRAINT fk_order       FOREIGN KEY (order_id) REFERENCES orders(id)
);
