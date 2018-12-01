package com.maple;

import com.maple.Exception.DataConstraintException;
import com.maple.Exception.MapleException;
import com.maple.Exception.NotFoundException;
import com.maple.validation.InvalidItemAttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost")
@RestController
public class ItemController extends InvalidItemAttributeValue {

    @Autowired
    private ItemService itemService;

    @GetMapping("/item")
    public BaseResponse<List<Item>> getAllItems(
            @RequestParam (value = "page", defaultValue = "0") int page,
            @RequestParam (value = "size", defaultValue = "10") int size,
            @RequestParam (value = "sortBy", defaultValue = "itemSku") String sortBy,
            @RequestParam (value = "search", required = false) String search
    ) {
        BaseResponse br = new BaseResponse();
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        br.setValue(itemService.getAll(pageRequest, search));
        br.setPage(pageRequest);
        return responseMapping(br, null);
    }

    @GetMapping("/item/{id}")
    public BaseResponse getItem (@PathVariable String id) {
        BaseResponse br = new BaseResponse();
        Item item;
        try {
            item = itemService.get(id);
            br.setValue(item);
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }

    @PostMapping("/item")
    public BaseResponse createItem(@Valid @RequestBody Item item) {
        BaseResponse<Item> br = new BaseResponse<>();
        try {
            br.setValue(itemService.create(item));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }

    @PostMapping("/item/{id}")
    public BaseResponse updateItem(@PathVariable String id, @Valid @RequestBody Item item) {
        BaseResponse<Item> br = new BaseResponse<>();
        try {
            br.setValue(itemService.update(id, item));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br,e);
        }
    }

    @DeleteMapping("/item/{id}")
    public BaseResponse deleteItem(@PathVariable String id) {
        BaseResponse br = new BaseResponse();
        try {
            itemService.delete(id);
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }

    @DeleteMapping("/items")
    public BaseResponse deleteItems() {
        itemService.deleteAll();
        return responseMapping(new BaseResponse("All items have been removed"), null);
    }

    //HELPER METHOD
    private BaseResponse responseMapping(BaseResponse br, MapleException e){
        if (e == null) {
            br.setSuccess(true);
            br.setCode(HttpStatus.OK);
            return br;
        }
        br.setCode(HttpStatus.BAD_REQUEST);
        br.setErrorCode(e.getCode());
        br.setErrorMessage(e.getMessage());
        return br;
    }
}
