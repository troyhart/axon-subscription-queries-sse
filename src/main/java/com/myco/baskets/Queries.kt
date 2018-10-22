package com.myco.baskets


data class BasketViewByIdQuery(
        val id: String
)


data class BasketViewsByTypeContainsQuery(
        val typeContains: String
)
