/*
 * Copyright (c) 2019. thinh ho
 * This file is part of 'qrmi-tools' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */

package qrmi.tools.client.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qrmi.api.QRMIRegistry;
import qrmi.support.RabbitObjectMetadata;

@Component
public class RegistryWorker extends UIWorker implements InitializingBean {

    @Autowired
    QRMIRegistry registry;

    @Override
    public void afterPropertiesSet() throws Exception {
        JButton listBtn = new JButton("List");
        JButton lookupBtn = new JButton("Lookup");
        JTextField input = new JTextField(20);

        JPanel inputs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputs.add(new JLabel("Name:"));
        inputs.add(input);
        inputs.add(lookupBtn);
        inputs.add(listBtn);

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

        JFrame frame = new JFrame("Service Registry");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(contents);
        frame.setVisible(true);
        frame.pack();
        SwingUtilities.invokeLater(() -> frame.setSize(new Dimension(500, 300)));

        lookupBtn.addActionListener(event -> {
            String i = input.getText();
            if(!i.isEmpty()) {
                try {
                    List<RabbitObjectMetadata> all = registry.lookup(i);
                    doLater.accept(text, String.format("Found %s service(s) matching '%s': %s", all.size(), i, all));
                } catch (Exception e) {
                    doLater.accept(text, String.format("Encountered exception: %s, %s", e.getClass().getName(), e.getMessage()));
                }
            }
        });

        listBtn.addActionListener(event -> {
            try {
                List<RabbitObjectMetadata> all = registry.list();
                doLater.accept(text, String.format("Found %s service(s): %s", all.size(), all));
            } catch (Exception e) {
                doLater.accept(text, String.format("Encountered exception: %s, %s", e.getClass().getName(), e.getMessage()));
            }
        });
    }
}
