package com.dalaoyang.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * Created by pj on 2019/1/23.
 */
@Mapper
public interface GoodsDao {

    @Select("SELECT count FROM goods WHERE goods_name = #{name};")
    int getGoodsNumByName(@Param("name")String good_name);

    @Update("UPDATE goods SET count = count - 1 WHERE goods_name = #{name} AND count > 0;")
    int cutdownOneGoodsNumByName(@Param("name")String good_name);

    @Update("UPDATE goods SET count = number WHERE goods_name = #{name};")
    int updateGoodsNumByName(@Param("number")int num);
}
