/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.api;

import java.io.Serializable;

/**
 * Wrapper for the answer from the Calculator to capture the source.
 * 
 * @author thinh ho
 *
 */
public class CalculatorResult implements Serializable {
    private static final long serialVersionUID = -2519068693260504356L;
    
    private Object value;
    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
