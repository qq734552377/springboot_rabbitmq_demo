package com.dalaoyang.dao;

import com.dalaoyang.entity.UserOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by pj on 2019/1/23.
 */
@Mapper
public interface UserOrderDao {

    @Select("INSERT INTO user_order (order_no,user_id,status,create_time,update_time) VALUE " +
            "(#{o.orderNo},#{o.userId},#{o.status},#{o.createTime},#{o.updateTime});")
    void insertOneOrder(@Param("o") UserOrder order);
}
