/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.api;

import java.util.Date;

import qrmi.support.RabbitObjectMetadata;

/**
 * Any API/service that is aware of the the Service Registry must meet this 
 * specifications for broadcasting status data.
 * 
 * @author thinh ho
 *
 */
public interface QRMIRegistryAware extends QRMIBroadcast {
    /**
     * Broadcast to the Service Registry that the service specified by the the
     * <code>metadata</code> is available.
     * 
     * @param metadata
     */
    void serviceAvailable(RabbitObjectMetadata metadata);
    
    /**
     * Heartbeat broadcast indicating the the service is still up at the specified
     * time.
     * 
     * @param metadata
     * @param currentTime
     */
    void heartbeat(RabbitObjectMetadata metadata, Date currentTime);
    
    /**
     * Service is no longer available for consumption.
     * 
     * @param metadata
     */
    void serviceShutdown(RabbitObjectMetadata metadata);
}
