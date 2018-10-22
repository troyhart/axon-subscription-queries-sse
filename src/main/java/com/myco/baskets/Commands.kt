package com.myco.baskets

import org.axonframework.commandhandling.TargetAggregateIdentifier


data class CreateBasket(
        @TargetAggregateIdentifier
        val basketId: String,
        val type: String
) {
  fun validate() {
    assertRequiredString(basketId, "basketId");
    assertRequiredString(type, "type");
  }
}


data class AddThingToBasket(
        @TargetAggregateIdentifier
        val basketId: String,
        val thing: Thing
) {
  fun validate() {
    assertRequiredString(basketId, "basketId");
    thing.validate();
  }
}
