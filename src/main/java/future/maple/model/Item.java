package future.maple.model;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class Item {

    @Id
    public String itemId;

    public String name;
    public int price;
    public int quantity;
    public String description;
    public Date createdDate;
    public Date updatedDate;

//    image nya gimana?

    public Item(String id, String name, int price, int quantity, String desc){
        itemId = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        description = desc;
        createdDate = new Date();
    }

    public Item(String id, String name, int price, int quantity){
        itemId = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        createdDate = new Date();
    }

    @Override
    public String toString(){
        return String.format("Item id:'%s', name:'%s', quantity:'%d', price:'%d' "
                ,itemId, name, quantity, price);
    }

}
