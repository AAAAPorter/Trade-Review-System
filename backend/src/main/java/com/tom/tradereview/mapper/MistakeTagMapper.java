package com.tom.tradereview.mapper;

import com.tom.tradereview.entity.MistakeTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * mistake_tag 表访问接口。
 */
@Mapper
public interface MistakeTagMapper {
    @Select("SELECT id, name, description FROM mistake_tag ORDER BY id ASC")
    List<MistakeTag> selectAllOrderById();

    @Select("SELECT id, name, description FROM mistake_tag WHERE id = #{id}")
    MistakeTag selectById(Long id);

    @Insert("""
            INSERT INTO mistake_tag (name, description)
            VALUES (#{name}, #{description})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MistakeTag mistakeTag);

    @Update("""
            UPDATE mistake_tag
            SET name = #{name},
                description = #{description}
            WHERE id = #{id}
            """)
    int updateById(MistakeTag mistakeTag);

    @Delete("DELETE FROM mistake_tag WHERE id = #{id}")
    int deleteById(Long id);
}
