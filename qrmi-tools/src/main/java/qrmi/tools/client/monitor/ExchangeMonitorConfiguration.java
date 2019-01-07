/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.client.monitor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeMonitorConfiguration implements ApplicationContextAware {
    private JFrame frame = new JFrame("RabbitMQ Monitor");
    private JTextField exchangeInput = new JTextField(20);
    private JButton btn = new JButton("Watch");
    private ApplicationContext applicationContext;
    
    public ExchangeMonitorConfiguration() {
        initComponents();
    }
    
    private void initComponents() {
        JPanel inputs = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputs.add(new JLabel("Exchange:"));
        inputs.add(exchangeInput);
        inputs.add(btn);
        
        frame.setLayout(new BorderLayout(5, 5));
        frame.add(inputs, BorderLayout.NORTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(450, 65));
        frame.setResizable(false);
        frame.pack();
        
        btn.addActionListener(e -> {
            if(applicationContext != null) {
                String _e = exchangeInput.getText().trim();
                applicationContext.publishEvent(new MonitorExchangeEvent(frame, _e));
            }
        });
        
        frame.setVisible(true);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
