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

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.maple.Helper.SimpleUtils.onlyAdmin;
import static com.maple.Helper.SimpleUtils.responseMapping;

@CrossOrigin(origins = Constant.LINK_ORIGIN)
@RequestMapping(value= Constant.LINK_ITEM_PREFIX)
@RestController
public class ItemController extends InvalidItemAttributeValue {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public BaseResponse<List<Item>> getAll(
            @RequestParam (value = "page", defaultValue = "0") int page,
            @RequestParam (value = "size", defaultValue = "10") int size,
            @RequestParam (value = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam (value = "search", required = false) String search) {
        BaseResponse br = new BaseResponse();
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        br.setPaging(pageRequest);
        br.setTotalPages(itemService.getTotalPage(pageRequest.getPageSize()));
        br.setTotalRecords(itemService.getTotalObject());
        br.setValue(itemService.getAll(search, pageRequest));
        return responseMapping(br, null);
    }

    @GetMapping(Constant.LINK_ID_PARAM)
    public BaseResponse get(@PathVariable String id) {
        BaseResponse br = new BaseResponse();
        try {
            br.setValue(itemService.get(id));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }

    @PostMapping
    public BaseResponse create(
            @RequestParam(value = "file",required = false) MultipartFile file,
            @RequestParam(value = "data") String item,
            HttpSession httpSession) {
        BaseResponse<Item> br = new BaseResponse<>();
        try {
            onlyAdmin("create item", httpSession);
            br.setValue(itemService.create(new ObjectMapper().readValue(item, Item.class), file, httpSession));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        } catch (IOException e) {
            return responseMapping(br, new MapleException(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping(Constant.LINK_ID_PARAM)
    public BaseResponse update(
            @PathVariable String id,
            @RequestParam(value = "file",required = false) MultipartFile file,
            @Valid @RequestParam(value = "data") String item,
            HttpSession httpSession) {
        BaseResponse<Item> br = new BaseResponse<>();
        try {
            onlyAdmin("update method",httpSession);
            br.setValue(itemService.update(id, new ObjectMapper().readValue(item, Item.class), file,
                    httpSession));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br,e);
        } catch (IOException e) {
            return responseMapping(br, new MapleException(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @DeleteMapping
    public BaseResponse delete(@RequestBody DeleteRequest deleteRequest, HttpSession httpSession) {
        BaseResponse br = new BaseResponse();
        try {
            onlyAdmin("delete item", httpSession);
            itemService.deleteMany(deleteRequest.getIds());
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }

    @GetMapping(value=Constant.LINK_ITEM_DOWNLOAD, produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] generatePdf(@PathVariable String id) {
        try {
            return itemService.generatePdf(id);
        } catch (Exception e) {
            return new byte[0];
        }

    }
}
