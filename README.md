# axon-subscription-queries-sse

A sample demonstrating how to implement axon subscription queries as server sent events (SSE) to a javascript application.

This sample uses this [EventStore polyfill](https://github.com/Yaffle/EventSource) in order to support IE and custom headers in subscription request.

## Usage

* clone repository
* Import maven project into your favorite IDE (STS has great spring boot support and is what I recommend)
* Launch the infrastructure (postgres and mongo) with the included `docker-compose.yml`.
* Launch the spring boot application (use STS spring boot dashboard, or however you do it in your IDE or with maven and the command line...)
* Open browser to http://localhost:8080

The application is a dumb little basket creater. You can create a basket of a given type and then add things to it. Each time you add a thing (the combination of name and description must be unique for each thing added) the model is updated and the new model is pushed from the server and the JSON data of the message is rendered on the page. 

You can also subscribe to a known basket identifier, which is useful to demonstrate that multiple browsers are updated the the model changes....
