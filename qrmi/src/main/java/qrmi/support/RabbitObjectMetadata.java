/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.support;

import java.io.Serializable;

/**
 * Metadata of a remote Rabbit object binded to RabbitMQ over an API.
 *      
 * @author thinh ho
 *
 */
public class RabbitObjectMetadata implements Serializable {
    private static final long serialVersionUID = -8892221978643884582L;
    
    private RabbitBrokerMetadata brokerLocation;
    private String remoteInterface;
    private String description;
    private String name;
    private String exchange;
    private String queue;

    public RabbitBrokerMetadata getBrokerLocation() {
        return brokerLocation;
    }

    public void setBrokerLocation(RabbitBrokerMetadata brokerLocation) {
        this.brokerLocation = brokerLocation;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getRemoteInterface() {
        return remoteInterface;
    }

    public void setRemoteInterface(String remoteInterface) {
        this.remoteInterface = remoteInterface;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((brokerLocation == null) ? 0 : brokerLocation.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((exchange == null) ? 0 : exchange.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((queue == null) ? 0 : queue.hashCode());
        result = prime * result + ((remoteInterface == null) ? 0 : remoteInterface.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RabbitObjectMetadata other = (RabbitObjectMetadata) obj;
        if (brokerLocation == null) {
            if (other.brokerLocation != null)
                return false;
        } else if (!brokerLocation.equals(other.brokerLocation))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (exchange == null) {
            if (other.exchange != null)
                return false;
        } else if (!exchange.equals(other.exchange))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (queue == null) {
            if (other.queue != null)
                return false;
        } else if (!queue.equals(other.queue))
            return false;
        if (remoteInterface == null) {
            if (other.remoteInterface != null)
                return false;
        } else if (!remoteInterface.equals(other.remoteInterface))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RabbitObjectMetadata [brokerLocation=" + brokerLocation + ", remoteInterface=" + remoteInterface
            + ", description=" + description + ", name=" + name + ", exchange=" + exchange + ", queue=" + queue + "]";
    }
}
