package com.maple;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<Item, String>{

    @Query("{ 'name' : ?0 }")
    public Item findByName(String name);

    @Query("{ 'price' : ?0 }")
    public Item findByPrice(int price);

//    @Query("{ 'price' : {$gte: min, $lte:max} }")
//    public List<Item> findByPriceRange(int min, int max);
}
