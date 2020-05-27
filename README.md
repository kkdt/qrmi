# qrmi

# Overview

Using the Spring AMQP and Spring Remoting libraries, this module provides support for API-enforced RabbitMQ interfaces. The main goal of this project is to decouple RabbitMQ communication logic from the actual API and its implementation(s). 

It addresses the following client-server interactions at the API level:

1. synchronous request-response

2. broadcast

3. asynchronous request (TODO)

## API Development

When designing an API, developers define what the interface looks (e.g. the Java `interface` definition) and provide an implementation of that interface. Clients or services that consume that API follow the interface specifications without knowing any implementation details.

The process of exposing an API implementation should be a separate concern and possibly involve another set of requirements to support different clients - i.e. RESTful, SOAP, RabbitMQ, etc. This module will facilitate exposing APIs to RabbitMQ for application systems that already has, or is required to implement RabbitMQ as its messaging infrastructure.

## Exporter and Locator

In this project, there are "exporter" classes and "locator" classes. 

    * Exporter binds an API to RabbitMQ, attaching the underlying implementation.
    
    * Locator looks up an API on RabbitMQ, returning a stub/proxy of the interface for remote access.

# Binding an API to RabbitMQ

## Synchronous Request-Response Style

### 1. Define and Implement the API

```java
public interface Calculator {
    CalculatorResult add(Double a, Double b);
    // supports void request-response 
    void compute(double a);
}

public class SimpleCalculator implements Calculator {...}
```

### 2. Declare Implementation a `@RabbitRemote`

```java
@RabbitRemote(
    name = "Calculator",
    description = "Calculator (simple) API, author: Thinh Ho",
    remoteInterface = Calculator.class,
    binding = @QueueBinding(
        exchange = @Exchange(
            name = "example.Calculator",
            autoDelete = Exchange.FALSE, // exchange is deleted when last queue is unbound from it
            declare = Exchange.TRUE, // controls whether the configured AmqpAdmin will create
            durable = Exchange.TRUE, // exchanges survive broker restart
            type = ExchangeTypes.DIRECT),
        value = @Queue(
            name = "example.Calculator_queue",
            autoDelete = Exchange.FALSE, // queue that has had at least one consumer is deleted when last consumer unsubscribes
            declare = Exchange.TRUE, // controls whether the configured AmqpAdmin will create
            durable = Exchange.TRUE, // the queue will survive a broker restart
            exclusive = Exchange.FALSE), // used by only one connection and the queue will be deleted when that connection closes
        key = {"qrmi.tools.api.Calculator"}) // routing key for this implementation
)
public class SimpleCalculator implements Calculator {...}
```

### 3. Bind the API to RabbitMQ


```java
// import the provided configuration to autodetect RabbitRemote annotated classes in the application class path

@Configuration
@Import(value = {RabbitExportConfiguration.class})
public class SimpleCalculatorService {...}

// Or, programmatically using RabbitExporterBuilder

SimpleCalculator impl = new SimpleCalculator();
new RabbitExporterBuilder(amqpAdmin, connectionFactory)
    .build(impl)
    .export();
```

### 4. Consume the API

```java
@Bean
public RabbitRemoteLocator calculator() {
    RabbitRemoteLocator remoteLocator = new RabbitRemoteLocator(amqpAdmin, connectionFactory);
    // 
    remoteLocator.setServiceInterface(Calculator.class);
    remoteLocator.setRoutingKey(Calculator.class.getName());
    remoteLocator.setExchange("example.Calculator");
    // 
    remoteLocator.setReplyTimeout(3000L);
    remoteLocator.setReplyExchange("qrmi.reply"); // if not set then AMQP will create a temporary queue for replies
    remoteLocator.setReplyQueueNamingStrategy(() -> String.format("qrmi.%s", UUID.randomUUID().toString())); // ignored if the replyExchange is not set
    return remoteLocator;
}
```

## Broadcast Style

In a broadcast-style interaction, the binded remote object (i.e. the API implementation) will be the receiving end. The `RabbitRemoteConsumer` will be the annotation that facilitate this interaction.

To reference the pub/sub pattern, implementations of a broadcast API will be subscribing to RabbitMQ for messages from publishers. Publishers, in this scenario, are any clients/services that have located the API over RabbitMQ and send messages via a stub/proxy reference to the remote API.

### 1. Define and Implement the API

There can be many different implementations.

```java
public interface Lottery {
    void resultsAvailable(LotteryResult results);
}

public class VirginiaLottery implements Lottery {...}
```

### 2. Declare Implementation a `@RabbitRemoteConsumer`

Each implementation can be configured on the same RabbitMQ exchange with routing key specifications.

```java
@RabbitRemoteConsumer(
    name = "Lottery",
    remoteInterface = Lottery.class,
    binding = @QueueBinding(
        exchange = @Exchange(
            name = "example.Lottery",
            autoDelete = Exchange.TRUE,
            declare = Exchange.TRUE,
            durable = Exchange.TRUE,
            type = ExchangeTypes.TOPIC),
        value = @Queue(
            autoDelete = Exchange.TRUE,
            declare = Exchange.TRUE,
            durable = Exchange.TRUE,
            exclusive = Exchange.TRUE),
        key = {"Virginia"}) // routing key for this implementation
)
public class VirginiaLottery implements Lottery {...}
```

### 3. Bind the Subscribers/Implementations to RabbitMQ

```java
// import the provided configuration to autodetect RabbitRemoteConsumer annotated classes in the application class path

@Configuration
@Import(value = {RabbitExportConfiguration.class})
public class LotteryConsumerConfiguration {...}

// Or, programmatically using RabbitExporterBuilder

VirginiaLottery va = new VirginiaLottery(); // routingKey="Virginia"
new RabbitExporterBuilder(amqpAdmin, connectionFactory)
    .build(va)
    .export();

MarylandLottery md = new MarylandLottery(); // routingKey="Maryland"
new RabbitExporterBuilder(amqpAdmin, connectionFactory)
    .build(md)
    .export();
```

### 5. Declare the Publishers

Obtain a reference to the remote API via an extension of Spring `AmqpProxyFactoryBean`. The beans below will be broadcasting messages via API invocation and consumer implementation(s) that has the appropriate `@RabbitRemoteConsumer` configuration will be receiving the API-invoked broadcast.

```java
@Bean
public RabbitConsumerLocator vaLottery(AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory) {
    RabbitConsumerLocator l = new RabbitConsumerLocator(amqpAdmin, connectionFactory);
    l.setServiceInterface(Lottery.class);
    l.setExchange("example.Lottery");
    l.setRoutingKey("Virginia");
    return l;
}

@Bean
public RabbitConsumerLocator mdLottery(AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory) {
    RabbitConsumerLocator l = new RabbitConsumerLocator(amqpAdmin, connectionFactory);
    l.setServiceInterface(Lottery.class);
    l.setExchange("example.Lottery");
    l.setRoutingKey("Maryland");
    return l;
}
```

## Asynchronous Request-Response Style

TODO
