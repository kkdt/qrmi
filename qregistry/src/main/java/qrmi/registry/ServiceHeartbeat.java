/*
 * Copyright (c) 2019. thinh ho
 * This file is part of 'qregistry_main' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */

package qrmi.registry;

import java.util.Date;
import org.springframework.context.ApplicationEvent;
import qrmi.support.RabbitObjectMetadata;

public class ServiceHeartbeat extends ApplicationEvent {
    private final RabbitObjectMetadata metadata;
    private final Date time;

    /**
     * The event wrapping the heartbeat data.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param metadata
     * @param time
     */
    public ServiceHeartbeat(Object source, RabbitObjectMetadata metadata, Date time) {
        super(source);
        this.metadata = metadata;
        this.time = time;
    }

    public RabbitObjectMetadata getMetadata() {
        return metadata;
    }

    public Date getTime() {
        return time;
    }
}

