package leo.dev.doc_task_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DocTaskManagementApplication {

	public static void main(String[] args) {
		System.out.println(new BCryptPasswordEncoder().encode("admin"));
		SpringApplication.run(DocTaskManagementApplication.class, args);
	}

}
