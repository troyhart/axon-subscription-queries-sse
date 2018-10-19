package com.myco.baskets

import org.axonframework.commandhandling.TargetAggregateIdentifier
import org.springframework.util.Assert

data class CreateBasket(
        @TargetAggregateIdentifier
        val basketId: String,
        val type: String
) {
  fun validate() {
    validateRequiredString(basketId, "basketId");
    validateRequiredString(type, "type");
  }
}

data class AddThingToBasket(
        @TargetAggregateIdentifier
        val basketId: String,
        val thing: Thing
) {
  fun validate() {
    validateRequiredString(basketId, "basketId");
    thing.validate();
  }
}
