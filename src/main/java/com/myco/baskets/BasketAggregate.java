package com.myco.baskets;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import java.util.HashSet;
import java.util.Set;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;


@Aggregate
public class BasketAggregate {

  @AggregateIdentifier(routingKey = "basketId")
  private String basketId;

  private Set<Thing> things;


  public BasketAggregate() {
  }


  @CommandHandler
  public BasketAggregate(CreateBasket command) {
    Assert.notNull(command, "Null command");
    command.validate();
    apply(new BasketCreated(command.getBasketId(), command.getType()));
  }


  @CommandHandler
  public void handle(AddThingToBasket command) {
    Assert.notNull(command, "Null command");
    command.validate();
    Assert.isTrue(!things.contains(command.getThing()), "Already in the basket: " + command.getThing());
    apply(new ThingAddedToBasket(command.getBasketId(), command.getThing()));
  }


  @EventSourcingHandler
  public void on(BasketCreated event) {
    this.basketId = event.getBasketId();
    things = new HashSet<>();
  }


  @EventSourcingHandler
  public void on(ThingAddedToBasket event) {
    things.add(event.getThing());
  }
}
