package cool.drinkup.drinkup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

import com.mzt.logapi.starter.annotation.EnableLogRecord;
@SpringBootApplication
@EnableRetry
@EnableLogRecord(tenant = "drinkup")
public class DrinkupApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrinkupApplication.class, args);
	}

}
