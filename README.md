Strava OAuth2 Proof of Concept
==============================

Proof of concept showing how to OAuth2 authenticate with Strava from a Java
Application. OAuth2 allows applications to authenticate with services without
revealing the user's username and password to the application (well that's
the theory, anyway). The principal use case is to allow one web service to
authenticate with another web service. For example to allow Instagram to post
images to your Twitter feed.

The flow is:
* Service A registers with Service B

* You ask service A to post to Service B
* Service A redirects your web browser to Service B's authorization service with Service A's client code and a callback URL on Service A
* You log in (if necessary) with Service B and grant Service A the authorization it is requesting
* Service B redirects your browser to Service A's callback URL with an access code as a parameter
* Service A request's a bearer token using the access code and a client secret

This falls down a bit for authorizing applications because you need to be able to

* Launch a Web Brower
* Start a server to listen for authorization callbacks.
* The client secret is encoded in the application. So it can be seen by "hackers" who can use it for their application.

Library Dependencies:
com.googlecode.json-simple - JSON processing
org.junit - Unit Tests
apache.httpclient - HTTP Communication with Strava
apache.httpcore
hamcrest-core
httpmime - File uploads
apache-mime-4j - File Uploads