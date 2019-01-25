package com.dalaoyang.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pj on 2019/1/22.
 */
@Configuration
public class RabbitMQConfig {
    private  final Logger log= LoggerFactory.getLogger(RabbitMQConfig.class);

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    @Value("${spring.rabbitmq.listener.concurrency}")
    private int concurrencyNum ;
    @Value("${spring.rabbitmq.listener.max-concurrency}")
    private int maxConcurrencyNum ;
    @Value("${spring.rabbitmq.listener.prefetch}")
    private int prefetchNum ;


    /**
     * 单一消费者
     * @return
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setTxSize(1);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }

    /**
     * 多个消费者
     * @return
     */
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory,connectionFactory);
//        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setConcurrentConsumers(concurrencyNum);
        factory.setMaxConcurrentConsumers(maxConcurrencyNum);
        factory.setPrefetchCount(prefetchNum);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(){
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",exchange,routingKey,replyCode,replyText,message);
            }
        });
        return rabbitTemplate;
    }

    @Value("${basic_info_mq_queue_name}")
    public  String basicInfoQueue;
    @Value("${basic_info_mq_exchange_name}")
    public  String basicInfoExchange;
    @Value("${basic_info_mq_routing_key_name}")
    public  String basicInfoRouting;


    @Bean
    public DirectExchange basicExchange(){
        return new DirectExchange(basicInfoExchange, true,false);
    }

    @Bean(name = "basicQueue")
    public Queue basicQueue(){
        return new Queue(basicInfoQueue, true);
    }

    @Bean
    public Binding basicBinding(){
        return BindingBuilder.bind(basicQueue()).to(basicExchange()).with(basicInfoRouting);
    }

    @Value("${user_order_queue_name}")
    public  String userOrderQueue;
    @Value("${user_order_exchange_name}")
    public  String userOrderExchange;
    @Value("${user_order_routing_key_name}")
    public  String userOrderRouting;


    @Value("${user_order_dead_queue_name}")
    public  String userOrderDeadQueue;
    @Value("${user_order_dead_exchange_name}")
    public  String userOrderDeadExchange;
    @Value("${user_order_dead_routing_key_name}")
    public  String userOrderDeadRouting;




    //TODO：用户下单支付超时死信队列模型

    @Bean
    public Queue userOrderDeadQueue(){
        Map<String, Object> args=new HashMap();
        args.put("x-dead-letter-exchange",userOrderDeadExchange);
        args.put("x-dead-letter-routing-key",userOrderDeadRouting);
        args.put("x-message-ttl",20000);

        return new Queue(userOrderQueue,true,false,false,args);
    }

    //绑定死信队列-面向生产端
    @Bean
    public TopicExchange userOrderDeadExchange(){
        return new TopicExchange(userOrderExchange,true,false);
    }

    @Bean
    public Binding userOrderDeadBinding(){
        return BindingBuilder.bind(userOrderDeadQueue()).to(userOrderDeadExchange()).with(userOrderRouting);
    }

    //创建并绑定实际监听消费队列-面向消费端
    @Bean
    public Queue userOrderDeadRealQueue(){
        return new Queue(userOrderDeadQueue,true);
    }


    @Bean
    public TopicExchange userOrderDeadRealExchange(){
        return new TopicExchange(userOrderDeadExchange);
    }

    @Bean
    public Binding userOrderDeadRealBinding(){
        return BindingBuilder.bind(userOrderDeadRealQueue()).to(userOrderDeadRealExchange()).with(userOrderDeadRouting);
    }

}
