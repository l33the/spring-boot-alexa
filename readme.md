<h1>Spring Boot Starter Alexa</h1>

This plugin supports creating Spring Boot Webservices with Amazon Alexa. With this, you can without using
AWS create Skills with a HTTPS Endpoint.
The setup is pretty simple.

<h2>Setup</h2>

In your `application.properties` add the following :

```
#Your skill id
alexa.skill.id=

#Choose an port for HTTPS (every port except 8080)
server.port=

#Choose where the alexa endpoint should be reachable
alexa.skill.endpoint.url=/alexa

#Setup your SSL. You need your certificates p12 file.
server.ssl.key-store= classpath:keystore.p12
server.ssl.key-store-password=<password>
server.ssl.keyStoreType= PKCS12
server.ssl.keyAlias=tomcat

```

<h2>Add RequestHandler</h2>

With Spring Boot you dont need to register your RequestHandler. Just add `@Component` 
to your RequestHandler.


This package includes Amazons official ASK SDK.

