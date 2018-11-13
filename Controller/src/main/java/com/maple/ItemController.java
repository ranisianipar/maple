package com.maple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost")
@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/item")
    public BaseResponse<List<Item>> getAllItems() {
        return new BaseResponse<List<Item>>(itemService.getAll());
    }

    @GetMapping("/item/{id}")
    public BaseResponse getItem( @PathVariable String id) {
        BaseResponse br = new BaseResponse();
        Item item;
        try {
            item = itemService.get(id);
            br.setValue(item);
            br.succeedResponse();
        } catch (NotFoundException e) {
            br.errorResponse();
            br.setErrorMessage(e.getMessage());
            br.setErrorCode(e.getCode());
        } finally {
            return br;
        }
    }

    @PostMapping("/item")
    public BaseResponse createItem(@Valid @RequestBody Item item) {
        BaseResponse<Item> br = new BaseResponse<>();
        br.setValue(itemService.create(item));
        return br;
    }

    @PostMapping("/item/{id}")
    public BaseResponse updateItem( @PathVariable String id) {

        return null;
    }

    @DeleteMapping("/item/{id}")
    public BaseResponse deleteItem (@PathVariable String id) {
        BaseResponse br = new BaseResponse("All items have been removed");
        try {
            itemService.delete(id);
            br.succeedResponse();
        } catch (NotFoundException e) {
            br.errorResponse();
            br.setErrorCode(e.getCode());
            br.setErrorMessage(e.getMessage());
        } finally {
            return br;
        }
    }

    @DeleteMapping("/items")
    public BaseResponse deleteItems () {
        BaseResponse br = new BaseResponse("All items have been removed");
        itemService.deleteAll();
        return br;
    }
}