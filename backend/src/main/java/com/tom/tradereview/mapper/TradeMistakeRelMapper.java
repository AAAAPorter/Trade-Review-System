package com.tom.tradereview.mapper;

import com.tom.tradereview.entity.TradeMistakeRel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * trade_mistake_rel 表访问接口。
 */
@Mapper
public interface TradeMistakeRelMapper {
    @Select("""
            SELECT id, trade_id, mistake_tag_id
            FROM trade_mistake_rel
            WHERE trade_id = #{tradeId}
            """)
    List<TradeMistakeRel> selectByTradeId(Long tradeId);

    @Select("""
            <script>
            SELECT id, trade_id, mistake_tag_id
            FROM trade_mistake_rel
            WHERE trade_id IN
            <foreach collection="tradeIds" item="tradeId" open="(" separator="," close=")">
                #{tradeId}
            </foreach>
            </script>
            """)
    List<TradeMistakeRel> selectByTradeIds(@Param("tradeIds") List<Long> tradeIds);

    @Delete("DELETE FROM trade_mistake_rel WHERE trade_id = #{tradeId}")
    int deleteByTradeId(Long tradeId);

    @Delete("DELETE FROM trade_mistake_rel WHERE mistake_tag_id = #{mistakeTagId}")
    int deleteByMistakeTagId(Long mistakeTagId);

    @Insert("""
            <script>
            INSERT INTO trade_mistake_rel (trade_id, mistake_tag_id)
            VALUES
            <foreach collection="relations" item="rel" separator=",">
                (#{rel.tradeId}, #{rel.mistakeTagId})
            </foreach>
            </script>
            """)
    int insertBatch(@Param("relations") List<TradeMistakeRel> relations);
}
