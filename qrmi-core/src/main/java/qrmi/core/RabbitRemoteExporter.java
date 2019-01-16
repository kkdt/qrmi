/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.core;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeTypes;

import java.util.Objects;

/**
 * Builder for exporting an API implementation to RabbitMQ.
 * 
 * @author thinh ho
 *
 */
public class RabbitRemoteExporter extends RabbitExporter {
    
    public RabbitRemoteExporter() {}
    
    @Override
    protected boolean supportExchange(Exchange exchange) {
        if(Objects.isNull(exchange) || Objects.isNull(exchange.getType())) return false;
        return ExchangeTypes.TOPIC.equals(exchange.getType()) || ExchangeTypes.DIRECT.equals(exchange.getType());
    }
}
