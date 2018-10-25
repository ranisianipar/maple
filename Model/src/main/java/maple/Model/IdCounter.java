package maple.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "maple.Model.IdCounter")
public class IdCounter {
    @Id
    String _id;

    public String employeeId;
    public String itemId;

    public IdCounter(String employeeId, String itemId) {
        this.employeeId = employeeId;
        this.itemId = itemId;
    }
}
