/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.service.calculator.rogue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import qrmi.core.RabbitRemoteConfiguration;
import qrmi.tools.api.Calculator;

/**
 * Nothing special here but a configuration to let <code>QRMITool</code> run as a
 * separate application. This application configuration will use the <code>RogueCalculator</code>.
 * 
 * <p>
 * The key line is the import of {@code RabbitRemoteConfiguration} so that it
 * automatically load up <code>RabbitRemote</code> to RabbitMQ.
 * </p>
 * 
 * @author thinh ho
 *
 */
@Configuration
@Import(value = {RabbitRemoteConfiguration.class})
public class RogueCalculatorConfiguration implements ApplicationRunner {
    
    Logger logger = LoggerFactory.getLogger(RogueCalculatorConfiguration.class);
    
    @Autowired(required = true)
    private Environment environment;
    
    @Autowired(required = true)
    private Calculator calculator;
    
    @Autowired(required = true)
    private AmqpAdmin amqpAdmin;
    
    @Autowired(required = true)
    private ConnectionFactory connectionFactory;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Application: " + environment.getProperty("qrmi.px"));
        logger.info("Calculator: " + calculator);
        logger.info("AmqpAdmin: " + amqpAdmin);
        logger.info("ConnectionFactory: " + connectionFactory);
    }
}
