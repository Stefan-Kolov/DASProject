package mk.finki.ukim.mk.daswebapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class DasWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(DasWebApplication.class, args);
	}

}
