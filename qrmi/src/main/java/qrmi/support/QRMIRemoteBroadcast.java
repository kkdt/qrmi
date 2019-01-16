/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.support;

import org.springframework.amqp.rabbit.annotation.Exchange;

import qrmi.api.QRMIBroadcast;

/**
 * The <code>QRMIRemoteBroadcast</code> captures API implementations that interacts 
 * with a broadcast interface established by the QRMI Registry.
 * 
 * @author thinh ho
 *
 */
public @interface QRMIRemoteBroadcast {
    /**
     * The exchange where the registry is listener for notifications. 
     * 
     * @return
     */
    Exchange broadcast();
    
    /**
     * The broadcast API - enforced to be of type {@code QRMIBroadcast}.
     * 
     * @return
     */
    Class<? extends QRMIBroadcast> broadcastInterface();
    
//    /**
//     * The Rabbit object that is aware of the registry.
//     * 
//     * @return
//     */
//    RabbitRemote object();
}
