/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.support;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.util.Assert;
import qrmi.api.QRMIRegistryAware;
import qrmi.core.RabbitExporter;
import qrmi.core.RabbitExporterBuilder;
import qrmi.core.annotation.RabbitConsumer;
import qrmi.core.annotation.RabbitRemote;

/**
 * The same builder as {@code RabbitExporterBuilder} but is hooked up to the 
 * remote {@code QRMIRegistryAware} to notify the Service Registry after the API is attached
 * to RabbitMQ.
 * 
 * @author thinh ho
 *
 */
public class RabbitServiceBuilder extends RabbitExporterBuilder {
    
    private final QRMIRegistryAware registryBroadcast;

    public RabbitServiceBuilder(AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory, QRMIRegistryAware registryBroadcast) {
        super(amqpAdmin, connectionFactory);
        Assert.notNull(registryBroadcast, "Requires QRMIRegistryAware");
        this.registryBroadcast = registryBroadcast;
    }
    
    @Override
    public RabbitExporter build(RabbitRemote ann, Object obj) {
        RabbitExporter exporter = super.build(ann, obj);
        bind(exporter);
        return exporter;
    }
    
    @Override
    public RabbitExporter build(RabbitConsumer ann, Object obj) {
        RabbitExporter exporter = super.build(ann, obj);
        bind(exporter);
        return exporter;
    }
    
    private void bind(RabbitExporter e) {
        RabbitObjectMetadata metadata = createMetadata(e);
        // attach to RabbitMQ
        e.export();
        // broadcast available
        registryBroadcast.serviceAvailable(metadata);
    }

    /**
     * Build the metadata for the specified service exporter.
     *
     * @param e
     * @return
     */
    public static RabbitObjectMetadata createMetadata(RabbitExporter e) {
        RabbitBrokerMetadata broker = new RabbitBrokerMetadata();
        broker.setHost(e.connectionFactory.getHost());
        broker.setVhost(e.connectionFactory.getVirtualHost());
        broker.setPort(e.connectionFactory.getPort());

        RabbitObjectMetadata m = new RabbitObjectMetadata();
        m.setDescription(e.description);
        m.setName(e.name);
        m.setRemoteInterface(e.remoteInterface.getName());
        m.setExchange(e.exchange.getName());
        m.setQueue(e.queue.getActualName());
        m.setBrokerLocation(broker);
        return m;
    }

}
