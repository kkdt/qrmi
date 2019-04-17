/**
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.core;

import java.util.Objects;
import java.util.Optional;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;

/**
 * Binds a consumer backed by a broadcast API to RabbitMQ.
 * 
 * @author thinh ho
 *
 */
public class RabbitConsumerExporter extends RabbitExporter {
    
    @Override
    protected boolean supportExchange(Exchange exchange) {
        if(Objects.isNull(exchange) || Objects.isNull(exchange.getType())) return false;
        return ExchangeTypes.TOPIC.equals(exchange.getType()) || ExchangeTypes.FANOUT.equals(exchange.getType());
    }

    @Override
    protected AmqpInvokerServiceExporter createMessageListener() {
        AmqpInvokerServiceExporter exporter = new RabbitConsumerMessageListener();
        exporter.setServiceInterface(remoteInterface);
        exporter.setService(remoteService);
        exporter.setAmqpTemplate(Optional.ofNullable(rabbitTemplate)
            .orElse(new RabbitTemplate(connectionFactory)));
        return exporter;
    }

}
