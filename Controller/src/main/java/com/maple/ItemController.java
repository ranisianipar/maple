package com.maple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost")
@RestController
public class ItemController extends InvalidItemAttributeValue{

    @Autowired
    private ItemService itemService;

    @GetMapping("/item")
    public BaseResponse<List<Item>> getAllItems() {
        BaseResponse br = new BaseResponse();
        br.succeedResponse();
        br.setValue(itemService.getAll());
        return br;
    }

    @GetMapping("/item/{id}")
    public BaseResponse getItem (@PathVariable String id) {
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
        try {
            br.setValue(itemService.create(item));
            br.succeedResponse();
        } catch (DataConstraintException e) {
            br.setErrorCode(HttpStatus.BAD_REQUEST);
            br.setCode(HttpStatus.BAD_REQUEST);
            br.setErrorMessage(e.getMessage());
        } finally {
            return br;
        }
    }

    @PostMapping("/item/{id}")
    public BaseResponse updateItem(@PathVariable String id, @Valid @RequestBody Item item) {
        BaseResponse<Item> baseResponse = new BaseResponse<>();
        try {
            baseResponse.setValue(itemService.update(id, item));
            baseResponse.succeedResponse();
        } catch (NotFoundException e) {
            baseResponse.errorResponse();
            baseResponse.setErrorMessage(e.getMessage());
            baseResponse.setErrorCode(e.getCode());
        } catch (DataConstraintException e) {
            baseResponse.errorResponse();
            baseResponse.setErrorMessage(e.getMessage());
            baseResponse.setErrorCode(e.getCode());
        } finally {
            return baseResponse;
        }
    }

    @DeleteMapping("/item/{id}")
    public BaseResponse deleteItem(@PathVariable String id) {
        BaseResponse br = new BaseResponse();
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
    public BaseResponse deleteItems() {
        BaseResponse br = new BaseResponse("All items have been removed");
        itemService.deleteAll();
        br.succeedResponse();
        return br;
    }
}
