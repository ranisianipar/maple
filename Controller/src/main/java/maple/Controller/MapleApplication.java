package maple.Controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("maple")
public class MapleApplication {

	public static void main(String[] args) { SpringApplication.run(MapleApplication.class, args); }
}
