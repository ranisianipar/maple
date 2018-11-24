package com.maple;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Document(collection = "items")
@Data
public class Item {

    @Id
    private String itemSku;

    @NotBlank(message = "Name can't be blank")
    private String name;

    @NotBlank(message = "Price can't be blank")
    private Integer price;

    @NotBlank(message = "Quantity can't be blank")
    private Integer quantity;
    private String description;
    private Date createdDate;
    private Date updatedDate;
    private String imagePath;
    private String createdBy;
    private String updatedBy;

    public Item (){ createdDate = new Date(); }

    // perlu dipindah ke service
    public void update (String id) {
        updatedBy = id;
        updatedDate = new Date();
    }
}
