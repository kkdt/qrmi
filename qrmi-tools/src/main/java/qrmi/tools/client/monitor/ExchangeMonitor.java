/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.client.monitor;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.stereotype.Component;

@Component
public class ExchangeMonitor implements ApplicationListener<MonitorExchangeEvent> {
    private static Logger logger = LoggerFactory.getLogger(ExchangeMonitor.class);
    
    @Autowired
    private ConnectionFactory connectionFactory;
    
    @Autowired
    private AmqpAdmin amqpAdmin;
    
    private final SimpleMessageConverter converter = new SimpleMessageConverter();

    @Override
    public void onApplicationEvent(MonitorExchangeEvent event) {
        String exchange = event.getExchange();
        logger.info("Received event to monitor {}", exchange);
        
        if(Objects.nonNull(amqpAdmin) && Objects.nonNull(connectionFactory) 
            && Objects.nonNull(exchange) && !exchange.isEmpty()) 
        {
            displayContents(event.getWindow(), exchange);
        }
    }
    
    private void displayContents(JFrame reference, String exchange) {
        final JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        DefaultCaret c = (DefaultCaret)text.getCaret();
        c.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane pane = new JScrollPane(text);
        
        JFrame frame = new JFrame(exchange);
        frame.setLocationRelativeTo(reference);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(pane, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        
        Queue queue = new Queue("monitor", false, true, true);
        String q = amqpAdmin.declareQueue(queue);
        Binding b = BindingBuilder.bind(queue)
            .to(new DirectExchange(exchange))
            .with("qrmi.tools.api.Calculator");
        amqpAdmin.declareBinding(b);
        
        final AtomicInteger counter = new AtomicInteger(0);
        DirectMessageListenerContainer container = new DirectMessageListenerContainer(connectionFactory);
        container.setAmqpAdmin(amqpAdmin);
        container.setConnectionFactory(connectionFactory);
        container.setAutoDeclare(true);
        container.setQueueNames(q);
        container.setMessageListener(m -> {
            MessageProperties properties = m.getMessageProperties();
            Object _m = converter.fromMessage(m);
            StringBuilder info = new StringBuilder();
            if (_m instanceof RemoteInvocation) {
                RemoteInvocation invocation = (RemoteInvocation) _m;
                info.append(String.format("Remote Invocation: %s(%s)", 
                    invocation.getMethodName(),
                    Stream.of(invocation.getArguments()).map(Object::toString).collect(Collectors.toSet())));
            } else {
                info.append(_m.toString());
            }
            text.append(String.format("Message %s:\n%s %s\n\n", counter.incrementAndGet(), properties.toString(), info.toString()));
        });
        container.start();
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                container.destroy();
            }
        });
    }

}
