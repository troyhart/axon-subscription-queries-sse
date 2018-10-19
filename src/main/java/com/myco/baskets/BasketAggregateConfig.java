package com.myco.baskets;

import org.axonframework.commandhandling.model.Repository;
import org.axonframework.common.caching.Cache;
import org.axonframework.eventsourcing.AggregateFactory;
import org.axonframework.eventsourcing.CachingEventSourcingRepository;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.spring.eventsourcing.SpringPrototypeAggregateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BasketAggregateConfig {

  @Bean
  @Scope("prototype")
  public BasketAggregate newBasketAggregate() {
    return new BasketAggregate();
  }

  @Bean
  public AggregateFactory<BasketAggregate> basketAggregateFactory() {
    SpringPrototypeAggregateFactory<BasketAggregate> aggregateFactory = new SpringPrototypeAggregateFactory<>();
    aggregateFactory.setPrototypeBeanName("newBasketAggregate");
    return aggregateFactory;
  }

  @Bean
  public Repository<BasketAggregate> basketAggregateRepository(EventStore eventStore, Cache cache,
      Snapshotter snapshotter) {
    CachingEventSourcingRepository<BasketAggregate> repository = new CachingEventSourcingRepository<>(
        basketAggregateFactory(), eventStore, cache, new EventCountSnapshotTriggerDefinition(snapshotter, 50));
    return repository;
  }
}
