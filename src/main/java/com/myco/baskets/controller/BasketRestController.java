package com.myco.baskets.controller;

import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
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
  public CompletableFuture<String> newBasket(@RequestBody CreateBasketRequest requestBody, HttpServletRequest request) {
    logRequestInfo(request, "#");
    return commandGateway.send(new CreateBasket(UUID.randomUUID().toString(), requestBody.getType()));
  }

  @PutMapping(path = "/api/baskets/{basketId}/things", consumes = MediaType.APPLICATION_JSON_VALUE)
  public CompletableFuture<Void> addThing(@PathVariable String basketId, @RequestBody AddThingRequest requestBody,
      HttpServletRequest request) {
    logRequestInfo(request, "@");
    return commandGateway
        .send(new AddThingToBasket(basketId, new Thing(requestBody.getName(), requestBody.getDescription())));
  }

  @GetMapping(path = "/api/baskets", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<List<BasketView>> subscribeToBasketList(@RequestParam("type") String basketType, HttpServletRequest request) {
    logRequestInfo(request, "!");
    return QueryUtils.subscribeToBasketsViewByType(queryGateway, basketType).updates();
  }

  @GetMapping(path = "/api/baskets/{basketId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<BasketView> subscribe(@PathVariable String basketId, HttpServletRequest request) {
    logRequestInfo(request, "!");
    return QueryUtils.subscribeToBasketViewById(queryGateway, basketId).updates();
  }

  private void logRequestInfo(HttpServletRequest request, String marker) {
    System.out.println(String.format(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> REQUEST: %1$s%2$s", request.getServletPath(),
        request.getQueryString() == null ? "" : "?" + request.getQueryString()));
    for (Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements();) {
      String name = names.nextElement();
      System.out.println(String.format("%1$s%1$s%1$s%1$s%1$s%1$s%1$s%1$s\tHEADER ==> %2$s: [%3$s]", marker, name,
          request.getHeader(name)));
    }
    for (Enumeration<String> names = request.getParameterNames(); names.hasMoreElements();) {
      String name = names.nextElement();
      System.out.println(String.format("%1$s%1$s%1$s%1$s%1$s%1$s%1$s%1$s\tPARAM ==> %2$s: [%3$s]", marker, name,
          request.getParameter(name)));
    }
  }
}
