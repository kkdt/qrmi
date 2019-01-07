/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.core;

import java.util.Objects;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Base64UrlNamingStrategy;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.NamingStrategy;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.remoting.client.AmqpProxyFactoryBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.Assert;

public class RabbitRemoteLocator extends AmqpProxyFactoryBean
    implements ApplicationListener<ContextClosedEvent>
{
    private AmqpAdmin amqpAdmin;
    private ConnectionFactory connectionFactory;
    private String exchange;
    private String replyExchange;
    private NamingStrategy replyQueueNamingStrategy = new Base64UrlNamingStrategy("qrmi.reply.");
    private SimpleMessageListenerContainer container;
    private Long replyTimeout = 5000L;
    private RabbitTemplate referenceTemplate;
    
    public RabbitRemoteLocator(ConnectionFactory connectionFactory) {
        Assert.notNull(connectionFactory, "Must provide Connection Factory");
        this.connectionFactory = connectionFactory;
        // set the local reference to access it within this class
        referenceTemplate = new RabbitTemplate(connectionFactory);
        setAmqpTemplate(referenceTemplate);
    }
    
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        stop();
    }
    
    @Override
    public void setAmqpTemplate(AmqpTemplate template) {
        if(template instanceof RabbitTemplate) {
            super.setAmqpTemplate(template);
            this.referenceTemplate = (RabbitTemplate)template;
        } else {
            throw new IllegalArgumentException("Only a RabbitTemplate is accepted");
        }
    }
    
    /**
     * Stop the reply listener container if applicable.
     */
    public void stop() {
        if(container != null) {
            container.stop();
        }
    }

    /**
     * The exchange where the remote API is exported.
     * 
     * @param exchange
     */
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    /**
     * The reply timeout in milliseconds.
     * 
     * @param replyTimeout
     */
    public void setReplyTimeout(Long replyTimeout) {
        this.replyTimeout = replyTimeout;
    }

    /**
     * This is needed to set up the reply exchange/queue for the template, if 
     * applicable.
     * 
     * @param amqpAdmin
     */
    public void setAmqpAdmin(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }
    
    /**
     * If a reply exchange is specified, then a {@code TopicExchange} will be created
     * for the Rabbit Template to listen for reply.
     * 
     * @param replyExchange
     */
    public void setReplyExchange(String replyExchange) {
        this.replyExchange = replyExchange;
    }

    /**
     * The reply queue where the Rabbit Template will be the exclusive owner 
     * listening for responses. The reply exchange/queue will be binded using the
     * queue name as the routing key.
     *  
     * @param replyQueueNamingStrategy
     */
    public void setReplyQueueNamingStrategy(NamingStrategy replyQueueNamingStrategy) {
        this.replyQueueNamingStrategy = replyQueueNamingStrategy;
    }

    @Override
    public void afterPropertiesSet() {
        TopicExchange _replyExchange = null;
        Queue _replyQueue = null;
        String _replyKey = null;
        
        if(Objects.nonNull(replyExchange) && !replyExchange.isEmpty() && Objects.nonNull(amqpAdmin)) {
            try {
                _replyExchange = new TopicExchange(replyExchange, false, false);
                _replyKey = replyQueueNamingStrategy.generateName();
                _replyQueue = new Queue(_replyKey, false, true, true);
                Binding b = BindingBuilder
                    .bind(_replyQueue)
                    .to(_replyExchange)
                    .with(_replyKey);
                amqpAdmin.declareExchange(_replyExchange);
                amqpAdmin.declareQueue(_replyQueue);
                amqpAdmin.declareBinding(b);
            } catch (Throwable e) {
                throw new IllegalStateException(String.format("Cannot locate %s", getServiceInterface().getName()), e);
            }
        }
        
        referenceTemplate.setExchange(exchange);
        referenceTemplate.setReceiveTimeout(replyTimeout);
        if(_replyExchange != null && _replyKey != null && _replyQueue != null) {
            referenceTemplate.setReplyAddress(String.format("%s/%s", _replyExchange.getName(), _replyKey));
            
            // since the reply exchange and routing key are specified, create up a listener container for the template
            container = new SimpleMessageListenerContainer(connectionFactory);
            container.setQueues(_replyQueue);
            container.setAmqpAdmin(amqpAdmin);
            container.setMessageListener(referenceTemplate);
            container.start();
        }
        
        super.afterPropertiesSet();
    }
}
