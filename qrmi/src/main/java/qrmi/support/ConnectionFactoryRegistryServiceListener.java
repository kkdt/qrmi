/*
 * Copyright (c) 2019. thinh ho
 * This file is part of 'qrmi-qrmi_main' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */

package qrmi.support;

import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.impl.AMQConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import qrmi.api.QRMIRegistryListener;
import qrmi.core.annotation.RabbitConsumer;


@RabbitConsumer(
    name = "QRMIRegistryListener",
    remoteInterface = QRMIRegistryListener.class,
    binding = @QueueBinding(
        exchange = @Exchange(
            name = "qrmi.service.registry.heartbeat",
            autoDelete = Exchange.FALSE,
            declare = Exchange.TRUE,
            durable = Exchange.TRUE,
            type = ExchangeTypes.FANOUT),
        value = @Queue(
            autoDelete = Exchange.FALSE,
            declare = Exchange.TRUE,
            durable = Exchange.TRUE,
            exclusive = Exchange.TRUE))
)
public class ConnectionFactoryRegistryServiceListener implements QRMIRegistryListener, ConnectionListener {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionFactoryRegistryServiceListener.class);

    private final CachingConnectionFactory connectionFactory;
    private final Map<String, Set<String>> cache = new HashMap<>();

    public ConnectionFactoryRegistryServiceListener(CachingConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.connectionFactory.addConnectionListener(this);
    }

    @Override
    public void heartbeat(RabbitBrokerMetadata metadata, Date date) {
        String host = metadata.getHost();
        String address = String.format("%s:%s", host, metadata.getPort());
        synchronized (cache) {
            Set<String> _cache = cache.getOrDefault(host, new HashSet<>());
            if(!_cache.contains(address)) {
                _cache.add(address);
                String a = String.join(",", _cache);
                connectionFactory.setAddresses(a);
                logger.info("Cached address(es) {} ", cache);
            }
        }
    }

    @Override
    public void onCreate(Connection connection) {
        logger.info("Connection created " + connection);
    }

    @Override
    public void onClose(Connection connection) {
        logger.info("Connection closed " + connection);
        try {
            String address = String.format("%s:%s", connection.getDelegate().getAddress().getHostName(), connection.getLocalPort());
            logger.info("Connection closed {}", address);
        } catch (Exception e) {
            logger.error("Address parsed error", e);
        }
    }

    @Override
    public void onShutDown(ShutdownSignalException signal) {
        if(signal.getReference() != null && signal.getReference() instanceof AMQConnection) {
            AMQConnection connection = (AMQConnection)signal.getReference();
            String address = String.format("%s:%s", connection.getAddress().getHostName(), connection.getLocalPort());
            logger.warn("Shutdown signaled on {}", address);
        }
    }
}
