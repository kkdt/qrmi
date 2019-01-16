/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.support;

public class QRMIRemoteException extends RuntimeException {
    private static final long serialVersionUID = 7757485409041321444L;
    
    public QRMIRemoteException() {
        super();
    }

    public QRMIRemoteException(String message) {
        super(message);
    }

    public QRMIRemoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public QRMIRemoteException(Throwable cause) {
        super(cause);
    }
}
