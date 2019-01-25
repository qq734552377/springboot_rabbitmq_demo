package com.dalaoyang.service;

import com.dalaoyang.dao.GoodsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Created by pj on 2019/1/24.
 */
@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    @Cacheable(value = "goodsNum",key = "#name")
    public int getGoodsNumByName(String name){
       return goodsDao.getGoodsNumByName(name);
    }

    @CacheEvict(value = "goodsNum",key = "#name")
    public int reduceGoodNum(String name){
       return goodsDao.cutdownOneGoodsNumByName(name);
    }
    @CacheEvict(value = "goodsNum",allEntries = true)
    public int updateNum(int num){
       return goodsDao.updateGoodsNumByName(num);
    }

    @CacheEvict(value = "goodsNum",allEntries = true)
    public void clearCache(){

    }
}
