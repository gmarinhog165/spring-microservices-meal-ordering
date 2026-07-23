CREATE TABLE menu_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10, 2) NOT NULL,
    restaurant_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (restaurant_id) REFERENCES restaurant(id)
);