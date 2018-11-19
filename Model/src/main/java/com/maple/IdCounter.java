package com.maple;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "IdCounter")
@Data
public class IdCounter {
    @Id
    int _id;
    private long employeeId;
    private long itemId;

    public IdCounter() {
        this._id = 1;
    }


}
