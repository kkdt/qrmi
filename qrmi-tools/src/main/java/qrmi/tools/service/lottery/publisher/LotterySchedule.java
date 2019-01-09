/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.service.lottery.publisher;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import qrmi.tools.api.Lottery;
import qrmi.tools.api.LotteryResult;

/**
 * Schedule two lottery APIs invocation.
 * 
 * @author thinh ho
 *
 */
@Configuration
@EnableScheduling
public class LotterySchedule {
    private static final Random rand = new Random();
    
    @Autowired
    private Lottery vaLottery;
    
    @Autowired
    private Lottery mdLottery;
    
    @Scheduled(fixedRate = 2000)
    public void broadcastVirginia() {
        vaLottery.resultsAvailable(new LotteryResult("VA", 
            rand.nextInt(57), rand.nextInt(57), 
            rand.nextInt(57), rand.nextInt(57), 
            rand.nextInt(57), rand.nextInt(57), rand.nextInt(57)));
    }
    
    @Scheduled(fixedRate = 5000)
    public void broadcastMaryland() {
        mdLottery.resultsAvailable(new LotteryResult("MD", 
            rand.nextInt(57), rand.nextInt(57), 
            rand.nextInt(57), rand.nextInt(57), 
            rand.nextInt(57), rand.nextInt(57), rand.nextInt(57)));
    }
}
