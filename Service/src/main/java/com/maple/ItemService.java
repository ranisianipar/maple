package com.maple;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.maple.Exception.DataConstraintException;
import com.maple.Exception.MapleException;
import com.maple.Exception.NotFoundException;
import com.maple.Helper.SimpleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.maple.Helper.SimpleUtils.*;


@Service
public class ItemService {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    AssignmentService assignmentService;

    @Autowired
    CounterService counterService;

    @Autowired
    SimpleUtils simpleUtils;

    final private String ITEM = "ITEM";

    public List<Item> getAll(String search, Pageable pageRequest) {
        if (search != null)
            return itemRepository.findByNameLike(search, pageRequest).getContent();

        return itemRepository.findAll(pageRequest).getContent();
    }

    public Item get(String id) throws NotFoundException {
        Optional<Item> item = itemRepository.findById(id);
        if (!item.isPresent()) throw new NotFoundException(ITEM);
        return item.get();
    }

    public long getTotalObject() {return SimpleUtils.getTotalObject(itemRepository);}
    public long getTotalPage(long size) {return getTotalPages(size, getTotalObject());}


    public Item create(Item item, MultipartFile file, String token) throws IOException,MapleException {
        item.setCreatedBy(getCurrentUserId(token));
        validate(item, true);
        item.setItemSku(counterService.getNextItem());
        item.setCreatedDate(new Date());
        if (file != null)
            item.setImagePath(storeFile(Constant.FOLDER_PATH_ITEM, file, item.getItemSku()));
        return itemRepository.save(item);
    }

    public Item update(String id, Item item, MultipartFile file, String token) throws MapleException, IOException{

        item.setUpdatedBy(getCurrentUserId(token));
        Optional<Item> itemObject = itemRepository.findById(id);
        if (!itemObject.isPresent()) throw new NotFoundException(ITEM);
        Item updatedItem = itemObject.get();


        //kalo dia berniat ngapus gambar, brarti dia harus imagePathnya di null in dari request
        if (item.getImagePath() == null) {
            deleteFile(item.getImagePath());
            updatedItem.setImagePath(null);
        }
        // user replace/add picture
        if (file != null) {
            updatedItem.setImagePath(storeFile(Constant.FOLDER_PATH_ITEM, file, item.getItemSku()));
        }

        updatedItem.setName(item.getName());
        updatedItem.update(item.getUpdatedBy());
        updatedItem.setDescription(item.getDescription());
        updatedItem.setPrice(item.getPrice());
        updatedItem.setQuantity(item.getQuantity());
        validate(updatedItem, false);
        return itemRepository.save(updatedItem);
    }
    public void updateManyItemQuantity(List<Item> items) {
        itemRepository.saveAll(items);
    }

    public void deleteMany(List<String> listOfId) throws MapleException {

        try {
            // delete image
            Optional<Item> itemObject;
            for (String id : listOfId) {
                itemObject = itemRepository.findById(id);
                if (itemObject.isPresent())
                deleteFile(itemObject.get().getImagePath());
            }

            itemRepository.deleteByItemSkuIn(listOfId);
            assignmentService.updateByItem(listOfId);
        } catch (Exception e) {
            throw new MapleException(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    public byte[] generatePdf(String id) throws Exception{
        Item item = itemRepository.findById(id).get();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(item.getItemSku()+".pdf"));
        document.addTitle(item.getName());

        //input item value
        document.open();
        if (item.getImagePath() != null) {
            Image img = Image.getInstance(item.getImagePath());
            img.scaleAbsolute(200,200);
            document.add(img);
        }

        document.add(new Paragraph("\n\n"));
        PdfPTable table = new PdfPTable(2);

        //header
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setPhrase(new Phrase("Attribute"));
        table.addCell(header);
        header.setPhrase(new Phrase("Value"));
        table.addCell(header);

        // attribute; value
        table.addCell("Item SKU"); table.addCell(item.getItemSku());
        table.addCell("Description"); table.addCell(item.getDescription());
        table.addCell("Price"); table.addCell(item.getPrice().toString());
        table.addCell("Quantity"); table.addCell(item.getQuantity().toString());
        table.addCell("Created by"); table.addCell(item.getCreatedBy());
        table.addCell("Created at"); table.addCell(item.getCreatedDate().toString());

        document.add(table);
        document.close();

        return Files.readAllBytes(Paths.get(item.getItemSku()+".pdf"));

    }

    // non functional

    public boolean isExist(String itemSku) {
        return itemRepository.findById(itemSku).isPresent();
    }

    public void returnItem(String itemSku, int quantity) {
        Item item = itemRepository.findById(itemSku).get();
        item.setQuantity(item.getQuantity()+quantity);
        itemRepository.save(item);
    }

    private void validate(Item item, boolean create) throws DataConstraintException{
        ArrayList errorMessage = new ArrayList();
        String name_msg = "Name have already exist";
        String null_warning = "can't be null";

        // check important attributes not null
        if (item.getName() == null) errorMessage.add("name "+null_warning);
        if (item.getQuantity() == null) errorMessage.add("quantity "+null_warning);
        if (item.getPrice() == null) errorMessage.add("price "+null_warning);

        //name uniqueness
        if (itemRepository.findByName(item.getName()) != null) {
            if (create) errorMessage.add(name_msg);
            else if (!itemRepository.findByName(item.getName()).getItemSku().equals(item.getItemSku())) {
                errorMessage.add(name_msg);
            }
        }
        if (!errorMessage.isEmpty()) throw new DataConstraintException(errorMessage.toString());
    }
}
