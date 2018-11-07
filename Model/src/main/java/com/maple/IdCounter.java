package com.maple;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "IdCounter")
@Getter @Setter
public class IdCounter {
    @Id
    int _id;
    public long employeeId;
    public long itemId;

    public IdCounter() {
        this._id = 1;
    }


}
