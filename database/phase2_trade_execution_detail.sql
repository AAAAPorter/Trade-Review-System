USE trade_review_system;

ALTER TABLE trade_record
    ADD COLUMN total_buy_quantity INT COMMENT 'total buy quantity',
    ADD COLUMN total_sell_quantity INT COMMENT 'total sell quantity',
    ADD COLUMN remaining_quantity INT COMMENT 'remaining quantity',
    ADD COLUMN avg_buy_price DECIMAL(10, 3) COMMENT 'average buy price',
    ADD COLUMN avg_sell_price DECIMAL(10, 3) COMMENT 'average sell price',
    ADD COLUMN position_status VARCHAR(30) COMMENT 'OPEN/PARTIAL_CLOSED/CLOSED';

CREATE TABLE IF NOT EXISTS trade_execution_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trade_id BIGINT NOT NULL COMMENT 'trade record id',
    action_type VARCHAR(20) NOT NULL COMMENT 'BUY or SELL',
    execution_time DATETIME NOT NULL COMMENT 'execution time',
    price DECIMAL(10, 3) NOT NULL COMMENT 'execution price',
    quantity INT NOT NULL COMMENT 'quantity in shares',
    position_note VARCHAR(100) COMMENT 'position note',
    reason TEXT COMMENT 'execution reason',
    remark TEXT COMMENT 'remark',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
