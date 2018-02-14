Spring Polling Example
===================

Overview
-----------

This project demonstrates the use of Spring's [DeferredResult](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/context/request/async/DeferredResult.html) in order to support long-polling server-side push notifications.

In addition, this project demonstrates how to make use of a shared database in order to coordinate long-polling notifications amongst systems that are participating in a cluster configuration.

When an event is triggered that must be notified to long-polling clients, the cluster node that initiated the event will write notification messages to the shared database.

Each cluster node has a [database listener](https://github.com/damianmcdonald/spring-long-polling/blob/master/src/main/java/com/github/damianmcdonald/springlongpolling/longpolling/LongPollingDatabaseListener.java) which polls the database every 5 seconds to check for notification updates.

Each cluster node will get it's node specific updates from the database and will update the long-polling clients that are registered with that node.

Getting started
-----------------

The library was built using the following toolchain:

* http://www.oracle.com/technetwork/java/javase/downloads/index.html[Java Oracle JDK 1.8]
* https://maven.apache.org/download.cgi[Maven 3.2.3]

Your mileage may vary with versions different than the ones specified above.

Follow these steps to get started:

1) Git-clone this repository.

    git clone git://github.com/damianmcdonald/spring-long-polling.git my-project


2) Change directory into your clone:

    cd my-project

3) Use Maven to compile everything:

 mvn clean package


4) Start the application:

    java -jar target/spring-long-polling-0.0.1-SNAPSHOT.jar

5) Connect to the app in a browser:

    http://localhost:8080

