package com.dalaoyang.controller;

import com.dalaoyang.dao.GoodsDao;
import com.dalaoyang.entity.UserOrder;
import com.dalaoyang.sender.Sender;
import com.dalaoyang.service.BasicRabbitMqSend;
import com.dalaoyang.service.GoodsService;
import com.dalaoyang.service.InitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author dalaoyang
 * @Description
 * @project springboot_learn
 * @package com.dalaoyang.controller
 * @email yangyang@dalaoyang.cn
 * @date 2018/4/25
 */
@RestController
public class TestController {

    @Autowired
    private Sender sender;
    @Autowired
    private BasicRabbitMqSend basicRabbitMqSend;
    @Autowired
    GoodsService goodsService;


    @GetMapping("hello")
    public String helloTest(){
//        sender.send();
        basicRabbitMqSend.sendBasicMqString("this is test  看看中文");
        return "success";
    }
    int i = 1;

    @GetMapping("testOrder")
    public String testDead(){
        int goodsNum = goodsService.getGoodsNumByName("海飞丝");
        if (goodsNum <= 0)
            return "没有商品了";
        UserOrder userOrder = new UserOrder();
        userOrder.setUserId(i%6);
        userOrder.setOrderNo(i + "");
        userOrder.setStatus(0);
        userOrder.setCreateTime(new Date());
        userOrder.setUpdateTime(new Date());
        basicRabbitMqSend.testUserOrder(userOrder);
        i++;
        return "success";
    }

    @Autowired
    InitService initService;

    @GetMapping("bingfa")
    public String testBingfa(){
        initService.generateMultiThread();
        return "success";
    }
    @GetMapping("testNum")
    public String testGoodsNum(){
        goodsService.reduceGoodNum("海飞丝");
        return   "间了一下";
    }

    @GetMapping("clearCache")
    public String clear(){
        goodsService.clearCache();
        return "clear Ok!";
    }

}
