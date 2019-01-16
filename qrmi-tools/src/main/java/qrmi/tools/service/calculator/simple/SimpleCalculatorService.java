/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.service.calculator.simple;

import java.util.UUID;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import qrmi.api.QRMIRegistryAware;
import qrmi.api.QRMIRegistry;
import qrmi.core.RabbitConsumerLocator;
import qrmi.core.RabbitExportConfiguration;
import qrmi.core.RabbitRemoteLocator;

/**
 * Nothing special here but a configuration to let <code>QRMITool</code> run as a
 * separate application.
 * 
 * <p>
 * The key line is the import of {@code RabbitObjectsConfiguration} so that it
 * automatically load up <code>RabbitRemote</code> to RabbitMQ.
 * </p>
 * 
 * @author thinh ho
 *
 */
@Configuration
@Import(value = {RabbitExportConfiguration.class})
public class SimpleCalculatorService {
    
    @Autowired
    private AmqpAdmin amqpAdmin;
    
    @Autowired
    private ConnectionFactory connectionFactory;
    
    @Bean
    public RabbitRemoteLocator registry() {
        RabbitRemoteLocator remoteLocator = new RabbitRemoteLocator(amqpAdmin, connectionFactory);
        // 
        remoteLocator.setServiceInterface(QRMIRegistry.class);
        remoteLocator.setRoutingKey(QRMIRegistry.class.getName());
        remoteLocator.setExchange("qrmi.service.registry");
        // 
        remoteLocator.setReplyTimeout(3000L);
        remoteLocator.setReplyExchange("qrmi.reply");
        remoteLocator.setReplyQueueNamingStrategy(() -> String.format("qrmi.%s.%s", SimpleCalculatorService.class.getSimpleName(), UUID.randomUUID().toString()));
        return remoteLocator;
    }
    
    @Bean
    public RabbitConsumerLocator registryBroadcast() {
        RabbitConsumerLocator l = new RabbitConsumerLocator(amqpAdmin, connectionFactory);
        l.setServiceInterface(QRMIRegistryAware.class);
        l.setExchange("qrmi.service.available");
        l.setRoutingKey(QRMIRegistryAware.class.getName());
        return l;
    }
}
