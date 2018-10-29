package com.myco.baskets.controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myco.baskets.AddThingToBasket;
import com.myco.baskets.BasketView;
import com.myco.baskets.CreateBasket;
import com.myco.baskets.QueryUtils;
import com.myco.baskets.Thing;

import reactor.core.publisher.Flux;


@RestController()
public class BasketRestController {

  private QueryGateway queryGateway;
  private CommandGateway commandGateway;


  @Autowired
  public BasketRestController(CommandGateway commandGateway, QueryGateway queryGateway) {

    this.commandGateway = commandGateway;
    this.queryGateway = queryGateway;
  }


  @PostMapping(path = "/api/baskets", consumes = MediaType.APPLICATION_JSON_VALUE)
  public CompletableFuture<String> newBasket(@RequestBody CreateBasketRequest requestBody) {

    return commandGateway.send(new CreateBasket(UUID.randomUUID().toString(), requestBody.getType()));
  }


  @PutMapping(path = "/api/baskets/{basketId}/things", consumes = MediaType.APPLICATION_JSON_VALUE)
  public CompletableFuture<Void> addThing(@PathVariable String basketId, @RequestBody AddThingRequest requestBody) {

    return commandGateway
        .send(new AddThingToBasket(basketId, new Thing(requestBody.getName(), requestBody.getDescription())));
  }


  @GetMapping(path = "/api/baskets", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<BasketView> typeListUpdates(@RequestParam("type") String basketType) {

    return Flux.<BasketView> create(emitter -> {
      SubscriptionQueryResult<List<BasketView>, BasketView> queryResult =
          QueryUtils.subscribeToBasketViewsByType(queryGateway, basketType);
      queryResult.initialResult().subscribe(basketViews -> basketViews.forEach(emitter::next));
      queryResult.updates().doOnComplete(emitter::complete).subscribe(emitter::next);
    });
  }

  @GetMapping(path = "/api/baskets/{basketId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<BasketView> basketUpdates(@PathVariable String basketId) {
    SubscriptionQueryResult<BasketView, BasketView> res = QueryUtils.subscribeToBasketViewById(queryGateway, basketId);
    return res.initialResult().concatWith(res.updates());
  }
}
