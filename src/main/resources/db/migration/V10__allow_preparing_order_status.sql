ALTER TABLE orders DROP CONSTRAINT fk_orders_status;

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_status
        CHECK (status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'DELIVERED', 'CANCELLED'));
