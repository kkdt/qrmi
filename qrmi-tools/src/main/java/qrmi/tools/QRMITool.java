/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

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
public class QRMITool {
    
    public static void main(String[] args) {
        DefaultApplicationArguments _args = new DefaultApplicationArguments(args);
        new SpringApplicationBuilder(QRMITool.class)
            .bannerMode(Mode.OFF).logStartupInfo(false)
            .headless(_args.containsOption("--ui"))
            .web(WebApplicationType.NONE).run(args);
    }
}
