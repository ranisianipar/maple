package com.maple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ItemService {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CounterService counterService;

    final String ITEM = "Item";

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public Item get(String id) throws NotFoundException{
        Optional<Item> item = itemRepository.findById(id);
        if (!item.isPresent()) throw new NotFoundException(ITEM);
        return item.get();
    }

    public Item create(Item item) {
        //validate(item, true)
        item.setItemSku(counterService.getNextItem());
        return itemRepository.save(item);
    }

    public Item update(String id, Item i) throws NotFoundException{
        Optional<Item> itemObj = itemRepository.findById(id);
        if (!itemObj.isPresent()) throw new NotFoundException(ITEM);

        Item item = itemObj.get();
        item.setImagePath(i.getImagePath());
        item.setName(i.getName());
        item.setUpdatedDate(new Date());
        item.setUpdatedBy(i.getUpdatedBy());
        item.setDescription(i.getDescription());
        item.setPrice(i.getPrice());
        item.setQuantity(i.getQuantity());
        return itemRepository.save(item);
    }

    public void delete(String id) throws NotFoundException {
        Optional<Item> item = itemRepository.findById(id);
        if (!item.isPresent()) throw new NotFoundException(ITEM);

        itemRepository.delete(item.get());
    }

    public void deleteAll() { itemRepository.deleteAll(); }

}
