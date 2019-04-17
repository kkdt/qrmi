/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.core;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.remoting.client.AmqpProxyFactoryBean;
import org.springframework.util.Assert;

/**
 * Base class for locating an API over RabbitMQ.
 * 
 * @author thinh ho
 *
 */
public abstract class RabbitObjectLocator extends AmqpProxyFactoryBean {
    private final AmqpAdmin amqpAdmin;
    private final ConnectionFactory connectionFactory;
    protected RabbitTemplate referenceTemplate;
    
    public RabbitObjectLocator(AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory) {
        Assert.notNull(amqpAdmin, "Must provide AmqpAdmin");
        Assert.notNull(connectionFactory, "Must provide Connection Factory");
        this.amqpAdmin = amqpAdmin;
        this.connectionFactory = connectionFactory;
        // set the local reference to access it within this class
        setAmqpTemplate(new RabbitTemplate(connectionFactory));
    }
    
    /**
     * Allow implementation to perform any custom instantiation logic right before 
     * executing {@link #afterPropertiesSet()}.
     */
    protected abstract void locateObject();
    
    /**
     * This is needed to set up the reply exchange/queue for the template, if 
     * applicable.
     * 
     * @return
     */
    public AmqpAdmin getAmqpAdmin() {
        return amqpAdmin;
    }
    
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
    
    /**
     * The exchange where the remote API is exported.
     * 
     * @param exchange
     */
    public void setExchange(String exchange) {
        referenceTemplate.setExchange(exchange);
    }
    
    /**
     * The exchange where the remote API is exported.
     * 
     * @return
     */
    public String getExchange() {
        return referenceTemplate.getExchange();
    }
    
    /**
     * The routing key, if applicable.
     * 
     * @return
     */
    public String getRoutingKey() {
        return referenceTemplate.getRoutingKey();
    }

    /**
     * The routing key, if applicable.
     * 
     * @param routingKey
     */
    public void setRoutingKey(String routingKey) {
        referenceTemplate.setRoutingKey(routingKey);
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
    
    @Override
    public void afterPropertiesSet() {
        locateObject();
        super.afterPropertiesSet();
    }
    
}
