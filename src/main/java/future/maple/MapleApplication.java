package future.maple;

import future.maple.repository.EmployeeRepository;
import future.maple.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MapleApplication {

    @Autowired
    private EmployeeRepository empRepo;

    @Autowired
    private ItemRepository itemRepo;

	public static void main(String[] args) {
		SpringApplication.run(MapleApplication.class, args);
	}
}
