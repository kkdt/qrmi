/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.core;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * Builder for exporting an API implementation to RabbitMQ.
 * 
 * @author thinh ho
 *
 */
public class RabbitRemoteExporter {
    private final DirectRabbitListenerContainerFactory listenerContainerFactory = new DirectRabbitListenerContainerFactory();
    private DirectMessageListenerContainer container;
    
    public ApplicationContext applicationContext;
    public AmqpAdmin amqpAdmin;
    public ConnectionFactory connectionFactory;
    public RabbitTemplate rabbitTemplate;
    public Class<?> remoteInterface;
    public Object remoteService;
    public MessageConverter messageConverter;
    public boolean declareQueue = true;
    public Queue queue;
    public boolean declareExchange = true;
    public Exchange exchange;
    public String[] key;
    
    public RabbitRemoteExporter() {}
    
    public RabbitRemoteExporter with(Consumer<RabbitRemoteExporter> builder) {
        builder.accept(this);
        return this;
    }
    
    /**
     * Exposes the listener container factory for external configuration.
     * 
     * <p>
     * The {@link #connectionFactory} and {@link #applicationContext} will also be
     * set when the export occurs.
     * </p>
     * 
     * @param builder
     * @return
     */
    public RabbitRemoteExporter withListenerContainer(Consumer<DirectRabbitListenerContainerFactory> builder) {
        builder.accept(listenerContainerFactory);
        return this;
    }
    
    /**
     * Bind the API implementation to RabbitMQ.
     */
    public void export() {
        Assert.notNull(remoteInterface, "Must provide a remote interface");
        Assert.notNull(remoteService, "Must provide a remote service implementation");
        Assert.notNull(exchange, "Must provide an Exchange");
        Assert.notNull(queue, "Must provide a Queue");
        Assert.notNull(connectionFactory, "Must provide Connection Factory");
        
        if(exchange.getType() != null 
            && !(ExchangeTypes.DIRECT.equals(exchange.getType()) || ExchangeTypes.TOPIC.equals(exchange.getType()))) 
        {
            throw new IllegalArgumentException(String.format("%s not support - only DIRECT and TOPIC exchanges are supported", exchange.getType()));
        }
        
        if(Objects.nonNull(key) && key.length > 0) {
            Stream.of(key).forEach(k -> declareBinding(exchange, queue, k));
        } else {
            declareBinding(exchange, queue, "#");
        }
        
        // message listener
        
        AmqpInvokerServiceExporter exporter = new AmqpInvokerServiceExporter();
        exporter.setServiceInterface(remoteInterface);
        exporter.setService(remoteService);
        exporter.setAmqpTemplate(Optional.ofNullable(rabbitTemplate)
            .orElse(new RabbitTemplate(connectionFactory)));
        
        // rabbit endpoint definition
        
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setQueues(queue);
        endpoint.setExclusive(queue.isExclusive());
        endpoint.setAdmin(amqpAdmin);
        endpoint.setMessageListener(exporter);
        
        // declare a factory to create the listener container
        
        listenerContainerFactory.setConnectionFactory(connectionFactory);
        listenerContainerFactory.setApplicationContext(applicationContext);
        
        // create and start listening for messages on the rabbit endpoint
        
        container = listenerContainerFactory.createListenerContainer(endpoint);
        container.start();
    }
    
    public void stop() {
        if(container != null) {
            container.stop();
        }
    }
    
    private void declareBinding(Exchange e, Queue q, String key) {
        if(declareQueue) amqpAdmin.declareQueue(q);
        if(declareExchange) amqpAdmin.declareExchange(e);
        
        Binding b;
        if(e instanceof DirectExchange) {
            b = BindingBuilder.bind(q)
                .to((DirectExchange)e)
                .with(key);
            amqpAdmin.declareBinding(b);
        } else if (e instanceof TopicExchange) {
            b = BindingBuilder.bind(q)
                .to((TopicExchange)e)
                .with(key);
            amqpAdmin.declareBinding(b);
        } else {
            throw new IllegalStateException("Invalid queue-exchange binding");
        }
    }
}
