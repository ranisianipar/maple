package com.maple;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maple.Exception.MapleException;
import com.maple.validation.InvalidItemAttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
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
        br.setTotalRecords(itemService.getTotalItem());
        br.setTotalPages(itemService.getTotalPage(pageRequest.getPageSize()));
        br.setValue(itemService.getAll(pageRequest, search));
        br.setPaging(pageRequest);
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
    public BaseResponse createItem(
            @RequestParam(value = "file",required = false) MultipartFile file,
            @Valid @RequestParam(value = "data") String item) {
        BaseResponse<Item> br = new BaseResponse<>();
        try {
            Item itemMapped = new ObjectMapper().readValue(item, Item.class);
            br.setValue(itemService.create(itemMapped, file));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        } catch (IOException e) {
            return responseMapping(br, new MapleException(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping("/item/{id}")
    public BaseResponse updateItem(
            @PathVariable String id,
            @RequestParam(value = "file",required = false) MultipartFile file,
            @Valid @RequestParam(value = "data") String item) {
        BaseResponse<Item> br = new BaseResponse<>();
        try {
            Item itemMapped = new ObjectMapper().readValue(item, Item.class);
            // upload gambar
            br.setValue(itemService.update(id, itemMapped));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br,e);
        } catch (IOException e) {
            return responseMapping(br, new MapleException(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @DeleteMapping("/item")
    public BaseResponse deleteItem(@RequestBody DeleteRequest deleteRequest) {
        BaseResponse br = new BaseResponse();
        try {
            itemService.deleteMany(deleteRequest.getIds());
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
