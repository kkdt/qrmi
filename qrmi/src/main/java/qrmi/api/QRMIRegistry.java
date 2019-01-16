/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.api;

import java.util.List;

import qrmi.support.QRMIRemoteException;
import qrmi.support.RabbitObjectMetadata;

public interface QRMIRegistry {
    /**
     * Lookup a particular service name.
     * 
     * <p>
     * If there are multiple instances of the services, more than one entry will
     * be returned.
     * </p>
     * 
     * @param name
     * @return
     * @throws QRMIRemoteException
     */
    List<RabbitObjectMetadata> lookup(String name) throws QRMIRemoteException;
    
    /**
     * Return all remote Rabbit object(s) currently binded/exported to RabbitMQ
     * registry.
     * 
     * <p>
     * This is a snapshot of the current registry at time of invocation.
     * </p>
     * 
     * @return
     * @throws QRMIRemoteException
     */
    List<RabbitObjectMetadata> list() throws QRMIRemoteException;
    
    /**
     * Binding lets the registry know that the remote service/API identified by
     * the <code>metadata</code> is available.
     * 
     * @param metadata
     * @throws QRMIRemoteException
     */
    void bind(RabbitObjectMetadata metadata) throws QRMIRemoteException;
    
    /**
     * Unbinding lets the registry know that the remote service identified by 
     * the <code>metadata</code> is no longer available.
     * 
     * @param metadata
     * @throws QRMIRemoteException
     */
    void unbind(RabbitObjectMetadata metadata) throws QRMIRemoteException;
}
