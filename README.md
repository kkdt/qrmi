# qrmi

# Overview

The goal of this project is to provide service discovery over RabbitMQ using Spring AMQP/RabbitMQ libraries.

## Why RabbitMQ?

1. RabbitMQ provides load balancing, clustering, mirroring capability
2. Can be configured for high throughput and high availability
3. STOMP plugin to support web clients
4. CLI, REST API, and Java libraries for broker management

## Service Discovery

Analogous to an RMI-based implementation, RabbitMQ will be the registry where services can publish their APIs for other services/clients to consume. The registry will provide the following high-level capabilities.

1. Service/API lookup
2. Listing APIs
3. Register an API
4. Registry holds metadata that will allow consumer to bind to a remote API

RabbitMQ will facilitate the routing and the communication infrastructure. One of the goal is to make the best attempt to keep communication logic decoupled from the application logic. 

## API-Enforced Rabbit Interfaces

To facilitate service discovery, communication over RabbitMQ will be enforced by APIs. There are "exporter" classes and "locator" classes in `qrmi-core` project.

* Exporter binds an API to RabbitMQ, attaching the underlying implementation.

* Locator look up an API on RabbitMQ, returning a stub/proxy of the interface for remote access.

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

# qrmi-core

Main supporting library for this project.

# qrmi

The service discovery server that can be deployed and run against a broker. This is analogous to executing `rmiregsitry &`.

# qrmi-tools

All tools start with a single Spring Boot application entry that points to a separate `Configuration` to load up an application. Each application and all its required components are packaged under a single package structure so that the Spring Boot entry point can do a package `@ComponentScan`. All tools are run under Vagrant; therefore, to run the examples below, please build and do a `vagrant up` first.

## Example 1: Calculator API

The Calculator example involves an API (`qrmi.tools.api.Calculator`) with both a return type and a `void`. It follows the request-response style of interaction. A testing scenario would be to export multiple instances of the Calculator API onto RabbitMQ and run the Calculator Client to interact with the API.

**Highlights**

1. Using the `@RabbitRemote` annotation to export the API to RabbitMQ.
1. Load balancing is free with RabbitMQ which load balance messages between consumers - i.e. multiple instances of the API.
2. It is possible for a "rogue" application to bind itself to the same exchange/routing key.

**Commands**

`java -jar -Dqrmi.px=SimpleCalculator1 -Dqrmi.px.package=qrmi.tools.service.calculator.simple qrmi-tools/build/libs/qrmi-tools-0.1.jar`

`java -jar -Dqrmi.px=RogueCalcuator -Dqrmi.px.package=qrmi.tools.service.calculator.rogue qrmi-tools/build/libs/qrmi-tools-0.1.jar`

`java -jar -Dqrmi.px=CalculatorClient1 -Dqrmi.px.package=qrmi.tools.client.calculator qrmi-tools/build/libs/qrmi-tools-0.1.jar`

## Example 2: Lottery API

The Lottery example involves an API (`qrmi.tools.api.Lottery`) that is a pub/sub-based style of interaction.

**Highlights**

1. Two different implementations of the same API with a `@RabbitConsumer` annotation to export to RabbitMQ.
2. Consumers are binded to the same queue with different routing keys through the annotation.

**Commands**

`java -jar -Dqrmi.px=LotteryConsumers -Dqrmi.px.packagermi.tools.service.lottery.consumer qrmi-tools/build/libs/qrmi-tools-0.1.jar`

`java -jar -Dqrmi.px=LotteryPublisher -Dqrmi.px.package=qrmi.tools.service.lottery.publisher qrmi-tools/build/libs/qrmi-tools-0.1.jar`

# Vagrant

* RabbitMQ default port `5672` (on host machine `6859`)
* RabbitMQ Management port `15672` (on host machine `6858`)