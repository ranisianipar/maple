package com.maple;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maple.Exception.MapleException;
import com.maple.validation.InvalidItemAttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = Constant.LINK_ORIGIN)
@RequestMapping(value= Constant.LINK_ITEM_PREFIX)
@RestController
public class ItemController extends InvalidItemAttributeValue {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public BaseResponse<List<Item>> getAllItems(
            @RequestParam (value = "page", defaultValue = "0") int page,
            @RequestParam (value = "size", defaultValue = "10") int size,
            @RequestParam (value = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam (value = "search", required = false) String search) {
        BaseResponse br = new BaseResponse();
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        br.setTotalRecords(itemService.getTotalItem());
        br.setTotalPages(itemService.getTotalPage(pageRequest.getPageSize()));
        br.setValue(itemService.getAll(search, pageRequest));
        br.setPaging(pageRequest);
        return responseMapping(br, null);
    }

    @GetMapping(Constant.LINK_ID_PARAM)
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

    @PostMapping
    public BaseResponse createItem(
            @RequestParam(value = "file",required = false) MultipartFile file,
            @RequestParam(value = "data") String item) {
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

    @PostMapping(Constant.LINK_ID_PARAM)
    public BaseResponse updateItem(
            @PathVariable String id,
            @RequestParam(value = "file",required = false) MultipartFile file,
            @Valid @RequestParam(value = "data") String item) {
        BaseResponse<Item> br = new BaseResponse<>();
        try {
            Item itemMapped = new ObjectMapper().readValue(item, Item.class);
            br.setValue(itemService.update(id, itemMapped, file));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br,e);
        } catch (IOException e) {
            return responseMapping(br, new MapleException(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @DeleteMapping
    public BaseResponse deleteItem(@RequestBody DeleteRequest deleteRequest) {
        BaseResponse br = new BaseResponse();
        try {
            itemService.deleteMany(deleteRequest.getIds());
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }

    @GetMapping(value=Constant.LINK_ITEM_DOWNLOAD, produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] generate(@PathVariable String id) {
        try {
            return itemService.generatePdf(id);
        } catch (Exception e) {
            return new byte[0];
        }

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
