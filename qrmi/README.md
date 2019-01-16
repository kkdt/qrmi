# qrmi

# Overview

The Service Discovery Registry server that can be deployed and run against a broker. This is analogous to executing `rmiregsitry &`. There are three parties involved with the RabbitMQ Service Registry.

1. Client - Any component that wishes to locate an API to consume.

2. Service - The service exposed to RabbitMQ via a defined API.

3. Registry - The service discovery registry.

All interactions between the three components will be over multiple service discovery APIs, all packaged under `qrmi.api`.

# Client

A client application interacts with the Service Registry for a stub/proxy reference to an API.

## Client and Registry

1. Lookup an API - Metadata about a specified API will be returned  - i.e. if an API exists in multiple brokers

2. Locate an API - A stub/proxy reference to the API will be returned

3. List available APIs - Metadata about APIs will be returned

## Client and Service

Once a client has an API reference, it will be able to consume it over RabbitMQ.

# Service

A service will interact with the registry for discovery and with clients over its API specifications.

## Service and Registry

1. Bind - A service will bind its API and its implementation to RabbitMQ on start up

2. Broadcast startup - Also as part of its start up process, a service will interact with registry and notify that it is available.

3. Broadcast health - On a scheduled interval, the service must heartbeat the registry to indicate that it is still up; otherwise, the registry will remove service registration after a configurable period of time of no hearbeats.

4. Broadcast shutdown - A service will notify the registry as part of its shutdown process.

# Registry

Each RabbitMQ broker will contain a Service Discovery Registry. Each Registry will interact with both clients and services on its broker (see Multiple Brokers section below). The Registry interactions are defined above for client and services; below highlights the responsibilities of a Registry.

## Store Service Metadata

After a service binds to the broker and broadcast its availability, the Registry must have a registration store for services. Likewise, a Registry must keep this list updated - i.e. when services shut down or have not send their heartbeats after a configurable amount of time.

# Multiple Brokers

In the scenario where there are multiple brokers within an application system, RabbitMQ can be configured so that each broker's Service Discovery Registry is aware of each other and the services exposed to their respective broker. 

For example, service discovery can be configured to occur at a single exchange - let's say, the `service.available` exchange. This exchanged is configured to be federated to all other brokers; and therefore, will be picked up by all other Registries. Suppose each service broadcast to this exchange when they are up. Each Registry will continue to process this registration and will include the actual location to the service when their respectively clients perform a lookup.

Some considerations

1. Queue mirror can facilitate HA of an API/service

2. A load balancer can be the front-facing component for all API consumers to hit to obtain the remote stub/proxy 

3. The "locator" library must be able to handle connection failure (i.e. RabbitMQ broker shutdown) and be able to reconnect to locate another instance of the service on another broker if they are available.

4. The "locator" library or the Registry must be able to give priority to the closest API - i.e. if the API exists on multiple brokers including the broker where the request was made, then the broker instance where the request was made must be the one that is returned. 

# Resources

1. https://blog.stanko.io/supercharging-services-architectures-with-rabbitmq-b2dc75804577

2. https://stackoverflow.com/questions/33952306/microservice-amqp-and-service-registry-discovery

