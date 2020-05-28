/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.stereotype.Component;

/**
 * The <code>RabbitRemoteBroadcast</code> identifies pub/sub-based API implementations
 * that may be invoked on RabbitMQ.
 * 
 * @author thinh ho
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RabbitRemoteBroadcast {
    /**
     * Service name to be used for look up.
     * 
     * @return
     */
    String name();
    
    /**
     * (optional) API description.
     * 
     * @return
     */
    String description() default "";
    /**
     * Identify the interface/api if the implementation satisfies multiple.
     * 
     * @return
     */
    Class<?> remoteInterface();
    
    /**
     * The queue-exchange binding. 
     * 
     * <p>
     * Only DIRECT and TOPIC exchanges are supported.
     * </p>
     * 
     * @return
     */
    QueueBinding binding();
    
    /**
     * The number of messages to send to each consumer in a single request - 
     * controls throughput.
     * 
     * @return (default 1)
     */
    int prefetchCount() default 1;
}
