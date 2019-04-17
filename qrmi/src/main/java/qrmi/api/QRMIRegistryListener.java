/*
 * Copyright (c) 2019. thinh ho
 * This file is part of 'qrmi-qrmi_main' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */

package qrmi.api;

import java.util.Date;
import qrmi.support.RabbitBrokerMetadata;

/**
 * The Service Registry will broadcast data per this API specification.
 */
public interface QRMIRegistryListener extends QRMIBroadcast {
    /**
     * Registry broadcasts a heartbeat.
     *
     * @param broker the broker the registry currently exists on.
     * @param date
     */
    void heartbeat(RabbitBrokerMetadata broker, Date date);
}
