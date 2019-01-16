/*
 * Copyright (c) 2019. thinh ho
 * This file is part of 'qrmi-tools_main' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */

package qrmi.tools.client.ui;

import javax.swing.*;
import java.util.function.BiConsumer;

public class UIWorker {
    /**
     * Add text to text area.
     */
    protected static final BiConsumer<JTextArea, String> doLater = (area, text) ->
        SwingUtilities.invokeLater(() -> area.append(String.format("%s\n", text)));
}
