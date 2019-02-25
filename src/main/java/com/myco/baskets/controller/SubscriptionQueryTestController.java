package com.myco.baskets.controller;

import java.util.concurrent.CompletableFuture;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.myco.baskets.BasketView;
import com.myco.baskets.BasketViewByIdQuery;

@RestController()
public class SubscriptionQueryTestController {

  @Autowired
  QueryGateway queryGateway;

  @GetMapping("/sqtest/{id}/standard")
  public CompletableFuture<BasketView> standard(@PathVariable("id") String id) {
    return queryGateway.query(new BasketViewByIdQuery(id), ResponseTypes.instanceOf(BasketView.class));
  }

  @GetMapping("/sqtest/{id}/sqi")
  public BasketView sqi(@PathVariable("id") String id) {
    return queryGateway.subscriptionQuery(new BasketViewByIdQuery(id), ResponseTypes.instanceOf(BasketView.class),
        ResponseTypes.instanceOf(BasketView.class)).initialResult().block();
  }
}
