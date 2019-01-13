/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.service.calculator.simple;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.beans.factory.annotation.Value;

import qrmi.core.annotation.RabbitRemote;
import qrmi.tools.api.Calculator;
import qrmi.tools.api.CalculatorResult;

/**
 * The queue/exchange binding is set to not auto delete so that if the server 
 * dies and restarts, then the client connection can pick back up using Spring
 * AMQP library.
 * 
 * @author thinh ho
 *
 */
@RabbitRemote(
    name = "Calculator",
    description = "Calculator (simple) API, author: Thinh Ho",
    remoteInterface = Calculator.class,
    binding = @QueueBinding(
        exchange = @Exchange(
            name = "example.Calculator",
            autoDelete = Exchange.FALSE,
            declare = Exchange.TRUE,
            durable = Exchange.TRUE,
            type = ExchangeTypes.DIRECT),
        value = @Queue(
            name = "example.Calculator_queue",
            autoDelete = Exchange.FALSE,
            declare = Exchange.TRUE,
            durable = Exchange.TRUE,
            exclusive = Exchange.FALSE),
        key = {"qrmi.tools.api.Calculator"})
)
public class SimpleCalculator implements Calculator {
    private static final Logger logger = LoggerFactory.getLogger(SimpleCalculator.class);
    
    @Value("${qrmi.px}")
    private String id;
    
    @Override
    public CalculatorResult add(Double a, Double b) {
        logger.info(String.format("Service %s received add", id));
        if(Objects.isNull(a) || Objects.isNull(b)) {
            throw new IllegalArgumentException(String.format("Invalid argument(s): a=%s, b=%s", a, b));
        }
        CalculatorResult result = new CalculatorResult();
        result.setSource(id);
        result.setValue(a + b);
        return result;
    }

    @Override
    public void compute(double a) {
        logger.info(String.format("Service %s received compute(%s)", id, a));
    }

}
