package com.tom.tradereview.mapper;

import com.tom.tradereview.entity.StockCompany;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * stock_company 表访问接口。
 */
@Mapper
public interface StockCompanyMapper {
    @Select("""
            SELECT code, name, search_name, market, exchange, source, fetched_at, created_at, updated_at
            FROM stock_company
            WHERE search_name LIKE CONCAT('%', #{searchName}, '%')
               OR code LIKE CONCAT('%', #{text}, '%')
               OR name LIKE CONCAT('%', #{text}, '%')
            ORDER BY code ASC
            LIMIT ${limit}
            """)
    List<StockCompany> search(
            @Param("searchName") String searchName,
            @Param("text") String text,
            @Param("limit") int limit
    );
}
