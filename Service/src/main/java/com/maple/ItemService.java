package com.maple;

import com.maple.Exception.DataConstraintException;
import com.maple.Exception.MapleException;
import com.maple.Exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    public Page<Item> getAll(Pageable pageRequest, String search) {
        if (search == null)
            return itemRepository.findAll(pageRequest);
        //kalo dia ngesearch: pake prefix nya
        return itemRepository.findAll(pageRequest);
    }

    public Item get(String id) throws NotFoundException {
        Optional<Item> item = itemRepository.findById(id);
        if (!item.isPresent()) throw new NotFoundException(ITEM);
        return item.get();
    }

    public long getTotalItem() {return SimpleUtils.getTotalObject(itemRepository);}
    public long getTotalPage(long size) {return SimpleUtils.getTotalPages(size, getTotalItem());}

    //createdBy belom -> nunggu login
    public Item create(Item item) throws DataConstraintException {
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

    public void deleteMany(List<String> listOfId) throws MapleException {
        try {
            itemRepository.deleteByItemSkuIn(listOfId);
        } catch (Exception e) {
            throw new MapleException(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    public void deleteAll() { itemRepository.deleteAll(); }

    public void validate(Item item, boolean create) throws DataConstraintException{
        ArrayList errorMessage = new ArrayList();
        String name_msg = "Name have already exist";
        //name uniqueness
        //kalo namanya udah ada
        if (itemRepository.findByName(item.getName()) != null) {
            if (create) errorMessage.add(name_msg);
            else if (!itemRepository.findByName(item.getName()).getItemSku().equals(item.getItemSku())) {
                errorMessage.add(name_msg);
            }
        }
        if (!errorMessage.isEmpty()) throw new DataConstraintException(errorMessage.toString());
    }
}
