package com.maple;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "items")
@Getter @Setter
public class Item {

    @Id
    public String itemSku;

    public String name;
    public int price;
    public int quantity;
    public String description;
    public Date createdDate;
    public Date updatedDate;
    public String imagePath;
    public String createdBy;
    public String updatedBy;

    public Item(){}
}
