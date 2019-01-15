# qrmi-tools

# Overview

All tools start with a single Spring Boot application entry that points to a separate `Configuration` to load up an application. Each application and all its required components are packaged under a single package structure so that the Spring Boot entry point can do a package `@ComponentScan`. All tools are run under Vagrant; therefore, to run the examples below, please build and do a `vagrant up` first at the project root directory level.

# Example 1: Calculator API

The Calculator example involves an API (`qrmi.tools.api.Calculator`) with both a return type and a `void`. It follows the request-response style of interaction. A testing scenario would be to export multiple instances of the Calculator API onto RabbitMQ and run the Calculator Client to interact with the API.

## Highlights

1. Using the `@RabbitRemote` annotation to export the API to RabbitMQ.
1. Load balancing is free with RabbitMQ which load balance messages between consumers - i.e. multiple instances of the API.
2. It is possible for a "rogue" application to bind itself to the same exchange/routing key.

## Commands

`java -jar -Dqrmi.px=SimpleCalculator1 -Dqrmi.px.package=qrmi.tools.service.calculator.simple qrmi-tools/build/libs/qrmi-tools-0.1.jar`

`java -jar -Dqrmi.px=RogueCalcuator -Dqrmi.px.package=qrmi.tools.service.calculator.rogue qrmi-tools/build/libs/qrmi-tools-0.1.jar`

`java -jar -Dqrmi.px=CalculatorClient1 -Dqrmi.px.package=qrmi.tools.client.calculator qrmi-tools/build/libs/qrmi-tools-0.1.jar`

# Example 2: Lottery API

The Lottery example involves an API (`qrmi.tools.api.Lottery`) that is a pub/sub-based style of interaction.

## Highlights

1. Two different implementations of the same API with a `@RabbitConsumer` annotation to export to RabbitMQ.
2. Consumers are binded to the same queue with different routing keys through the annotation.

## Commands

`java -jar -Dqrmi.px=LotteryConsumers -Dqrmi.px.packagermi.tools.service.lottery.consumer qrmi-tools/build/libs/qrmi-tools-0.1.jar`

`java -jar -Dqrmi.px=LotteryPublisher -Dqrmi.px.package=qrmi.tools.service.lottery.publisher qrmi-tools/build/libs/qrmi-tools-0.1.jar`

