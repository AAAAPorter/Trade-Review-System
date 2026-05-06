package com.tom.tradereview.mapper;

import com.tom.tradereview.entity.TradeExecutionDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * trade_execution_detail 表访问接口。
 */
@Mapper
public interface TradeExecutionDetailMapper {
    String COLUMNS = """
            id, trade_id, action_type, execution_time, price, quantity,
            position_note, reason, remark, created_at, updated_at
            """;

    @Select("SELECT " + COLUMNS + " FROM trade_execution_detail WHERE id = #{id}")
    TradeExecutionDetail selectById(Long id);

    @Select("""
            SELECT """ + COLUMNS + """
            FROM trade_execution_detail
            WHERE trade_id = #{tradeId}
            ORDER BY execution_time ASC, id ASC
            """)
    List<TradeExecutionDetail> selectByTradeIdOrderByExecutionTime(Long tradeId);

    @Select("SELECT " + COLUMNS + " FROM trade_execution_detail WHERE trade_id = #{tradeId}")
    List<TradeExecutionDetail> selectByTradeId(Long tradeId);

    @Insert("""
            INSERT INTO trade_execution_detail
                (trade_id, action_type, execution_time, price, quantity, position_note, reason, remark)
            VALUES
                (#{tradeId}, #{actionType}, #{executionTime}, #{price}, #{quantity}, #{positionNote}, #{reason}, #{remark})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TradeExecutionDetail detail);

    @Insert("""
            <script>
            INSERT INTO trade_execution_detail
                (trade_id, action_type, execution_time, price, quantity, position_note, reason, remark)
            VALUES
            <foreach collection="details" item="detail" separator=",">
                (#{detail.tradeId}, #{detail.actionType}, #{detail.executionTime}, #{detail.price},
                 #{detail.quantity}, #{detail.positionNote}, #{detail.reason}, #{detail.remark})
            </foreach>
            </script>
            """)
    int insertBatch(@Param("details") List<TradeExecutionDetail> details);

    @Update("""
            UPDATE trade_execution_detail
            SET trade_id = #{tradeId},
                action_type = #{actionType},
                execution_time = #{executionTime},
                price = #{price},
                quantity = #{quantity},
                position_note = #{positionNote},
                reason = #{reason},
                remark = #{remark}
            WHERE id = #{id}
            """)
    int updateById(TradeExecutionDetail detail);

    @Delete("DELETE FROM trade_execution_detail WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM trade_execution_detail WHERE trade_id = #{tradeId}")
    int deleteByTradeId(Long tradeId);
}
