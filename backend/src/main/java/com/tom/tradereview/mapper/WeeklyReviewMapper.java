package com.tom.tradereview.mapper;

import com.tom.tradereview.entity.WeeklyReview;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * weekly_review 表访问接口。
 */
@Mapper
public interface WeeklyReviewMapper {
    String COLUMNS = """
            id, week_start, week_end, start_capital, end_capital, profit_amount,
            profit_rate, trade_count, win_count, loss_count, win_rate, pattern_trade_count,
            non_pattern_trade_count, top_mistake_summary, biggest_win_trade, biggest_loss_trade,
            profit_source, loss_source, biggest_problem, best_action, rule_one, rule_two,
            rule_three, training_topic, training_method, execution_score, created_at, updated_at
            """;

    @Select("SELECT " + COLUMNS + " FROM weekly_review ORDER BY week_start DESC")
    List<WeeklyReview> selectAllOrderByWeekStartDesc();

    @Select("SELECT " + COLUMNS + " FROM weekly_review WHERE id = #{id}")
    WeeklyReview selectById(Long id);

    @Select("SELECT " + COLUMNS + " FROM weekly_review ORDER BY week_start DESC LIMIT 1")
    WeeklyReview selectLatest();

    @Insert("""
            INSERT INTO weekly_review
                (week_start, week_end, start_capital, end_capital, profit_amount, profit_rate,
                 trade_count, win_count, loss_count, win_rate, pattern_trade_count, non_pattern_trade_count,
                 top_mistake_summary, biggest_win_trade, biggest_loss_trade, profit_source, loss_source,
                 biggest_problem, best_action, rule_one, rule_two, rule_three, training_topic,
                 training_method, execution_score)
            VALUES
                (#{weekStart}, #{weekEnd}, #{startCapital}, #{endCapital}, #{profitAmount}, #{profitRate},
                 #{tradeCount}, #{winCount}, #{lossCount}, #{winRate}, #{patternTradeCount}, #{nonPatternTradeCount},
                 #{topMistakeSummary}, #{biggestWinTrade}, #{biggestLossTrade}, #{profitSource}, #{lossSource},
                 #{biggestProblem}, #{bestAction}, #{ruleOne}, #{ruleTwo}, #{ruleThree}, #{trainingTopic},
                 #{trainingMethod}, #{executionScore})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WeeklyReview weeklyReview);

    @Update("""
            UPDATE weekly_review
            SET week_start = #{weekStart},
                week_end = #{weekEnd},
                start_capital = #{startCapital},
                end_capital = #{endCapital},
                profit_amount = #{profitAmount},
                profit_rate = #{profitRate},
                trade_count = #{tradeCount},
                win_count = #{winCount},
                loss_count = #{lossCount},
                win_rate = #{winRate},
                pattern_trade_count = #{patternTradeCount},
                non_pattern_trade_count = #{nonPatternTradeCount},
                top_mistake_summary = #{topMistakeSummary},
                biggest_win_trade = #{biggestWinTrade},
                biggest_loss_trade = #{biggestLossTrade},
                profit_source = #{profitSource},
                loss_source = #{lossSource},
                biggest_problem = #{biggestProblem},
                best_action = #{bestAction},
                rule_one = #{ruleOne},
                rule_two = #{ruleTwo},
                rule_three = #{ruleThree},
                training_topic = #{trainingTopic},
                training_method = #{trainingMethod},
                execution_score = #{executionScore}
            WHERE id = #{id}
            """)
    int updateById(WeeklyReview weeklyReview);
}
