package com.myco.baskets;

import java.util.List;

import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;


public class QueryUtils {

  public static SubscriptionQueryResult<BasketView, BasketView> subscribeToBasketViewById(QueryGateway queryGateway,
      String basketId) {
    // @formatter:off
    return queryGateway.subscriptionQuery(
        new BasketViewByIdQuery(basketId),
        ResponseTypes.instanceOf(BasketView.class), 
        ResponseTypes.instanceOf(BasketView.class));
    // @formatter:on
  }


  public static SubscriptionQueryResult<List<BasketView>, BasketView> subscribeToBasketViewsByType(
      QueryGateway queryGateway, String basketType) {
    // @formatter:off
    return queryGateway.subscriptionQuery(
        new BasketViewsByTypeContainsQuery(basketType),
        ResponseTypes.multipleInstancesOf(BasketView.class), 
        ResponseTypes.instanceOf(BasketView.class));
    // @formatter:on
  }
}
