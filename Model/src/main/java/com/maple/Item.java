package com.maple;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "items")
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

    public Item(String name, int price, int quantity, String imagePath, String desc, String userName){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        description = desc;
        createdDate = new Date();
        this.imagePath = imagePath;
        createdBy = userName;
    }

    @Override
    public String toString(){
        return String.format("Item id:'%s', name:'%s', quantity:'%d', price:'%d' "
                ,itemSku, name, quantity, price);
    }

}
