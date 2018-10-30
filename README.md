# axon-subscription-queries-sse

A sample demonstrating how to implement axon subscription queries as server sent events (SSE) to a javascript application.

This sample uses this [EventSource polyfill](https://github.com/EventSource/eventsource) in order to support IE and custom headers in subscription request.

## Usage

* clone repository
* build the docker container
* launch the containers
* open application

### clone the repository

```
git clone git@github.com:troyhart/axon-subscription-queries-sse.git
```

### build the docker container

From the project root directory:

```
$ ./build.sh
```

### launch the containers

From the project root directory:

```
$ docker-compose up
```

### open application

Point your browser to: [http://localhost:8686](http://localhost:8686)

The application is a dumb little basket creater. You can create a basket of a given type and then add things to it. Each time you add a thing (the combination of name and description must be unique for each thing added) the model is updated and the new model is pushed from the server and the JSON data of the message is rendered on the page. 

You can also subscribe to a known basket identifier, which is useful to demonstrate that multiple browsers are updated the the model changes....
