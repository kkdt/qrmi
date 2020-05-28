/*
 * Copyright (c) 2019. thinh ho
 * This file is part of 'qrmi-core_main' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */

package qrmi.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

/**
 * Consumer API message listener will not be providing a response. This is a broadcast-style remote invocation.
 *
 * @author thinh
 */
public class RabbitRemoteBroadcastMessageListener extends AmqpInvokerServiceExporter {
    private static final Logger logger = LoggerFactory.getLogger(RabbitRemoteBroadcastMessageListener.class);

    @Override
    public void onMessage(Message message) {
        Object invocationRaw = this.getMessageConverter().fromMessage(message);
        RemoteInvocationResult remoteInvocationResult = null;
        RemoteInvocation invocation = null;
        if (!(invocationRaw instanceof RemoteInvocation)) {
            remoteInvocationResult = new RemoteInvocationResult(new IllegalArgumentException("The message does not contain a RemoteInvocation payload"));
        } else {
            invocation = (RemoteInvocation)invocationRaw;
            remoteInvocationResult = this.invokeAndCreateResult(invocation, this.getService());
        }

        if(logger.isDebugEnabled()) {
            logger.debug("Consumer invoked {}, results: {}", invocation, remoteInvocationResult);
        }
    }
}
