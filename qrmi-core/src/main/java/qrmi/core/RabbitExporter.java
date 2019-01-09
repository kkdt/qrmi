/** 
 * Copyright (C) 2019 thinh ho
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
import org.springframework.amqp.core.FanoutExchange;
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
 * Base builder class for exporting an API to RabbitMQ.
 * 
 * @author thinh ho
 *
 */
public abstract class RabbitExporter {
    private final DirectRabbitListenerContainerFactory listenerContainerFactory = new DirectRabbitListenerContainerFactory();
    private DirectMessageListenerContainer container;
    
    public ApplicationContext applicationContext;
    public AmqpAdmin amqpAdmin;
    public ConnectionFactory connectionFactory;
    public Class<?> remoteInterface;
    public Object remoteService;
    public MessageConverter messageConverter;
    public boolean declareQueue = true;
    public Queue queue;
    public boolean declareExchange = true;
    public Exchange exchange;
    public String[] key;
    public RabbitTemplate rabbitTemplate;
    
    public RabbitExporter() {}
    
    /**
     * Determine if the specified <code>exchange</code> is supported.
     * 
     * @param exchange
     * @return true if supported; false otherwise.
     */
    protected abstract boolean supportExchange(Exchange exchange);
    
    /**
     * Create the message listener of the <code>RemoteInvocation</code>.
     * 
     * @return
     */
    protected AmqpInvokerServiceExporter createMessageListener() {
        AmqpInvokerServiceExporter exporter = new AmqpInvokerServiceExporter();
        exporter.setServiceInterface(remoteInterface);
        exporter.setService(remoteService);
        exporter.setAmqpTemplate(Optional.ofNullable(rabbitTemplate)
            .orElse(new RabbitTemplate(connectionFactory)));
        return exporter;
    }
    
    public RabbitExporter with(Consumer<RabbitExporter> builder) {
        builder.accept(this);
        return this;
    }
    
    public void stop() {
        if(container != null) {
            container.stop();
        }
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
    public RabbitExporter withListenerContainer(Consumer<DirectRabbitListenerContainerFactory> builder) {
        builder.accept(listenerContainerFactory);
        return this;
    }
    
    public void export() {
        Assert.notNull(remoteInterface, "Must provide a remote interface");
        Assert.notNull(remoteService, "Must provide a remote service implementation");
        Assert.notNull(exchange, "Must provide an Exchange");
        Assert.notNull(queue, "Must provide a Queue");
        Assert.notNull(connectionFactory, "Must provide Connection Factory");
        
        if(!supportExchange(exchange)) {
            throw new IllegalArgumentException(String.format("Exchange not supported: %s %s", exchange.getType(), exchange.getName()));
        }
        
        if(Objects.nonNull(key) && key.length > 0) {
            Stream.of(key).forEach(k -> declareBinding(exchange, queue, k));
        } else {
            declareBinding(exchange, queue, "#");
        }
        
        // rabbit endpoint definition
        
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setQueues(queue);
        endpoint.setExclusive(queue.isExclusive());
        endpoint.setAdmin(amqpAdmin);
        endpoint.setMessageListener(createMessageListener());
        if(messageConverter != null) {
            endpoint.setMessageConverter(messageConverter);
        }
        
        // declare a factory to create the listener container
        
        listenerContainerFactory.setConnectionFactory(connectionFactory);
        listenerContainerFactory.setApplicationContext(applicationContext);
        
        // create and start listening for messages on the rabbit endpoint
        
        container = listenerContainerFactory.createListenerContainer(endpoint);
        container.start();
    }
    
    protected void declareBinding(Exchange e, Queue q, String key) {
        if(!supportExchange(e)) {
            throw new IllegalArgumentException(String.format("Exchange not supported: %s %s", e.getType(), e.getName()));
        }
        
        if(declareQueue) amqpAdmin.declareQueue(q);
        if(declareExchange) amqpAdmin.declareExchange(e);
        
        Binding b;
        if(e instanceof FanoutExchange) {
            b = BindingBuilder.bind(q)
                .to((DirectExchange)e)
                .with(key);
            amqpAdmin.declareBinding(b);
        } else if (e instanceof TopicExchange) {
            b = BindingBuilder.bind(q)
                .to((TopicExchange)e)
                .with(key);
            amqpAdmin.declareBinding(b);
        } else if (e instanceof DirectExchange) {
            b = BindingBuilder.bind(q)
                .to((DirectExchange)e)
                .with(key);
            amqpAdmin.declareBinding(b);
        } else {
            throw new IllegalStateException("Invalid queue-exchange binding");
        }
    }
}
