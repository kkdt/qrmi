# qrmi

> This is a prototype that explores RabbitMQ, microservices, and service discovery.

> All README documents are currently in development.

# Overview

The goal of this project is to provide service discovery over RabbitMQ using Spring AMQP/RabbitMQ libraries.

## References

1. https://www.rabbitmq.com/tutorials/amqp-concepts.html

2. https://insidethecpu.com/2014/11/17/load-balancing-a-rabbitmq-cluster/

3. https://www.nginx.com/blog/service-discovery-in-a-microservices-architecture/

4. https://www.rabbitmq.com/cluster-formation.html

## Why RabbitMQ?

1. Consumer-level load balancing
2. Node clustering and peer discovery
3. Queue mirroring
4. Can be configured for high throughput and high availability
5. STOMP plugin to support web clients
6. CLI, REST API, and Java libraries for broker management

## Service Discovery

Analogous to an RMI-based implementation, RabbitMQ will be the registry where services can publish their APIs for other services/clients to consume. The registry will provide the following high-level capabilities.

1. Service/API lookup
2. Listing APIs
3. Register an API
4. Registry holds metadata that will allow clients/services to bind to a remote API

RabbitMQ will facilitate the routing and the communication infrastructure. One of the goal is to make the best attempt to keep communication logic decoupled from the application logic. 

## API-Enforced Rabbit Interfaces

To facilitate service discovery, communication over RabbitMQ will be enforced by APIs - provided by the `qrmi-core` module.

## Modules

| Module        | Description                     |
| ------------- | ------------------------------- |
| qrmi-core     | [README](qrmi-core/README.md)   |
| qrmi-tools    | [README](qrmi-tools/README.md)  |
| qrmi          | [README](qrmi/README.md)        |

# Vagrant

Vagrant has two configured VMs:

1. qrmi1 with broker vhost `qrmiBroker1` (all tools are configured to use this for now)

    * RabbitMQ default port `5672` (on host machine `6859`)
    
    * RabbitMQ Management port `15672` (on host machine `6858`)

2. qrmi2 with broker vhost `qrmiBroker2`

    * RabbitMQ default port `5672` (on host machine `7859`)
    
    * RabbitMQ Management port `15672` (on host machine `7858`)

# Initial Thoughts

1. Java RMI registry design/pattern

2. Support to handle custom client connection properties

3. Support to handle a custom "reply" exchange/queue routing

4. Support both Spring and non-Spring applications

5. Client-Server interactions

    a. request-response
    
    b. broadcast
    
    c. asynchronous request

6. Load balancing over RabbitMQ

7. High throughput and high availability configurations on RabbitMQ

8. Support for multiple instances of the same service/API

9. Support for custom, human-readable exchange and queue naming schemes

10. Logging support

11. Unit testing

12. Security
