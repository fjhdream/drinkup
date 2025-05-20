package cool.drinkup.drinkup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
@SpringBootApplication
@EnableRetry
public class DrinkupApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrinkupApplication.class, args);
	}

}
