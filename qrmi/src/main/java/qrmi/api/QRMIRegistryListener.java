/*
 * Copyright (c) 2019. thinh ho
 * This file is part of 'qrmi-qrmi_main' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */

package qrmi.api;

import java.util.Date;
import qrmi.support.RabbitObjectMetadata;

/**
 * The Service Registry will broadcast data per this API specification.
 */
public interface QRMIRegistryListener extends QRMIBroadcast {
    /**
     * Registry broadcasts a heartbeat.
     *
     * @param metadata
     * @param date
     */
    void heartbeat(RabbitObjectMetadata metadata, Date date);
}
