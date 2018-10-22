package com.myco.baskets;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface BasketViewRepository
    extends PagingAndSortingRepository<BasketView, String>, QueryByExampleExecutor<BasketView> {
}
