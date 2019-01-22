# qrmi-tools

# Overview

All tools start with a single Spring Boot application entry that points to a separate `Configuration` to load up an application. Each application and all its required components are packaged under a single package structure so that the Spring Boot entry point can do a package `@ComponentScan`. All tools are run under Vagrant; therefore, to run the examples below, please build and do a `vagrant up` first at the project root directory level.

# Example 1: Calculator API

The Calculator example involves an API (`qrmi.tools.api.Calculator`) with both a return type and a `void`. It follows the request-response style of interaction. A testing scenario would be to export multiple instances of the Calculator API onto RabbitMQ and run the Calculator Client to interact with the API.

## Highlights

1. Using the `@RabbitRemote` annotation to export the API to RabbitMQ.
2. Load balancing is free with RabbitMQ which load balance messages between consumers - i.e. multiple instances of the API.
3. It is possible for a "rogue" application to bind itself to the same exchange/routing key.
4. Demonstrates Service Registry interaction

## Commands

1. These two commands will be run inside a Vagrant box so you must up/ssh into either qrmi1 or qrmi2

    `java -jar -Dqrmi.px=SimpleCalculator1 -Dqrmi.px.package=qrmi.tools.service.calcular.simple /vagrant/qrmi-tools/build/libs/qrmi-tools-0.1.jar`

    `java -jar -Dqrmi.px=RogueCalcuator -Dqrmi.px.package=qrmi.tools.service.calculator.rogue /vagrant/qrmi-tools/build/libs/qrmi-tools-0.1.jar`

2. Run the client from your workstation (note the port specification to qrmi1)

    `java -jar -Dspring.rabbitmq.port=6859 -Dqrmi.px=Clients -Dqrmi.px.package=qrmi.tools.client.ui qrmi-tools/build/libs/qrmi-tools-0.1.jar`

3. On the "Calculator Client" window, input a number of threads to kick off simultaneous API invocation and click Start.

    You should see the output indicating which service served out each request.

# Example 2: Lottery API

The Lottery example involves an API (`qrmi.tools.api.Lottery`) that is a pub/sub-based style of interaction.

## Highlights

1. Two different implementations of the same API with a `@RabbitConsumer` annotation to export to RabbitMQ.
2. Consumers are binded to the same queue with different routing keys through the annotation.

## Commands

1. Start the consumer on qrmi1

    `java -jar -Dqrmi.px=LotteryConsumers -Dqrmi.px.package=qrmi.tools.service.lottery.consumer /vagrant/qrmi-tools/build/libs/qrmi-tools-0.1.jar`

2. Start the publisher on qrmi2

    `java -jar -Dqrmi.px=LotteryPublisher -Dqrmi.px.package=qrmi.tools.service.lottery.publisher /vagrant/qrmi-tools/build/libs/qrmi-tools-0.1.jar`

3. Note the console log on qrmi1 when the publisher sends data via asynchronous broadcast

# Example 3: Register Calculator API for Service Discovery

The Calculator service will start up as normal but the `qrmi.register` flag will be passed into the application to register with its local Service Registry.

## Highlights

Demonstrate service start up and shutdown and registering and de-registering from Service Registry, respectively.

## Commands

1. Start `qregistry` on qrmi1

2. Log into **qrmi2**

3. Start up the Calculator service on **qrmi2**, passing in the flag to register

    `java -jar -Dqrmi.register=true -Dqrmi.px=SimpleCalculator1 -Dqrmi.px.package=qrmi.tools.service.calculator.simple /vagrant/qrmi-tools/build/libs/qrmi-tools-0.1.jar `

4. Note from the console log on qrmi1 that the service registered itself on start up

5. Run the client from your workstation (note the port specification to qrmi1)

    `java -jar -Dspring.rabbitmq.port=6859 -Dqrmi.px=Clients -Dqrmi.px.package=qrmi.tools.client.ui qrmi-tools/build/libs/qrmi-tools-0.1.jar`

6. On the "Service Registry" window, click List to confirm the Calculator service

7. On the Calculator terminal on **qrmi2**, Control-C to terminate the program and note the registry console log that the service is unbinded.

8. Go back to the "Service Registry" window, click List again to confirm no service available.

# Example 4: Synchronized Service Registry Provided by RabbitMQ

There will be two Service Registries - one on qrmi1 and the other on qrmi2.

## Highlights

All Service Registry data is synchronized across the exchange as services start up and shutdown

## Commands

1. Start `qregistry` on qrmi1

2. Start `qregistry` on qrmi2

3. Start up the Calculator service on **qrmi2**, passing in the flag to register

    `java -jar -Dqrmi.register=true -Dqrmi.px=SimpleCalculator1 -Dqrmi.px.package=qrmi.tools.service.calculator.simple /vagrant/qrmi-tools/build/libs/qrmi-tools-0.1.jar `

4. Note from the console log on qrmi1 and qrmi2 that the service registered itself on start up, keeping registration data synchronized

5. Run the client from your workstation (note the port specification to qrmi1)

   `java -jar -Dspring.rabbitmq.port=6859 -Dqrmi.px=Clients -Dqrmi.px.package=qrmi.tools.client.ui qrmi-tools/build/libs/qrmi-tools-0.1.jar`

6. Run the client from your workstation (note the port specification to qrmi2)

    `java -jar -Dspring.rabbitmq.port=7859 -Dqrmi.px=Clients -Dqrmi.px.package=qrmi.tools.client.ui qrmi-tools/build/libs/qrmi-tools-0.1.jar`

7. On both clients, on the "Service Registry" window, click List to confirm the Calculator service

8. Control-C to terminate both Calculator services and note the registry console logs that the service is unbinded.

9. Go back to the "Service Registry" window, click List again to confirm no service available.