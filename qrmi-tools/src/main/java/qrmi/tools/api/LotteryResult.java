/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.api;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LotteryResult implements Serializable {
    private static final long serialVersionUID = -7917900047113773490L;
    
    private final String source;
    private final List<Integer> list;
    
    public LotteryResult(String source, int... numbers) {
        if(numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("Must provide winning numbers");
        }
        this.source = source;
        list = Arrays.stream(numbers).boxed().collect(Collectors.toList());
    }
    
    public String getSource() {
        return source;
    }
    
    public List<Integer> getNumbers() {
        return list;
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s", source, Arrays.toString(list.toArray(new Integer[list.size()])));
    }
}
