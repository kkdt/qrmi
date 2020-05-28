/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import qrmi.core.annotation.RabbitRemoteBroadcast;
import qrmi.core.annotation.RabbitRemoteService;

/**
 * A configuration class that will look for all {@code RabbitRemoteService} and {@code RabbitRemoteBroadcast}
 * bean(s) and bind them to the underlying RabbitMQ interface.
 * 
 * <p>
 * Exposes a 'remoteExport' bean of type {@code RabbitExporterBuilder} to the
 * application for access if needed.
 * </p>
 * 
 * @author thinh ho
 *
 */
@Configuration
public class RabbitExportConfiguration implements ApplicationListener<ContextStoppedEvent>, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RabbitExportConfiguration.class);
    
    @Autowired
    private AmqpAdmin amqpAdmin;
    
    @Autowired
    private ConnectionFactory connectionFactory;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    private Set<RabbitExporter> exported = new HashSet<>();
    
    @Bean
    public RabbitExporterBuilder remoteExport() {
        return new RabbitExporterBuilder(amqpAdmin, connectionFactory)
            .with(c -> c.applicationContext = applicationContext);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> rabbitRemotes = applicationContext.getBeansWithAnnotation(RabbitRemoteService.class);
        rabbitRemotes.values().forEach(o -> 
            exported.add(remoteExport()
                .build(AnnotationUtils.findAnnotation(AopUtils.getTargetClass(o), RabbitRemoteService.class), o))
        );
        
        Map<String, Object> rabbitConsumers = applicationContext.getBeansWithAnnotation(RabbitRemoteBroadcast.class);
        rabbitConsumers.values().forEach(o -> 
            exported.add(remoteExport()
                .build(AnnotationUtils.findAnnotation(AopUtils.getTargetClass(o), RabbitRemoteBroadcast.class), o))
        );
        
        logger.info("Exporting {} Rabbit object(s)", exported.size());
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

}
