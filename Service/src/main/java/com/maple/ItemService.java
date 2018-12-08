package com.maple;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.maple.Exception.DataConstraintException;
import com.maple.Exception.MapleException;
import com.maple.Exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ItemService {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    AssignmentService assignmentService;

    @Autowired
    CounterService counterService;

    final private String ITEM = "Item";
    final String UPLOADED_FOLDER = "C:\\Users\\user\\Documents\\future\\maple_uploaded\\Item";

    public List<Item> getAll(Pageable pageRequest, String search) {
        if (search == null)
            return itemRepository.findAll(pageRequest).getContent();
        //kalo dia ngesearch: pake prefix nya
        return itemRepository.findAll(pageRequest).getContent();
    }

    public Item get(String id) throws NotFoundException {
        Optional<Item> item = itemRepository.findById(id);
        if (!item.isPresent()) throw new NotFoundException(ITEM);
        return item.get();
    }

    public long getTotalItem() {return SimpleUtils.getTotalObject(itemRepository);}
    public long getTotalPage(long size) {return SimpleUtils.getTotalPages(size, getTotalItem());}

    //createdBy belom -> nunggu login
    public Item create(Item item, MultipartFile file) throws IOException,DataConstraintException {
        validate(item, true);
        item.setItemSku(counterService.getNextItem());
        item.setCreatedDate(new Date());
        if (file != null)
            item.setImagePath(SimpleUtils.storeFile(UPLOADED_FOLDER, file, item.getItemSku()));
        return itemRepository.save(item);
    }

    //updatedBy belom -> nunggu login
    public Item update(String id, Item item, MultipartFile file) throws NotFoundException, DataConstraintException, IOException{
        Optional<Item> itemObject = itemRepository.findById(id);
        if (!itemObject.isPresent()) throw new NotFoundException(ITEM);
        Item updatedItem = itemObject.get();
        SimpleUtils.deleteFile(item.getImagePath());

        updatedItem.setImagePath(SimpleUtils.storeFile(UPLOADED_FOLDER,file, item.getItemSku()));
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
            assignmentService.deleteByItem(listOfId);
        } catch (Exception e) {
            throw new MapleException(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    public void deleteAll() { itemRepository.deleteAll(); }

    public void generatePdf(String id) throws Exception{
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
