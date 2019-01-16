/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.support;

import java.io.Serializable;

/**
 * Rabbit Broker metadata.
 * 
 * @author thinh ho
 *
 */
public class RabbitBrokerMetadata implements Serializable {
    private static final long serialVersionUID = -5253893992703458983L;
    
    private String host = "localhost";
    private String vhost;
    private int port = 5672;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
        result = prime * result + ((vhost == null) ? 0 : vhost.hashCode());
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
        RabbitBrokerMetadata other = (RabbitBrokerMetadata) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (port != other.port)
            return false;
        if (vhost == null) {
            if (other.vhost != null)
                return false;
        } else if (!vhost.equals(other.vhost))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RabbitBrokerMetadata [host=" + host + ", vhost=" + vhost + ", port=" + port + "]";
    }

}
