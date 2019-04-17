/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.core;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import qrmi.core.annotation.RabbitConsumer;
import qrmi.core.annotation.RabbitRemote;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Builds {@code RabbitExporter} from an annotation and the annotated bean.
 * 
 * @author thinh ho
 *
 */
public class RabbitExporterBuilder {
    protected final AmqpAdmin amqpAdmin;
    protected final ConnectionFactory connectionFactory;
    
    public ApplicationContext applicationContext;
    public ConsumerTagStrategy consumerTagStrategy;
    
    public RabbitExporterBuilder(AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory) {
        Assert.notNull(amqpAdmin, "Must provide AmqpAdmin");
        Assert.notNull(connectionFactory, "Must provide Connection Factory");
        this.amqpAdmin = amqpAdmin;
        this.connectionFactory = connectionFactory;
    }
    
    public RabbitExporterBuilder with(Consumer<RabbitExporterBuilder> b) {
        b.accept(this);
        return this;
    }
    
    /**
     * Detect whether or not the provided object can be exported and return the 
     * appropriate {@code RabbitExporter}.
     * 
     * @param obj
     * @return the {@code RabbitExporter} or null.
     */
    public RabbitExporter build(Object obj) {
        Assert.notNull(obj, "Bean of type RabbitRemote cannot be null");
        
        RabbitRemote remote = AnnotationUtils.findAnnotation(obj.getClass(), RabbitRemote.class);
        if(remote != null) {
            return this.build(remote, obj);
        }
        
        RabbitConsumer consumer = AnnotationUtils.findAnnotation(obj.getClass(), RabbitConsumer.class);
        if(consumer != null) {
            return this.build(consumer, obj);
        }
        
        return null;
    }
    
    public RabbitExporter build(RabbitRemote ann, Object obj) {
        Assert.notNull(obj, "Bean of type RabbitRemote cannot be null");
        Assert.notNull(ann, "RabbitRemote annotation cannot be null");
        
        // bind the remote API to rabbit
        return new RabbitRemoteExporter()
            .with(r -> r.name = ann.name())
            .with(r -> r.description = ann.description())
            .with(r -> r.applicationContext = applicationContext)
            .with(r -> r.amqpAdmin = amqpAdmin)
            .with(r -> r.connectionFactory = connectionFactory)
            .with(r -> r.remoteInterface = ann.remoteInterface())
            .with(r -> r.remoteService = obj)
            .with(r -> r.key = ann.binding().key())
            .with(r -> {
                QueueBinding binding = ann.binding();
                Queue q = binding.value();
                org.springframework.amqp.core.Queue queue = 
                    new org.springframework.amqp.core.Queue(q.name(), 
                        Boolean.valueOf(q.durable()),
                        Boolean.valueOf(q.exclusive()),
                        Boolean.valueOf(q.autoDelete()));
                r.queue = queue;
                
                switch(q.declare()) {
                case Exchange.TRUE:
                    r.declareQueue = true;
                    break;
                default:
                    r.declareQueue = false;
                    break;
                }
                
                Exchange e = binding.exchange();
                org.springframework.amqp.core.Exchange exchange;
                switch(e.type()) {
                case ExchangeTypes.TOPIC:
                    exchange = new TopicExchange(e.name(), 
                        Boolean.valueOf(e.durable()),
                        Boolean.valueOf(e.autoDelete()));
                    break;
                default:
                    exchange = new DirectExchange(e.name(), 
                        Boolean.valueOf(e.durable()),
                        Boolean.valueOf(e.autoDelete()));
                    break;
                }
                r.exchange = exchange;
                
                switch(e.declare()) {
                case Exchange.TRUE:
                    r.declareExchange = true;
                    break;
                default:
                    r.declareExchange = false;
                    break;
                }
            }).withListenerContainer(c -> {
                c.setPrefetchCount(ann.prefetchCount());
                if(consumerTagStrategy != null) {
                    c.setConsumerTagStrategy(consumerTagStrategy);
                } else {
                    c.setConsumerTagStrategy(s -> String.format("qrmi.%s.%s", obj.getClass().getSimpleName(), UUID.randomUUID().toString()));
                }
            });
    }
    
    public RabbitExporter build(RabbitConsumer ann, Object obj) {
        Assert.notNull(obj, "Bean of type RabbitConsumer cannot be null");
        Assert.notNull(ann, "RabbitConsumer annotation cannot be null");
        
        return new RabbitConsumerExporter()
            .with(r -> r.name = ann.name())
            .with(r -> r.description = ann.description())
            .with(r -> r.applicationContext = applicationContext)
            .with(r -> r.amqpAdmin = amqpAdmin)
            .with(r -> r.connectionFactory = connectionFactory)
            .with(r -> r.remoteInterface = ann.remoteInterface())
            .with(r -> r.remoteService = obj)
            .with(r -> r.key = ann.binding().key())
            .with(r -> {
                QueueBinding binding = ann.binding();
                Queue q = binding.value();
                org.springframework.amqp.core.Queue queue = 
                    new org.springframework.amqp.core.Queue(q.name(), 
                        Boolean.valueOf(q.durable()),
                        Boolean.valueOf(q.exclusive()),
                        Boolean.valueOf(q.autoDelete()));
                r.queue = queue;
                
                switch(q.declare()) {
                case Exchange.TRUE:
                    r.declareQueue = true;
                    break;
                default:
                    r.declareQueue = false;
                    break;
                }
                
                Exchange e = binding.exchange();
                org.springframework.amqp.core.Exchange exchange;
                switch(e.type()) {
                case ExchangeTypes.TOPIC:
                    exchange = new TopicExchange(e.name(), 
                        Boolean.valueOf(e.durable()),
                        Boolean.valueOf(e.autoDelete()));
                    break;
                default:
                    exchange = new FanoutExchange(e.name(),
                        Boolean.valueOf(e.durable()),
                        Boolean.valueOf(e.autoDelete()));
                    break;
                }
                r.exchange = exchange;
                
                switch(e.declare()) {
                case Exchange.TRUE:
                    r.declareExchange = true;
                    break;
                default:
                    r.declareExchange = false;
                    break;
                }
            }).withListenerContainer(c -> {
                c.setPrefetchCount(ann.prefetchCount());
                if(consumerTagStrategy != null) {
                    c.setConsumerTagStrategy(consumerTagStrategy);
                } else {
                    c.setConsumerTagStrategy(s -> String.format("qrmi.%s.%s", obj.getClass().getSimpleName(), UUID.randomUUID().toString()));
                }
            });
    }
    
}
