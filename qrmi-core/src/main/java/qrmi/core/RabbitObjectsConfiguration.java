/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import qrmi.core.annotation.RabbitConsumer;
import qrmi.core.annotation.RabbitRemote;

/**
 * A configuration class that will look for all {@code RabbitRemote} bean(s) and 
 * bind them to the underlying RabbitMQ interface.
 * 
 * @author thinh ho
 *
 */
@Configuration
@ConditionalOnClass({AmqpAdmin.class, ConnectionFactory.class})
public class RabbitObjectsConfiguration implements ApplicationListener<ContextStoppedEvent>, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RabbitObjectsConfiguration.class);
    
    @Autowired(required = true)
    private AmqpAdmin amqpAdmin;
    
    @Autowired(required = true)
    private ConnectionFactory connectionFactory;
    
    @Autowired(required = true)
    private ApplicationContext applicationContext;
    
    private Set<RabbitExporter> exported = new HashSet<>();
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> rabbitRemotes = applicationContext.getBeansWithAnnotation(RabbitRemote.class);
        rabbitRemotes.values().forEach(o -> {
            exported.add(initAmqpRemote(o, 
                AnnotationUtils.findAnnotation(AopUtils.getTargetClass(o), 
                RabbitRemote.class),
                applicationContext));
        });
        
        Map<String, Object> rabbitConsumers = applicationContext.getBeansWithAnnotation(RabbitConsumer.class);
        rabbitConsumers.values().forEach(o -> {
            exported.add(initAmqpConsumer(o, 
                AnnotationUtils.findAnnotation(AopUtils.getTargetClass(o), 
                RabbitConsumer.class),
                applicationContext));
        });
        
        logger.info("Exporting Rabbit {} object(s)", exported.size());
        exported.forEach(RabbitExporter::export);
    }
    
    @Override
    public void onApplicationEvent(ContextStoppedEvent event) {
        exported.forEach(r -> {
            try {
                logger.info("Stopping container endpoint {}", r.remoteInterface.getName());
                r.stop();
            } catch(Throwable e) {
                logger.error("Encountered error stopping remote endpoint", e);
            }
        });
    }
    
    private RabbitExporter initAmqpConsumer(Object bean, RabbitConsumer ann, ApplicationContext applicationContext) {
        Assert.notNull(bean, "Bean of type RabbitConsumer cannot be null");
        Assert.notNull(ann, "RabbitConsumer annotation cannot be null");
        
        return new RabbitConsumerExporter()
            .with(r -> r.applicationContext = applicationContext)
            .with(r -> r.amqpAdmin = amqpAdmin)
            .with(r -> r.connectionFactory = connectionFactory)
            .with(r -> r.remoteInterface = ann.remoteInterface())
            .with(r -> r.remoteService = bean)
            .with(r -> r.key = ann.binding().key())
            .with(r -> {
                QueueBinding binding = ann.binding();
                Queue q = binding.value();
                org.springframework.amqp.core.Queue queue = 
                    new org.springframework.amqp.core.Queue(q.name(), 
                        Boolean.valueOf(q.durable()).booleanValue(),
                        Boolean.valueOf(q.exclusive()).booleanValue(), 
                        Boolean.valueOf(q.autoDelete()).booleanValue());
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
                        Boolean.valueOf(e.durable()).booleanValue(), 
                        Boolean.valueOf(e.autoDelete()).booleanValue());
                    break;
                default:
                    exchange = new DirectExchange(e.name(), 
                        Boolean.valueOf(e.durable()).booleanValue(), 
                        Boolean.valueOf(e.autoDelete()).booleanValue());
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
                // TODO: decide where these configurations should take place
                c.setConsumerTagStrategy(s -> String.format("qrmi.%s.%s", bean.getClass().getSimpleName(), UUID.randomUUID().toString()));
            });
    }
    
    private RabbitExporter initAmqpRemote(Object bean, RabbitRemote ann, ApplicationContext applicationContext) {
        Assert.notNull(bean, "Bean of type RabbitRemote cannot be null");
        Assert.notNull(ann, "RabbitRemote annotation cannot be null");
        
        // bind the remote API to rabbit
        return new RabbitRemoteExporter()
            .with(r -> r.applicationContext = applicationContext)
            .with(r -> r.amqpAdmin = amqpAdmin)
            .with(r -> r.connectionFactory = connectionFactory)
            .with(r -> r.remoteInterface = ann.remoteInterface())
            .with(r -> r.remoteService = bean)
            .with(r -> r.key = ann.binding().key())
            .with(r -> {
                QueueBinding binding = ann.binding();
                Queue q = binding.value();
                org.springframework.amqp.core.Queue queue = 
                    new org.springframework.amqp.core.Queue(q.name(), 
                        Boolean.valueOf(q.durable()).booleanValue(),
                        Boolean.valueOf(q.exclusive()).booleanValue(), 
                        Boolean.valueOf(q.autoDelete()).booleanValue());
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
                        Boolean.valueOf(e.durable()).booleanValue(), 
                        Boolean.valueOf(e.autoDelete()).booleanValue());
                    break;
                default:
                    exchange = new DirectExchange(e.name(), 
                        Boolean.valueOf(e.durable()).booleanValue(), 
                        Boolean.valueOf(e.autoDelete()).booleanValue());
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
                // TODO: decide where these configurations should take place
                c.setConsumerTagStrategy(s -> String.format("qrmi.%s.%s", bean.getClass().getSimpleName(), UUID.randomUUID().toString()));
            });
    }

}
