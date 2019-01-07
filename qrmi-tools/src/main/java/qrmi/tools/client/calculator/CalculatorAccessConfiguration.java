/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.client.calculator;

import java.util.UUID;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import qrmi.core.RabbitRemoteLocator;
import qrmi.tools.api.Calculator;

/**
 * A Calculator client configuration for <code>QRMITool</code> to load, exposing
 * the Calculator API to this client.
 * 
 * @author thinh ho
 *
 */
@Configuration
public class CalculatorAccessConfiguration {
    @Autowired
    private ConnectionFactory connectionFactory;
    
    @Autowired
    private AmqpAdmin amqpAdmin;
    
    @Value("${qrmi.px}")
    private String applicationId;
    
    @Bean
    public RabbitRemoteLocator calculator() {
        RabbitRemoteLocator remoteLocator = new RabbitRemoteLocator(connectionFactory);
        // 
        remoteLocator.setServiceInterface(Calculator.class);
        remoteLocator.setRoutingKey(Calculator.class.getName());
        remoteLocator.setExchange("example.Calculator");
        // 
        remoteLocator.setAmqpAdmin(amqpAdmin);
        remoteLocator.setReplyTimeout(3000L);
        remoteLocator.setReplyExchange("qrmi.reply");
        remoteLocator.setReplyQueueNamingStrategy(() -> String.format("qrmi.%s.%s", applicationId, UUID.randomUUID().toString()));
        return remoteLocator;
    }
}
