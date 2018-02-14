Long Polling Example
===================

This project is a derivative of <https://github.com/sgilda/wildfly-quickstart/tree/master/helloworld-html5/>

Overview
-----------

This project demonstrates the use of JavaEE Asynchronous Responses in order to support long-polling server-side push notifications.

Deploying the application
-------------------------

First you need to start the Wildfly container. To do this, run

    $JBOSS_HOME/bin/standalone.sh

or if you are using windows

    $JBOSS_HOME/bin/standalone.bat

To deploy the application, you first need to produce the archive to deploy using
the following Maven goal:

    mvn package

You can now deploy the artifact by executing the following command:

    mvn wildfly:deploy

This will deploy both the client and service applications.

The application will be running at the following URL <http://localhost:8080/wildfly-long-polling/>.

To undeploy run this command:

    mvn wildfly:undeploy

You can also start the JBoss container and deploy the project using JBoss Tools. See the
<a href="https://github.com/wildfly/quickstart/guide/Introduction/" title="Getting Started Developing Applications Guide">Getting Started Developing Applications Guide</a>
for more information.


