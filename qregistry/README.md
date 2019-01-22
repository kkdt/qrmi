# qregistry

# Overview

A simple Service Registry prototype and is comprised of the following APIs.

| API                       | Description                     |
| ------------------------- | ------------------------------- |
| `QRMIRegistry`            | Exposed on the `qrmi.service.registry` exchange   |
| `QRMIRegistryAware`       | Listen on the `qrmi.service.available` exchange for service registration and heartbeat  |

# Usage

1. Start up the Vagrant environments

    a. Start up qrmi1 - `vagrant up qrmi1`
    
    b. After qrmi1 is completely up, start up qrmi2 - `vagrant up qrmi2`

2. Log into qrmi1 - `vagrant ssh qrmi1`

3. Unpack the tarball - `tar xvf /vagrant/qregistry/build/distributions/qregistry-0.1.tar`

4. Start the registry - `qregistry-0.1/bin/qregistry`

5. On your host (i.e. your machine), verify RabbitMQ Management

    a. Navigate to `http://localhost:6858/`
    
    b. Login as `guest`/`guest`
    
    c. Verify exchange and queue

