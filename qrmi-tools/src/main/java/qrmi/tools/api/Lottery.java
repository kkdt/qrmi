/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.api;

public interface Lottery {
    /**
     * Notifies that winning numbers are available.
     * 
     * @return
     */
    void resultsAvailable(LotteryResult results);
}
