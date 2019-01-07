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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import qrmi.core.annotation.RabbitRemote;

/**
 * A configuration class that will look for all {@code AmqpRemote} bean(s) and 
 * bind them to the underlying RabbitMQ interface.
 * 
 * <p>
 * This can also be declared as a bean or through an annotated application context.
 * </p>
 * 
 * @author thinh ho
 *
 */
@Configuration
public class RabbitRemoteConfiguration 
    implements ApplicationContextAware, ApplicationListener<ContextStoppedEvent>
{
    private static final Logger logger = LoggerFactory.getLogger(RabbitRemoteConfiguration.class);
    
    @Autowired(required = true)
    private AmqpAdmin amqpAdmin;
    
    @Autowired(required = true)
    private ConnectionFactory connectionFactory;
    
    private Set<RabbitRemoteExporter> exported = new HashSet<>();
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> amqpRemotes = applicationContext.getBeansWithAnnotation(RabbitRemote.class);
        amqpRemotes.values().forEach(o -> {
            exported.add(initAmqpRemote(o, 
                AnnotationUtils.findAnnotation(AopUtils.getTargetClass(o), 
                RabbitRemote.class),
                applicationContext));
        });
        exported.forEach(RabbitRemoteExporter::export);
    }
    
    @Override
    public void onApplicationEvent(ContextStoppedEvent event) {
        exported.forEach(r -> {
            try {
                logger.info("Stopping container endpoing {}", r.remoteInterface.getName());
                r.stop();
            } catch(Throwable e) {
                logger.error("Encountered error stopping remote endpoint", e);
            }
        });
    }
    
    private RabbitRemoteExporter initAmqpRemote(Object bean, RabbitRemote ann, ApplicationContext applicationContext) {
        Assert.notNull(bean, "Bean of type AmqpRemote cannot be null");
        Assert.notNull(ann, "AmqpRemote annotation cannot be null");
        
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
                c.setPrefetchCount(1);
                c.setConsumerTagStrategy(s -> String.format("qrmi.%s.%s", bean.getClass().getSimpleName(), UUID.randomUUID().toString()));
            });
    }
}
