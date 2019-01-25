package com.dalaoyang.receiver;

import com.dalaoyang.dao.GoodsDao;
import com.dalaoyang.dao.UserOrderDao;
import com.dalaoyang.entity.User;
import com.dalaoyang.entity.UserOrder;
import com.dalaoyang.service.GoodsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by pj on 2019/1/22.
 */
@Component
public class BasicMqListen {

    Logger log = LoggerFactory.getLogger(BasicMqListen.class);
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserOrderDao userOrderDao;

    @Autowired
    GoodsService goodsService;

    ReentrantLock lock = new ReentrantLock();

    @RabbitListener(queues = "${basic_info_mq_queue_name}",containerFactory = "singleListenerContainer")
    public void listenBasicStringMq(Message msg){

       String str = new String( msg.getBody());
       log.info(str);
    }

    //消息超时处理
    @RabbitListener(queues = "${user_order_dead_queue_name}",containerFactory = "multiListenerContainer")
    public void listenRealDeadUserOrderMq(Message msg){
        try {
            UserOrder userOrder = objectMapper.readValue(msg.getBody(), UserOrder.class);


            log.info("超时处理   " + userOrder.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //消息正常处理
    @RabbitListener(queues = "${user_order_queue_name}",containerFactory = "multiListenerContainer")
    @Transactional
    public void listenDeadUserOrderMq(Message msg){
        try {
            UserOrder userOrder = objectMapper.readValue(msg.getBody(), UserOrder.class);
//            lock.lock();
            if (goodsService.getGoodsNumByName("海飞丝") <= 0)
                return;
            if(goodsService.reduceGoodNum("海飞丝") > 0){
                userOrderDao.insertOneOrder(userOrder);
                log.info("强到一单");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("生成订单的时候出现错误");
        }finally {
//            lock.unlock();
        }

    }
}
