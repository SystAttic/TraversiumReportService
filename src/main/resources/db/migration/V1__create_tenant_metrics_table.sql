CREATE TABLE IF NOT EXISTS tenant_metrics (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    metric_date TIMESTAMP WITH TIME ZONE NOT NULL,
    total_users BIGINT NOT NULL DEFAULT 0,
    active_users BIGINT NOT NULL DEFAULT 0,
    total_trips BIGINT NOT NULL DEFAULT 0,
    total_storage_bytes BIGINT NOT NULL DEFAULT 0,
    total_api_calls BIGINT NOT NULL DEFAULT 0,
    calculated_cost DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tenant_metrics_tenant_date ON tenant_metrics(tenant_id, metric_date DESC);
CREATE INDEX idx_tenant_metrics_tenant ON tenant_metrics(tenant_id);

