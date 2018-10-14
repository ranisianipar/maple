package future.maple.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="admins")
public class Admin {
    @Id
    String username;

    String password;

    public Admin(String usern, String pass){
        username = usern;
        password = pass;
    }
}
