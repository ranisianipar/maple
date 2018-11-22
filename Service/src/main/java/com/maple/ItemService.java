package com.maple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CounterService counterService;

    final private String ITEM = "Item";

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public Item get(String id) throws NotFoundException{
        Optional<Item> item = itemRepository.findById(id);
        if (!item.isPresent()) throw new NotFoundException(ITEM);
        return item.get();
    }

    //createdBy belom -> nunggu login
    public Item create(Item item) throws DataConstraintException{
        validate(item, true);
        item.setItemSku(counterService.getNextItem());
        item.setCreatedDate(new Date());
        return itemRepository.save(item);
    }

    //updatedBy belom -> nunggu login
    public Item update(String id, Item item) throws NotFoundException, DataConstraintException{
        Optional<Item> itemObject = itemRepository.findById(id);
        if (!itemObject.isPresent()) throw new NotFoundException(ITEM);
        Item updatedItem = itemObject.get();
        updatedItem.setImagePath(item.getImagePath());
        updatedItem.setName(item.getName());
        updatedItem.update(item.getUpdatedBy());
        updatedItem.setDescription(item.getDescription());
        updatedItem.setPrice(item.getPrice());
        updatedItem.setQuantity(item.getQuantity());
        validate(updatedItem, false);
        return itemRepository.save(updatedItem);
    }

    public void delete(String id) throws NotFoundException {
        Optional<Item> item = itemRepository.findById(id);
        if (!item.isPresent()) throw new NotFoundException(ITEM);

        itemRepository.delete(item.get());
    }

    public void deleteAll() { itemRepository.deleteAll(); }

    public void validate(Item item, boolean create) throws DataConstraintException{
        ArrayList errorMessage = new ArrayList();
        String name_msg = "Name have already exist";
        String priceQuantity_msg = "Price and Quantity should consist of numbers";
        //name uniqueness
        //kalo namanya udah ada
        if (itemRepository.findByName(item.getName()) != null) {
            if (create) errorMessage.add(name_msg);
            else if (!itemRepository.findByName(item.getName()).getItemSku().equals(item.getItemSku())) {
                errorMessage.add(name_msg);
            }
        }
        //quantity and price should consist of number
        try {
            Integer.parseInt(item.getQuantity());
            Integer.parseInt(item.getPrice());
        } catch (Exception e) {
            errorMessage.add(priceQuantity_msg);
        } finally {
            if (!errorMessage.isEmpty()) throw new DataConstraintException(errorMessage.toString());
        }

    }
}
