CREATE DATABASE IF NOT EXISTS trade_review_system
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE trade_review_system;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS trade_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    stock_code VARCHAR(20) NOT NULL,
    stock_name VARCHAR(50) NOT NULL,
    buy_time DATETIME,
    buy_price DECIMAL(10, 3),
    sell_time DATETIME,
    sell_price DECIMAL(10, 3),
    position_level INT COMMENT '仓位层级',
    stop_loss_price DECIMAL(10, 3),
    buy_reason TEXT,
    sell_reason TEXT,
    teacher_opinion TEXT COMMENT '老周观点',
    key_level TEXT COMMENT '关键位',
    profit_amount DECIMAL(12, 2),
    profit_rate DECIMAL(8, 4),
    is_pattern_trade TINYINT DEFAULT 1 COMMENT '1模式内，0模式外',
    trade_date DATE,
    total_buy_quantity INT COMMENT '累计买入数量',
    total_sell_quantity INT COMMENT '累计卖出数量',
    remaining_quantity INT COMMENT '剩余持仓数量',
    avg_buy_price DECIMAL(10, 3) COMMENT '平均买入价',
    avg_sell_price DECIMAL(10, 3) COMMENT '平均卖出价',
    position_status VARCHAR(30) COMMENT '持仓状态：OPEN/PARTIAL_CLOSED/CLOSED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stock_company (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    search_name VARCHAR(100) NOT NULL,
    market VARCHAR(20),
    exchange VARCHAR(50),
    source VARCHAR(100),
    fetched_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_stock_company_name (name),
    INDEX idx_stock_company_search_name (search_name)
);

CREATE TABLE IF NOT EXISTS trade_execution_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trade_id BIGINT NOT NULL COMMENT '关联交易记录ID',
    action_type VARCHAR(20) NOT NULL COMMENT 'BUY买入 / SELL卖出',
    execution_time DATETIME NOT NULL COMMENT '成交时间',
    price DECIMAL(10, 3) NOT NULL COMMENT '成交价格',
    quantity INT NOT NULL COMMENT '成交数量，单位股',
    position_note VARCHAR(100) COMMENT '仓位说明，例如1层、加1层、减半、清仓',
    reason TEXT COMMENT '本次成交理由',
    remark TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS mistake_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS trade_mistake_rel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trade_id BIGINT NOT NULL,
    mistake_tag_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS trade_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trade_id BIGINT NOT NULL,
    operation_process TEXT COMMENT '操作经过',
    original_plan TEXT COMMENT '当时计划',
    actual_execution TEXT COMMENT '实际执行',
    real_problem TEXT COMMENT '真正问题',
    improvement_rule VARCHAR(255) COMMENT '改进规则',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS weekly_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    week_start DATE NOT NULL,
    week_end DATE NOT NULL,
    start_capital DECIMAL(12, 2),
    end_capital DECIMAL(12, 2),
    profit_amount DECIMAL(12, 2),
    profit_rate DECIMAL(8, 4),
    trade_count BIGINT,
    win_count BIGINT,
    loss_count BIGINT,
    win_rate DECIMAL(8, 4),
    pattern_trade_count BIGINT,
    non_pattern_trade_count BIGINT,
    top_mistake_summary TEXT,
    biggest_win_trade VARCHAR(100),
    biggest_loss_trade VARCHAR(100),
    profit_source TEXT COMMENT '赚钱主要来自哪里',
    loss_source TEXT COMMENT '亏钱主要来自哪里',
    biggest_problem TEXT COMMENT '本周最致命问题',
    best_action TEXT COMMENT '本周做得最好的一点',
    rule_one VARCHAR(100),
    rule_two VARCHAR(100),
    rule_three VARCHAR(100),
    training_topic VARCHAR(100),
    training_method TEXT,
    execution_score INT COMMENT '执行评分 0-10',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO mistake_tag (name, description)
SELECT * FROM (
    SELECT '怕踏空追高', '位置已经较高，仍因怕错过而买入'
    UNION ALL SELECT '急跌恐慌卖', '没有跌破止损位，仅因急跌提前卖出'
    UNION ALL SELECT '破位后拖延', '跌破关键位后没有及时处理'
    UNION ALL SELECT '赚钱单无定性', '不清楚这笔交易赚哪一段钱'
    UNION ALL SELECT '反抽不过前高没处理', '冲高回落后反抽失败，但没有及时减仓'
    UNION ALL SELECT '分时均价线附近不敢买', '模式内回踩均价线，但不敢小仓试错'
    UNION ALL SELECT '没有提前写预案', '买前没有明确买点、止损和卖出条件'
    UNION ALL SELECT '仓位过重', '仓位超出试错范围，导致情绪压力变大'
    UNION ALL SELECT '模式外交易', '不符合计划和交易体系的操作'
    UNION ALL SELECT '等老周说法覆盖纪律', '已触发纪律，但等待外部确认导致执行变慢'
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM mistake_tag);
