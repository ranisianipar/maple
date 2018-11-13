package com.maple;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Document(collection = "items")
@Getter @Setter
public class Item {

    @Id
    public String itemSku;

    @NotBlank(message = "Name can't be blank")
    public String name;

    @NotBlank(message = "Price can't be blank")
    public int price;

    @NotBlank(message = "Quantity can't be blank")
    public int quantity;
    public String description;
    public Date createdDate;
    public Date updatedDate;
    public String imagePath;
    public String createdBy;
    public String updatedBy;

    public Item(){}
}
