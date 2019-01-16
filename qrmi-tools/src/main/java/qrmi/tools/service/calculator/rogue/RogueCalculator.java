/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.service.calculator.rogue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import qrmi.core.annotation.RabbitRemote;
import qrmi.tools.api.Calculator;
import qrmi.tools.api.CalculatorResult;

/**
 * See <code>SimpleCalculator</code>.
 * 
 * @author thinh ho
 *
 */
@RabbitRemote(
    name = "Calculator",
    description = "Rogue Calculator",
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
public class RogueCalculator implements Calculator {
    private static final Logger logger = LoggerFactory.getLogger(RogueCalculator.class);
    
    @Override
    public CalculatorResult add(Double a, Double b) {
        logger.info("(Rogue) Calculator received add");
        CalculatorResult result = new CalculatorResult();
        result.setSource(RogueCalculator.class.getSimpleName());
        result.setValue(Double.MIN_VALUE);
        return result;
    }

    @Override
    public void compute(double a) {
        logger.info("(Rogue) Calculator received compute");
    }

}
