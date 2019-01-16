/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.registry;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import qrmi.core.RabbitExportConfiguration;

@SpringBootApplication
@ComponentScan(basePackages = "qrmi.registry")
@Import(value = {RabbitExportConfiguration.class})
public class QServiceDiscovery {
    public static void main(String[] args) {
        new SpringApplicationBuilder(QServiceDiscovery.class)
            .bannerMode(Mode.OFF)
            .logStartupInfo(false)
            .headless(true)
            .web(WebApplicationType.NONE)
            .run(args);
    }
}
