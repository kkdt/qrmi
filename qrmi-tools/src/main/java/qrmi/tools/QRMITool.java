/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import com.rabbitmq.client.ShutdownSignalException;

/**
 * Spring Boot application.
 * 
 * <p>
 * Requires the following <code>-D</code> variables:
 * <ol>
 * <li>qrmi.px.package = Package of application for <code>PackageScan</code></li>
 * <li>qrmi.px = Application Name</li>
 * </ol>
 * 
 * @author thinh ho
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "${qrmi.px.package}")
public class QRMITool implements InitializingBean, ConnectionListener {
    private static final Logger logger = LoggerFactory.getLogger(QRMITool.class);
    
    @Autowired
    private ConnectionFactory connectionFactory;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        connectionFactory.addConnectionListener(this);
    }
    
    @Override
    public void onCreate(Connection connection) {
        logger.info("Connection created on local port {}: {}", connection.getLocalPort(), connection.getDelegate().getServerProperties());
    }
    
    @Override
    public void onClose(Connection connection) {
        logger.info("Connection closed on local port {}: {}", connection.getLocalPort(), connection.getDelegate().getServerProperties());
    }

    @Override
    public void onShutDown(ShutdownSignalException signal) {
        logger.info("Connection shutdown on local port", signal);
    }
    
    public static void main(String[] args) {
        DefaultApplicationArguments _args = new DefaultApplicationArguments(args);
        new SpringApplicationBuilder(QRMITool.class)
            .bannerMode(Mode.OFF).logStartupInfo(false)
            .headless(_args.containsOption("--ui"))
            .web(WebApplicationType.NONE).run(args);
    }
}
