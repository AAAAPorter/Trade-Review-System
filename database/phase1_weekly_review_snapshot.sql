USE trade_review_system;

ALTER TABLE weekly_review
    ADD COLUMN trade_count BIGINT AFTER profit_rate,
    ADD COLUMN win_count BIGINT AFTER trade_count,
    ADD COLUMN loss_count BIGINT AFTER win_count,
    ADD COLUMN win_rate DECIMAL(8, 4) AFTER loss_count,
    ADD COLUMN pattern_trade_count BIGINT AFTER win_rate,
    ADD COLUMN non_pattern_trade_count BIGINT AFTER pattern_trade_count,
    ADD COLUMN top_mistake_summary TEXT AFTER non_pattern_trade_count,
    ADD COLUMN biggest_win_trade VARCHAR(100) AFTER top_mistake_summary,
    ADD COLUMN biggest_loss_trade VARCHAR(100) AFTER biggest_win_trade;
