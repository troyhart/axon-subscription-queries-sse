package com.myco.baskets.controller;

import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.myco.baskets.BasketViewByIdQuery;
import com.myco.baskets.BasketViewsByTypeContainsQuery;
import com.myco.baskets.CreateBasket;
import com.myco.baskets.QueryUtils;
import com.myco.baskets.Thing;

import reactor.core.publisher.Flux;


@RestController()
public class BasketRestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasketRestController.class);

  private QueryGateway queryGateway;
  private CommandGateway commandGateway;


  @Autowired
  public BasketRestController(CommandGateway commandGateway, QueryGateway queryGateway) {

    this.commandGateway = commandGateway;
    this.queryGateway = queryGateway;
  }


  @PostMapping(path = "/api/baskets", consumes = MediaType.APPLICATION_JSON_VALUE)
  public CompletableFuture<String> newBasket(@RequestBody CreateBasketRequest requestBody, HttpServletRequest request) {

    StringBuilder sb = logMessageBuilder(request, "CREATE NEW BASKET");
    LOGGER.info(sb.toString());
    return commandGateway.send(new CreateBasket(UUID.randomUUID().toString(), requestBody.getType()));
  }


  @PutMapping(path = "/api/baskets/{basketId}/things", consumes = MediaType.APPLICATION_JSON_VALUE)
  public CompletableFuture<Void> addThing(@PathVariable String basketId, @RequestBody AddThingRequest requestBody,
      HttpServletRequest request) {

    StringBuilder sb = logMessageBuilder(request, "ADD THING TO BASKET");
    LOGGER.info(sb.toString());
    return commandGateway
        .send(new AddThingToBasket(basketId, new Thing(requestBody.getName(), requestBody.getDescription())));
  }


  @GetMapping(path = "/api/baskets", produces = MediaType.APPLICATION_JSON_VALUE)
  public CompletableFuture<List<BasketView>> typeList(@RequestParam("type") String basketType) {
    return queryGateway.query(new BasketViewsByTypeContainsQuery(basketType),
        ResponseTypes.multipleInstancesOf(BasketView.class));
  }


  @GetMapping(path = "/api/baskets/updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<BasketView> typeListUpdates(@RequestParam("type") String basketType, HttpServletRequest request) {

    StringBuilder sb = logMessageBuilder(request, String.format("SUBSCRIBE TO TYPED BASKETVIEW STREAM", basketType));
    LOGGER.info(sb.toString());
    SubscriptionQueryResult<List<BasketView>, BasketView> result =
        QueryUtils.subscribeToBasketViewsByType(queryGateway, basketType);
    return result.updates();
  }


  @GetMapping(path = "/api/baskets/{basketId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public CompletableFuture<BasketView> basket(@PathVariable String basketId) {
    return queryGateway.query(new BasketViewByIdQuery(basketId), ResponseTypes.instanceOf(BasketView.class));
  }


  @GetMapping(path = "/api/baskets/{basketId}/updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<BasketView> basketUpdates(@PathVariable String basketId, HttpServletRequest request) {

    StringBuilder sb = logMessageBuilder(request, "SUBSCRIBE TO BASKETVIEW UPDATES STREAM");
    LOGGER.info(sb.toString());
    return QueryUtils.subscribeToBasketViewById(queryGateway, basketId).updates();
  }


  private StringBuilder logMessageBuilder(HttpServletRequest request, String title) {

    StringBuilder sb = new StringBuilder(String.format("\n************************************\n%s\nREQUEST\t==> %s%s",
        title, request.getServletPath(), request.getQueryString() == null ? "" : "?" + request.getQueryString()));

    sb.append("\nHEADERS.....");
    for (Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements();) {
      String name = names.nextElement();
      sb.append(String.format("\n==> %s:\t\t[%s]", name, request.getHeader(name)));
    }

    sb.append("\nPARAMS.....");
    for (Enumeration<String> names = request.getParameterNames(); names.hasMoreElements();) {
      String name = names.nextElement();
      LOGGER.info(String.format("\n==> %s:\t\t[%s]", name, request.getParameter(name)));
    }

    return sb;
  }
}
