/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.core;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.remoting.RemoteProxyFailureException;
import org.springframework.remoting.support.RemoteInvocation;

/**
 * Locates a broadcast API exported to RabbitMQ.
 * 
 * @author thinh ho
 *
 */
public class RabbitRemoteBroadcastLocator extends RabbitObjectLocator {

    public RabbitRemoteBroadcastLocator(AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory) {
        super(amqpAdmin, connectionFactory);
    }

    @Override
    protected void locateObject() {
        // nothing special in this case
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        RemoteInvocation remoteInvocation = getRemoteInvocationFactory().createRemoteInvocation(invocation);
        try {
            if (getRoutingKey() == null) {
                this.getAmqpTemplate().convertAndSend(remoteInvocation);
            } else {
                this.getAmqpTemplate().convertAndSend(getRoutingKey(), remoteInvocation);
            }
        } catch (Exception e) {
            throw new RemoteProxyFailureException("Error broadcasting message", e);
        }

        return true;
    }
}
