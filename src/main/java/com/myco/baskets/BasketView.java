package com.myco.baskets;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;


@Document
public class BasketView {

  @Id
  private String id;

  private String type;

  private LinkedHashSet<Thing> things;

  private Instant lastModified;

  private long aggregateVersion;


  public BasketView() {
  }


  public BasketView(String id, String type) {
    Assert.hasText(id, "null/blank identifier");
    Assert.hasText(type, "null/blank type");
    this.id = id;
    this.type = type;
  }


  public String getId() {
    return id;
  }


  public String getType() {
    return type;
  }


  public Set<Thing> getThings() {
    return things;
  }


  public boolean hasThing(Thing thing) {
    return things != null && things.contains(thing);
  }


  public Instant getLastModified() {
    return lastModified;
  }


  public long getAggregateVersion() {
    return aggregateVersion;
  }


  BasketView setType(String type) {
    this.type = type;
    return this;
  }


  BasketView setLastModified(Instant occurrenceInstant) {
    this.lastModified = occurrenceInstant;
    return this;
  }


  BasketView setAggregateVersion(long lastVersion) {
    this.aggregateVersion = lastVersion;
    return this;
  }


  BasketView addThing(Thing thing) {
    Assert.notNull(thing, "null thing");
    Assert.isTrue(things == null || !things.contains(thing), "Already have the given thing: " + thing);
    if (things == null) {
      things = new LinkedHashSet<>();
    }
    things.add(thing);
    return this;
  }
}
