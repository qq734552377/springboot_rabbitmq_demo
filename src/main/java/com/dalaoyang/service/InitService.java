package com.dalaoyang.service;

import com.dalaoyang.entity.UserOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by steadyjack on 2018/8/24.
 */
@Service
public class InitService {
    private static final Logger log= LoggerFactory.getLogger(InitService.class);

    public static final int ThreadNum = 2000;

    private static int mobile=0;



    @Autowired
    private BasicRabbitMqSend basicRabbitMqSend;

    public void generateMultiThread(){
        log.info("开始初始化线程数----> ");

        try {
            CountDownLatch countDownLatch=new CountDownLatch(1);
//            for (int i=0;i<ThreadNum;i++){
//                new Thread(new RunThread(countDownLatch,i + 1)).start();
//            }

            for (int i = 0; i < ThreadNum; i++) {
                UserOrder userOrder = new UserOrder();
                userOrder.setUserId(i%6);
                userOrder.setOrderNo(i + "");
                userOrder.setStatus(0);
                userOrder.setCreateTime(new Date());
                userOrder.setUpdateTime(new Date());
                basicRabbitMqSend.testUserOrder(userOrder);
            }

            //TODO：启动多个线程
            countDownLatch.countDown();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class RunThread implements Runnable{
        private final CountDownLatch startLatch;
        private int num;

        public RunThread(CountDownLatch startLatch,int num) {
            this.startLatch = startLatch;
            this.num = num;
        }

        public void run() {
            try {
                //TODO：线程等待
                startLatch.await();
                mobile += 1;
                UserOrder userOrder = new UserOrder();
                userOrder.setUserId(this.num%6);
                userOrder.setOrderNo(this.num + "");
                userOrder.setStatus(0);
                userOrder.setCreateTime(new Date());
                userOrder.setUpdateTime(new Date());
                basicRabbitMqSend.testUserOrder(userOrder);

                //concurrencyService.manageRobbing(String.valueOf(mobile));//--v1.0
                //commonMqService.sendRobbingMsg(String.valueOf(mobile));//+v2.0
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
