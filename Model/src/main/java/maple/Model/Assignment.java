package maple.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Date;

@Document(collection = "assignments")
public class Assignment {
    @Id
    public String assignmentId;
    public String employeeId;
    public String itemSku;

    public String status;
    public int quantity;
    public Date createdDate;
    public Date updatedDate;
    public String createdBy;
    public String updatedBy;
    public String note;

    public Assignment(String id, String emp, String item, String status, int quan, String createdBy){

    }

    public void setNote(String note) {
        this.note = note;
    }
}
