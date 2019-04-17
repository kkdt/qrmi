/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.registry;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import qrmi.api.QRMIRegistryListener;
import qrmi.core.RabbitExportConfiguration;
import qrmi.support.RabbitBrokerMetadata;

@SpringBootApplication
@ComponentScan(basePackages = "qrmi.registry")
@Import(value = {RabbitExportConfiguration.class, QServiceDiscoveryConfiguration.class})
@EnableScheduling
public class QServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(QServiceDiscovery.class);

    @Autowired
    private QRMIRegistryListener heartbeat;

    @Autowired
    private ConnectionFactory connectionFactory;


    @Scheduled(fixedRate = 1000, initialDelay = 5000)
    public void doHeartbeat() {
        RabbitBrokerMetadata metadata = new RabbitBrokerMetadata.Builder()
            .with(connectionFactory)
            .build();
        heartbeat.heartbeat(metadata, new Date());
        logger.info("Hearbeat published");
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(QServiceDiscovery.class)
            .bannerMode(Mode.OFF)
            .logStartupInfo(false)
            .headless(true)
            .web(WebApplicationType.NONE)
            .run(args);
    }
}
