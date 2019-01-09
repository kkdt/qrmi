/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.service.lottery.publisher;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import qrmi.core.RabbitConsumerLocator;
import qrmi.tools.api.Lottery;

/**
 * App that exposes two {@code Lottery} proxies and then schedule them to invoke
 * the broadcast.
 * 
 * @author thinh ho
 * @see LotterySchedule
 *
 */
@Configuration
public class LotteryPublisherConfiguration {
    @Bean
    public RabbitConsumerLocator vaLottery(AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory) {
        RabbitConsumerLocator l = new RabbitConsumerLocator(amqpAdmin, connectionFactory);
        l.setServiceInterface(Lottery.class);
        l.setExchange("example.Lottery");
        l.setRoutingKey("Virginia");
        return l;
    }
    
    @Bean
    public RabbitConsumerLocator mdLottery(AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory) {
        RabbitConsumerLocator l = new RabbitConsumerLocator(amqpAdmin, connectionFactory);
        l.setServiceInterface(Lottery.class);
        l.setExchange("example.Lottery");
        l.setRoutingKey("Maryland");
        return l;
    }
}
