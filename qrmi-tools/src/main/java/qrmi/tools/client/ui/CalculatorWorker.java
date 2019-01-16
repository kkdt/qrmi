/*
 * Copyright (c) 2019. thinh ho
 * This file is part of 'qrmi-tools_main' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.client.ui;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qrmi.tools.api.Calculator;
import qrmi.tools.api.CalculatorResult;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.function.BiConsumer;

/**
 * Exposing this as a <code>Component</code> so that Spring Boot automatically 
 * detects that this is a non-headless application.
 * 
 * <p>
 * Allows the user to input the number of thread(s) that will invoke the Calculator
 * API all at once.
 * </p>
 * 
 * @author thinh ho
 *
 */
@Component
public class CalculatorWorker extends UIWorker implements InitializingBean {
    
    @Autowired
    private Calculator calculator;
    
    /**
     * Invoke the Calculator API in a separate thread.
     */
    private BiConsumer<JTextArea, CyclicBarrier> invokeCalculator = (area,b) ->
        new Thread(() -> {
            try {
                b.await();
            } catch (Exception e) {
                // ignore
            }
            
            int max = 100;
            final Random rand = new Random();
            Double x = Integer.valueOf(rand.nextInt(max)).doubleValue();
            Double y = Integer.valueOf(rand.nextInt(max)).doubleValue();
            try {
                calculator.compute(x);
                CalculatorResult z = calculator.add(x, y);
                if(z != null) {
                    doLater.accept(area, String.format("Received: add(%s, %s)\t= %s\tfrom %s", x, y, z.getValue(), z.getSource()));
                } else {
                    doLater.accept(area, String.format("add(%s, %s)\t= No Answer", x, y));
                }
            } catch (Exception e) {
                doLater.accept(area, String.format("Encountered exception: %s, %s", e.getClass().getName(), e.getMessage()));
            }
        }).start();

    @Override
    public void afterPropertiesSet() throws Exception {
        JTextField workersInput = new JTextField(5);
        JButton btn = new JButton("Start");
        
        JPanel inputs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputs.add(new JLabel("Num Threads:"));
        inputs.add(workersInput);
        inputs.add(btn);
        inputs.add(Box.createHorizontalStrut(150));
        
        final JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        DefaultCaret c = (DefaultCaret)text.getCaret();
        c.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane pane = new JScrollPane(text);
        
        JPanel contents = new JPanel(new BorderLayout(5, 5));
        contents.add(inputs, BorderLayout.NORTH);
        contents.add(pane, BorderLayout.CENTER);
        
        JFrame frame = new JFrame("Calculator Client");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(contents);
        frame.setVisible(true);
        frame.pack();
        SwingUtilities.invokeLater(() -> frame.setSize(new Dimension(500, 300)));
        
        btn.addActionListener(event -> {
            String input = workersInput.getText();
            if(!input.isEmpty()) {
                int count;
                try {
                    count = Integer.parseInt(input);
                } catch (Exception e) {
                    doLater.accept(text, String.format("Invalid input: %s", input));
                    return;
                }
                
                final CyclicBarrier b = new CyclicBarrier(count);
                for(int i = 0; i < count; i++) {
                    invokeCalculator.accept(text, b);
                }
            }
        });
    }

}