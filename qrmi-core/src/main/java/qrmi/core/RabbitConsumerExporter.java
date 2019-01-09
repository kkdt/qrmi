/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.core;

import java.util.Objects;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeTypes;

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
        
        if(ExchangeTypes.TOPIC.equals(exchange.getType()) || ExchangeTypes.FANOUT.equals(exchange.getType())) 
        {
            return true;
        }
        
        return false;
    }

}
