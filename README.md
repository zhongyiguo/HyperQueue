spring mvc
=================
Spring MVC web application using Gradle


Deployment steps:
0. Install gradle if it is not on your machine;
1. Execute command to build the application: gradle build;
2. Run the application:  gradle jettyRunWar;
3. Integrate with eclipse with this command: gradle eclipseWtp;

When the web application is running, do the following for test purpose:

1. Post message to a topic:
wget --post-data 'message=lkjsdsdj' http://localhost:8080/{topic}

2. Get message through a topic
http://localhost:8080/{topic}

3. Open an aggregator page which has consumer and poster
http://localhost:8080/aggregator/{topic}

Enjoy!