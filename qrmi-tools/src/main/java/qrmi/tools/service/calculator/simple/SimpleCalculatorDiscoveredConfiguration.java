/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.service.calculator.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import qrmi.api.QRMIRegistry;
import qrmi.api.QRMIRegistryAware;
import qrmi.core.RabbitExporter;
import qrmi.support.RabbitServiceBuilder;
import qrmi.tools.api.Calculator;

@Component
public class SimpleCalculatorDiscoveredConfiguration implements InitializingBean {
    
    private Logger logger = LoggerFactory.getLogger(SimpleCalculatorDiscoveredConfiguration.class);
    
    @Autowired
    private QRMIRegistry registry;
    
    @Autowired
    private QRMIRegistryAware registryBroadcast;
    
    @Autowired
    private Calculator calculator;
    
    @Autowired
    private AmqpAdmin amqpAdmin;
    
    @Autowired
    private ConnectionFactory connectionFactory;

    @Value("${qrmi.register:false}")
    private Boolean doRegister;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(doRegister) {
            logger.info("Registering calculator {} ", calculator.getClass().getName());

            RabbitExporter exporter = new RabbitServiceBuilder(amqpAdmin, connectionFactory, registryBroadcast)
                .build(calculator);

            StringBuilder b = new StringBuilder("All available service(s)");
            registry.list().forEach(m -> {
                b.append("\n    ").append(m);
            });
            logger.info(b.toString());

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    registryBroadcast.serviceShutdown(RabbitServiceBuilder.createMetadata(exporter));
                } catch (Exception e) {
                    logger.warn("Cannot notify service shutdown", e);
                }
            }));
        }
    }
}
