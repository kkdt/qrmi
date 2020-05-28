/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.core;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.Objects;

/**
 * Locates an API exported to RabbitMQ.
 * 
 * @author thinh ho
 *
 */
public class RabbitRemoteServiceLocator extends RabbitObjectLocator {
    private String replyExchange;
    private NamingStrategy replyQueueNamingStrategy = new Base64UrlNamingStrategy("qrmi.reply.");
    private SimpleMessageListenerContainer container;
    private Long replyTimeout = 5000L;
    
    public RabbitRemoteServiceLocator(AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory) {
        super(amqpAdmin, connectionFactory);
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
     * The reply timeout in milliseconds.
     * 
     * @param replyTimeout
     */
    public void setReplyTimeout(Long replyTimeout) {
        this.replyTimeout = replyTimeout;
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
    protected void locateObject() {
        TopicExchange _replyExchange = null;
        Queue _replyQueue = null;
        String _replyKey = null;
        
        if(Objects.nonNull(replyExchange) && !replyExchange.isEmpty()) {
            try {
                _replyExchange = new TopicExchange(replyExchange, false, false);
                _replyKey = replyQueueNamingStrategy.generateName();
                _replyQueue = new Queue(_replyKey, false, true, true);
                Binding b = BindingBuilder
                    .bind(_replyQueue)
                    .to(_replyExchange)
                    .with(_replyKey);
                getAmqpAdmin().declareExchange(_replyExchange);
                getAmqpAdmin().declareQueue(_replyQueue);
                getAmqpAdmin().declareBinding(b);
            } catch (Throwable e) {
                throw new IllegalStateException(String.format("Cannot locate %s", getServiceInterface().getName()), e);
            }
        }
        
        referenceTemplate.setReceiveTimeout(replyTimeout);
        if(_replyExchange != null) {
            referenceTemplate.setReplyAddress(String.format("%s/%s", _replyExchange.getName(), _replyKey));
            
            // since the reply exchange and routing key are specified, create up a listener container for the template
            container = new SimpleMessageListenerContainer(getConnectionFactory());
            container.setQueues(_replyQueue);
            container.setAmqpAdmin(getAmqpAdmin());
            container.setMessageListener(referenceTemplate);
            container.start();
        }
    }
}
