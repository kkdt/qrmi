# qrmi

# Overview

The goal of this project is to provide service discovery over RabbitMQ using Spring AMQP/RabbitMQ libraries.

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

# qrmi-core

Main supporting library for this project.

# qrmi-tools

Tools all start with a single Spring Boot application entry that points to a separate `Configuration` to load up an application. Each application and all its required components are packaged under a single package structure so that the Spring Boot entry point can do a package `@ComponentScan`.

`java -jar -Dqrmi.px=SimpleCalculator1 -Dqrmi.px.package=qrmi.tools.service.calculator.simple qrmi-tools/build/libs/qrmi-tools-0.1.jar`

`java -jar -Dqrmi.px=RogueCalcuator -Dqrmi.px.package=qrmiools.service.calculator.rogue qrmi-tools/build/libs/qrmi-tools-0.1.jar`

`java -jar -Dqrmi.px=CalculatorClient1 -Dqrmi.px.package=qrmi.tools.client.calculator qrmi-tools/build/libs/qrmi-tools-0.1.jar`

# Vagrant

* RabbitMQ default port `5672` (on host machine `6859`)
* RabbitMQ Management port `15672` (on host machine `6858`)