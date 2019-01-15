/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.api;

/**
 * Sample API to expose on Rabbit.
 * 
 * @author thinh ho
 *
 */
public interface Calculator {
    /**
     * Add two numeric values.
     * 
     * @param a
     * @param b
     * @return
     */
    CalculatorResult add(Double a, Double b);
    /**
     * No response method signature.
     * 
     * @param a
     */
    void compute(double a);
}
