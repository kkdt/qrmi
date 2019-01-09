/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.core;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

/**
 * Locates a broadcast API exported to RabbitMQ.
 * 
 * @author thinh ho
 *
 */
public class RabbitConsumerLocator extends RabbitObjectLocator {

    public RabbitConsumerLocator(AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory) {
        super(amqpAdmin, connectionFactory);
    }

    @Override
    protected void locateObject() {
        // nothing special in this case
    }
}
