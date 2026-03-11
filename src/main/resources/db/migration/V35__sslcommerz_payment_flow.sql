ALTER TABLE payment_transaction
    ADD COLUMN IF NOT EXISTS plan_code VARCHAR(30),
    ADD COLUMN IF NOT EXISTS checkout_source VARCHAR(30),
    ADD COLUMN IF NOT EXISTS payment_provider VARCHAR(50) NOT NULL DEFAULT 'MOCK',
    ADD COLUMN IF NOT EXISTS gateway_session_key VARCHAR(255),
    ADD COLUMN IF NOT EXISTS validation_reference VARCHAR(255),
    ADD COLUMN IF NOT EXISTS gateway_status VARCHAR(100);

CREATE UNIQUE INDEX IF NOT EXISTS uq_payment_transaction_gateway_reference
    ON payment_transaction(gateway_reference);
