/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.registry;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import qrmi.api.QRMIRegistry;
import qrmi.api.QRMIRegistryAware;
import qrmi.core.annotation.RabbitConsumer;
import qrmi.support.RabbitObjectMetadata;

/**
 * Listens for services that want to bind to the registry.
 */
@RabbitConsumer(
    name = "SimpleRegistryServiceListener",
    description = "Simple listener for API/service availability ",
    remoteInterface = QRMIRegistryAware.class,
    binding = @QueueBinding(
        exchange = @Exchange(
            name = "qrmi.service.available",
            autoDelete = Exchange.FALSE,
            declare = Exchange.TRUE,
            durable = Exchange.TRUE,
            type = ExchangeTypes.TOPIC),
        value = @Queue(
            autoDelete = Exchange.TRUE,
            declare = Exchange.TRUE,
            durable = Exchange.TRUE,
            exclusive = Exchange.FALSE),
        key = {"qrmi.api.QRMIRegistryAware"})
)
public class QServiceReceiver implements QRMIRegistryAware {
    private static final Logger logger = LoggerFactory.getLogger(QServiceReceiver.class);
    
    @Autowired
    private QRMIRegistry registry;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void serviceAvailable(RabbitObjectMetadata metadata) {
        logger.info("Service available: {}", metadata.toString());
        registry.bind(metadata);
    }

    @Override
    public void heartbeat(RabbitObjectMetadata metadata, Date currentTime) {
        applicationContext.publishEvent(new ServiceHeartbeat(this, metadata, currentTime));
    }

    @Override
    public void serviceShutdown(RabbitObjectMetadata metadata) {
        logger.info("Shutdown received: {}", metadata.toString());
        registry.unbind(metadata);
    }
}
