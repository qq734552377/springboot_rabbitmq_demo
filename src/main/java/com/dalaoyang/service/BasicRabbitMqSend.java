package com.dalaoyang.service;

import com.dalaoyang.config.RabbitMQConfig;
import com.dalaoyang.entity.UserOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * Created by pj on 2019/1/22.
 */
@Service
public class BasicRabbitMqSend {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    RabbitMQConfig rabbitMQConfig;

    private static final Logger log= LoggerFactory.getLogger(BasicRabbitMqSend.class);

    public void sendBasicMqString(String msg){
        try {
        rabbitTemplate.setExchange(rabbitMQConfig.basicInfoExchange);
        rabbitTemplate.setRoutingKey(rabbitMQConfig.basicInfoRouting);

        Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                                        .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                                        .build();

        rabbitTemplate.send(message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void testUserOrder(UserOrder user){
        try {
//            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(rabbitMQConfig.userOrderExchange);
            rabbitTemplate.setRoutingKey(rabbitMQConfig.userOrderRouting);
            Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(user))
                                            .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                                            .build();

            rabbitTemplate.send(message);
        }catch (Exception e){
            log.error("发送抢单信息入队列发生异常： mobile={} ",user.getOrderNo());
        }
    }

}
