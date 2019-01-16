/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.context.ApplicationListener;
import qrmi.api.QRMIRegistry;
import qrmi.core.annotation.RabbitRemote;
import qrmi.support.QRMIRemoteException;
import qrmi.support.RabbitObjectMetadata;

/**
 * In-memory cache of services.
 *
 * @author thinh ho
 */
@RabbitRemote(
    name = "QRegistry",
    description = "RabbitMQ Service Discovery",
    remoteInterface = QRMIRegistry.class,
    binding = @QueueBinding(
        exchange = @Exchange(
            name = "qrmi.service.registry",
            autoDelete = Exchange.FALSE,
            declare = Exchange.TRUE,
            durable = Exchange.TRUE,
            type = ExchangeTypes.DIRECT),
        value = @Queue(
            name = "qrmi.service.registry_queue",
            autoDelete = Exchange.FALSE,
            declare = Exchange.TRUE,
            durable = Exchange.TRUE,
            exclusive = Exchange.FALSE),
        key = {"qrmi.api.QRMIRegistry"})
)
public class QRegistry implements QRMIRegistry, ApplicationListener<ServiceHeartbeat> {
    private final Logger logger = LoggerFactory.getLogger(QRegistry.class);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private Map<String, Set<RabbitObjectMetadata>> cache = new HashMap<>();

    @Override
    public List<RabbitObjectMetadata> lookup(String name) throws QRMIRemoteException {
        List<RabbitObjectMetadata> ret = new ArrayList<>();
        ReadLock l = lock.readLock();
        l.lock();
        try {
            ret.addAll(cache.getOrDefault(name, new HashSet<>()));
        } finally {
            l.unlock();
        }
        return ret;
    }

    @Override
    public List<RabbitObjectMetadata> list() throws QRMIRemoteException {
        List<RabbitObjectMetadata> ret = new ArrayList<>();
        ReadLock l = lock.readLock();
        l.lock();
        try {
            ret.addAll(cache.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
        } finally {
            l.unlock();
        }
        return ret;
    }

    @Override
    public void bind(RabbitObjectMetadata metadata) throws QRMIRemoteException {
        logger.info("Binding {}", metadata);
        WriteLock l = lock.writeLock();
        l.lock();
        try {
            String name = metadata.getName();
            cache.putIfAbsent(name, new HashSet<>());
            cache.get(name).add(metadata);
        } finally {
            l.unlock();
        }
    }

    @Override
    public void unbind(RabbitObjectMetadata metadata) throws QRMIRemoteException {
        WriteLock l = lock.writeLock();
        l.lock();
        try {
            String name = metadata.getName();
            cache.getOrDefault(name, new HashSet<>()).remove(metadata);
        } finally {
            l.unlock();
        }
    }

    @Override
    public void onApplicationEvent(ServiceHeartbeat event) {
        logger.info("Received heartbeat {} at {}", event.getMetadata(), event.getTime());
    }
}
