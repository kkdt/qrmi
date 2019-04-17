/*
 * Copyright (c) 2019. thinh ho
 * This file is part of 'qregistry_main' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */

package qrmi.registry;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import qrmi.api.QRMIRegistryListener;
import qrmi.core.RabbitConsumerLocator;

@Configuration
public class QServiceDiscoveryConfiguration {

    @Bean
    public RabbitConsumerLocator heartbeat(AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory) {
        RabbitConsumerLocator l = new RabbitConsumerLocator(amqpAdmin, connectionFactory);
        l.setServiceInterface(QRMIRegistryListener.class);
        l.setExchange("qrmi.service.registry.heartbeat");
        l.setRoutingKey("heartbeat");
        return l;
    }
}
