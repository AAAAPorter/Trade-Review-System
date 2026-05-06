package com.tom.tradereview.mapper;

import com.tom.tradereview.entity.TradeRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

/**
 * trade_record 表访问接口。
 *
 * <p>原生 MyBatis 不再继承通用 Mapper，业务需要的 SQL 在这里显式声明。</p>
 */
@Mapper
public interface TradeRecordMapper {
    String COLUMNS = """
            id, stock_code, stock_name, buy_time, buy_price, sell_time, sell_price,
            position_level, stop_loss_price, buy_reason, sell_reason, teacher_opinion,
            key_level, profit_amount, profit_rate, is_pattern_trade, trade_date,
            total_buy_quantity, total_sell_quantity, remaining_quantity, avg_buy_price,
            avg_sell_price, position_status, created_at, updated_at
            """;

    @Select("""
            <script>
            SELECT ${columns}
            FROM trade_record
            WHERE 1 = 1
            <if test="startDate != null">
                AND trade_date &gt;= #{startDate}
            </if>
            <if test="endDate != null">
                AND trade_date &lt;= #{endDate}
            </if>
            <if test="stockName != null and stockName != ''">
                AND stock_name LIKE CONCAT('%', #{stockName}, '%')
            </if>
            <if test="isPatternTrade != null">
                AND is_pattern_trade = #{isPatternTrade}
            </if>
            ORDER BY trade_date DESC, id DESC
            </script>
            """)
    List<TradeRecord> selectList(
            @Param("columns") String columns,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("stockName") String stockName,
            @Param("isPatternTrade") Integer isPatternTrade
    );

    @Select("SELECT " + COLUMNS + " FROM trade_record WHERE id = #{id}")
    TradeRecord selectById(Long id);

    @Insert("""
            INSERT INTO trade_record (stock_code, stock_name, is_pattern_trade, teacher_opinion, key_level)
            VALUES (#{stockCode}, #{stockName}, #{isPatternTrade}, #{teacherOpinion}, #{keyLevel})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TradeRecord tradeRecord);

    @Update("""
            UPDATE trade_record
            SET stock_code = #{stockCode},
                stock_name = #{stockName},
                is_pattern_trade = #{isPatternTrade},
                teacher_opinion = #{teacherOpinion},
                key_level = #{keyLevel}
            WHERE id = #{id}
            """)
    int updateEditableById(TradeRecord tradeRecord);

    @Update("""
            UPDATE trade_record
            SET buy_time = #{buyTime},
                buy_price = #{buyPrice},
                sell_time = #{sellTime},
                sell_price = #{sellPrice},
                trade_date = #{tradeDate},
                total_buy_quantity = #{totalBuyQuantity},
                total_sell_quantity = #{totalSellQuantity},
                remaining_quantity = #{remainingQuantity},
                avg_buy_price = #{avgBuyPrice},
                avg_sell_price = #{avgSellPrice},
                position_status = #{positionStatus},
                profit_amount = #{profitAmount},
                profit_rate = #{profitRate}
            WHERE id = #{id}
            """)
    int updateSummaryById(TradeRecord tradeRecord);

    @Delete("DELETE FROM trade_record WHERE id = #{id}")
    int deleteById(Long id);
}
