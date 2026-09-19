package ijse.lk.AutoCareManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class AutoCareManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutoCareManagementApplication.class, args);
	}

}
