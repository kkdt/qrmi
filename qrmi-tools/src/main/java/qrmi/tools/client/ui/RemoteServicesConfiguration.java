/*
 * Copyright (c) 2019. thinh ho
 * This file is part of 'qrmi-tools_main' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.client.ui;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import qrmi.api.QRMIRegistry;
import qrmi.core.RabbitExporterBuilder;
import qrmi.core.RabbitRemoteLocator;
import qrmi.support.ConnectionFactoryRegistryServiceListener;
import qrmi.tools.api.Calculator;

/**
 * A client configuration for <code>QRMITool</code> to load, exposing remote APIs.
 * 
 * @author thinh ho
 *
 */
@Configuration
public class RemoteServicesConfiguration {
    @Autowired
    private ConnectionFactory connectionFactory;
    
    @Autowired
    private AmqpAdmin amqpAdmin;
    
    @Value("${qrmi.px}")
    private String applicationId;
    
    @Bean
    public RabbitRemoteLocator calculator() {
        RabbitRemoteLocator remoteLocator = new RabbitRemoteLocator(amqpAdmin, connectionFactory);
        remoteLocator.setServiceInterface(Calculator.class);
        remoteLocator.setRoutingKey(Calculator.class.getName());
        remoteLocator.setExchange("example.Calculator");
        remoteLocator.setReplyTimeout(3000L);
//        remoteLocator.setReplyExchange("qrmi.reply");
//        remoteLocator.setReplyQueueNamingStrategy(() -> String.format("qrmi.%s.%s", applicationId, UUID.randomUUID().toString()));
        return remoteLocator;
    }

    @Bean
    public RabbitRemoteLocator registry() {
        RabbitRemoteLocator remoteLocator = new RabbitRemoteLocator(amqpAdmin, connectionFactory);
        remoteLocator.setServiceInterface(QRMIRegistry.class);
        remoteLocator.setRoutingKey(QRMIRegistry.class.getName());
        remoteLocator.setExchange("qrmi.service.registry");
        remoteLocator.setReplyTimeout(3000L);
//        remoteLocator.setReplyExchange("qrmi.reply");
//        remoteLocator.setReplyQueueNamingStrategy(() -> String.format("qrmi.%s.%s", applicationId, UUID.randomUUID().toString()));
        return remoteLocator;
    }

    @Bean
    public ConnectionFactoryRegistryServiceListener registryServiceListener() {
        Assert.isTrue(connectionFactory instanceof CachingConnectionFactory, "Expecting CachingConnectionFactory");
        ConnectionFactoryRegistryServiceListener b = new ConnectionFactoryRegistryServiceListener((CachingConnectionFactory)connectionFactory);
        new RabbitExporterBuilder(amqpAdmin, connectionFactory).build(b);
        return b;
    }
}
