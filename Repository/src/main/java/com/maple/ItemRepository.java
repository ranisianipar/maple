package com.maple;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<Item, String>{

    public Page<Item> findByNameLike(String name, Pageable page);
    public Item findByName(String name);
    public Page<Item> findAll(String name, Pageable page);
    public void deleteByItemSkuIn(List<String> ids);
}
