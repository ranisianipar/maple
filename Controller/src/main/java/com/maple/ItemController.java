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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.maple.Helper.SimpleUtils.getTokenFromRequest;
import static com.maple.Helper.SimpleUtils.onlyAuthorizedUser;
import static com.maple.Helper.SimpleUtils.responseMapping;

@CrossOrigin(origins = Constant.LINK_ORIGIN)
@RequestMapping(value= Constant.LINK_ITEM_PREFIX)
@RestController
public class ItemController extends InvalidItemAttributeValue {

    @Autowired
    private ItemService itemService;

    @Autowired
    private AdminService adminService;

    @GetMapping
    public BaseResponse<List<Item>> getAll(
            @RequestParam (value = "page", defaultValue = "0") int page,
            @RequestParam (value = "size", defaultValue = "10") int size,
            @RequestParam (value = "sortBy", defaultValue = "createdDate") String sortBy,
            @RequestParam (value = "search", required = false) String search,
            HttpServletRequest request) {
        BaseResponse br = new BaseResponse();

        try {
            onlyAuthorizedUser("get all item", getTokenFromRequest(request));
        }   catch (MapleException m) {
            responseMapping(br, m);
        }
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        br.setPaging(pageRequest);
        br.setTotalPages(itemService.getTotalPage(pageRequest.getPageSize()));
        br.setTotalRecords(itemService.getTotalObject());
        br.setValue(itemService.getAll(search, pageRequest));
        return responseMapping(br, null);
    }

    @GetMapping(Constant.LINK_ID_PARAM)
    public BaseResponse get(@PathVariable String id, HttpServletRequest request) {
        BaseResponse br = new BaseResponse();
        try {
            onlyAuthorizedUser("get item", getTokenFromRequest(request));
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
            HttpServletRequest request) {
        BaseResponse<Item> br = new BaseResponse<>();
        try {
            adminService.onlyAdmin("create item", getTokenFromRequest(request));
            br.setValue(itemService.create(new ObjectMapper().readValue(item, Item.class), file));
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
            HttpServletRequest request) {
        BaseResponse<Item> br = new BaseResponse<>();
        try {
            adminService.onlyAdmin("update method", getTokenFromRequest(request));
            br.setValue(itemService.update(id, new ObjectMapper().readValue(item, Item.class), file));
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br,e);
        } catch (IOException e) {
            return responseMapping(br, new MapleException(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @DeleteMapping
    public BaseResponse delete(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        BaseResponse br = new BaseResponse();
        try {
            adminService.onlyAdmin("delete item", getTokenFromRequest(request));
            itemService.deleteMany(deleteRequest.getIds());
            return responseMapping(br, null);
        } catch (MapleException e) {
            return responseMapping(br, e);
        }
    }

    @GetMapping(value=Constant.LINK_ITEM_DOWNLOAD, produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] generatePdf(@PathVariable String id, HttpServletRequest request) {
        try {
            onlyAuthorizedUser("generate PDF", getTokenFromRequest(request));
            return itemService.generatePdf(id);
        } catch (Exception e) {
            return new byte[0];
        }

    }
}
