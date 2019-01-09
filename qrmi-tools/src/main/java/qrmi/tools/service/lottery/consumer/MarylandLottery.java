/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.service.lottery.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;

import qrmi.core.annotation.RabbitConsumer;
import qrmi.tools.api.Lottery;
import qrmi.tools.api.LotteryResult;

@RabbitConsumer(
    name = "Lottery",
    remoteInterface = Lottery.class,
    binding = @QueueBinding(
        exchange = @Exchange(
            name = "example.Lottery",
            autoDelete = Exchange.TRUE,
            declare = Exchange.TRUE,
            durable = Exchange.TRUE,
            type = ExchangeTypes.TOPIC),
        value = @Queue(
            autoDelete = Exchange.TRUE,
            declare = Exchange.TRUE,
            durable = Exchange.TRUE,
            exclusive = Exchange.TRUE),
        key = {"Maryland"})
)
public class MarylandLottery implements Lottery {
    private static final Logger logger = LoggerFactory.getLogger(MarylandLottery.class);
    
    @Override
    public void resultsAvailable(LotteryResult results) {
        logger.info("Received new lottery numbers: [{}]", results != null ? results.toString(): "N/A");
    }

}