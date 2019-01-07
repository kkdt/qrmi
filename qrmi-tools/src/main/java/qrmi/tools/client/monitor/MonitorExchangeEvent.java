/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.client.monitor;

import javax.swing.JFrame;

import org.springframework.context.ApplicationEvent;

public class MonitorExchangeEvent extends ApplicationEvent {
    private static final long serialVersionUID = -8419097673763336191L;

    private final JFrame frame;
    private final String exchange;
    
    public MonitorExchangeEvent(JFrame source, String exchange) {
        super(source);
        this.frame = source;
        this.exchange = exchange;
    }
    
    public String getExchange() {
        return exchange;
    }
    
    public JFrame getWindow() {
        return frame;
    }
}
