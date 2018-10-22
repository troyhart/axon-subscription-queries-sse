package com.myco.baskets;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.eventsourcing.SequenceNumber;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Component;


@Component
public class BasketViewProjector {

  private static final Logger LOGGER = LoggerFactory.getLogger(BasketViewProjector.class);

  private BasketViewRepository repository;
  private QueryUpdateEmitter queryUpdateEmitter;


  @Autowired
  public BasketViewProjector(BasketViewRepository repository, QueryUpdateEmitter queryUpdateEmitter) {
    this.repository = repository;
    this.queryUpdateEmitter = queryUpdateEmitter;
  }


  @QueryHandler
  public BasketView handle(BasketViewByIdQuery query) {
    return repository.findById(query.getId()).get();
  }


  @QueryHandler
  public List<BasketView> handle(BasketViewsByTypeContainsQuery query) {

    Example<BasketView> example = Example.of(new BasketView().setType(query.getTypeContains()),
        ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
    return StreamSupport.stream(repository.findAll(example).spliterator(), false).collect(Collectors.toList());
  }


  @EventHandler
  public void on(BasketCreated event, @SequenceNumber long aggregateVersion, @Timestamp Instant occurrenceInstant) {
    BasketView view = new BasketView(event.getBasketId(), event.getType());
    save(view, occurrenceInstant, aggregateVersion);
    queryUpdateEmitter.emit(BasketViewsByTypeContainsQuery.class,
        query -> view.getType().toUpperCase().contains(query.getTypeContains().toUpperCase()), view);
  }


  @EventHandler
  public void on(ThingAddedToBasket event, @SequenceNumber long aggregateVersion,
      @Timestamp Instant occurrenceInstant) {
    save(repository.findById(event.getBasketId()).get().addThing(event.getThing()), occurrenceInstant,
        aggregateVersion);
  }


  private void save(BasketView basketView, Instant occurrenceInstant, long aggregateVersion) {

    basketView.setLastModified(occurrenceInstant).setAggregateVersion(aggregateVersion);

    repository.save(basketView);

    LOGGER.trace("emitting update: {}", basketView);

    // emit the updated basket view to all subscribers
    queryUpdateEmitter.emit(BasketViewByIdQuery.class, query -> query.getId().equals(basketView.getId()), basketView);
  }
}
