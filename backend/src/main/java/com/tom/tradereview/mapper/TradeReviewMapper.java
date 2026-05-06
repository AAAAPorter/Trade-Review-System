package com.tom.tradereview.mapper;

import com.tom.tradereview.entity.TradeReview;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * trade_review 表访问接口。
 */
@Mapper
public interface TradeReviewMapper {
    String COLUMNS = """
            id, trade_id, operation_process, original_plan, actual_execution,
            real_problem, improvement_rule, created_at, updated_at
            """;

    @Select("SELECT " + COLUMNS + " FROM trade_review WHERE id = #{id}")
    TradeReview selectById(Long id);

    @Select("SELECT " + COLUMNS + " FROM trade_review WHERE trade_id = #{tradeId} LIMIT 1")
    TradeReview selectByTradeId(Long tradeId);

    @Insert("""
            INSERT INTO trade_review
                (trade_id, operation_process, original_plan, actual_execution, real_problem, improvement_rule)
            VALUES
                (#{tradeId}, #{operationProcess}, #{originalPlan}, #{actualExecution}, #{realProblem}, #{improvementRule})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TradeReview tradeReview);

    @Update("""
            UPDATE trade_review
            SET trade_id = #{tradeId},
                operation_process = #{operationProcess},
                original_plan = #{originalPlan},
                actual_execution = #{actualExecution},
                real_problem = #{realProblem},
                improvement_rule = #{improvementRule}
            WHERE id = #{id}
            """)
    int updateById(TradeReview tradeReview);

    @Delete("DELETE FROM trade_review WHERE trade_id = #{tradeId}")
    int deleteByTradeId(Long tradeId);
}
