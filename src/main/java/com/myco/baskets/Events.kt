package com.myco.baskets


data class BasketCreated(
        val basketId: String,
        val type: String
)


data class ThingAddedToBasket(
        val basketId: String,
        val thing: Thing
)
