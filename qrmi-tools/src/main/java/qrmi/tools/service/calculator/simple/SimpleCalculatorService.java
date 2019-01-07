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
 * separate application.
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
public class SimpleCalculatorService implements ApplicationRunner {
    
    Logger logger = LoggerFactory.getLogger(SimpleCalculatorService.class);
    
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
        
//        TopicExchange replyExchange = new TopicExchange("qrmi.reply", false, false);
//        String replyQueue = new Base64UrlNamingStrategy("qrmi.CalculatorClient.").generateName();
//        Queue queue = new Queue(replyQueue, false, true, true);
//        amqpAdmin.declareExchange(replyExchange);
//        amqpAdmin.declareQueue(queue);
//        Binding b = BindingBuilder
//            .bind(queue)
//            .to(replyExchange)
//            .with(replyQueue);
//        amqpAdmin.declareBinding(b);
//        
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setExchange("example.Calculator");
////        template.setRoutingKey(Calculator.class.getName());
//        template.setReplyAddress(String.format("%s/%s", replyExchange.getName(), replyQueue));
//        template.setReceiveTimeout(-1);
//        
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
//        container.setQueues(queue);
//        container.setAmqpAdmin(amqpAdmin);
//        container.setMessageListener(template);
//        container.start();
//        
//        AmqpProxyFactoryBean factoryBean = new AmqpProxyFactoryBean();
//        factoryBean.setServiceInterface(Calculator.class);
//        factoryBean.setAmqpTemplate(template);
//        factoryBean.setRoutingKey(Calculator.class.getName());
//        factoryBean.afterPropertiesSet();
//        Calculator client = (Calculator)factoryBean.getObject();
//        final Random rand = new Random();
//        
//        Runnable r = () -> {
//            while(true) {
//                try {
//                    double x = rand.nextDouble();
//                    double y = rand.nextDouble();
//                    logger.info(String.format("Sending add(%s,%s)", x, y));
//                    double z = client.add(x, y);
//                    logger.info(String.format("add(%s,%s) = %s", x, y, z));
//                } catch (Throwable t) {
//                    logger.error("Cannot invoke api", t);
//                }
//                try {
//                    Thread.sleep(2000L);
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        };
//        new Thread(r, "RandomAdd").start();
    }
}
