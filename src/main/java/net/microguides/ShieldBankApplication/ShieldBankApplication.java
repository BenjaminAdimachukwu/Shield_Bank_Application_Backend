package net.microguides.ShieldBankApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPIDefinition(
		info = @Info(
				title = "The shield Bank App",
				description = "Backend Rest APIs for Shield Bank",
				version = "v1.0",
				contact = @Contact (
						name = "Benjamin Adimachukwu",
						email = "adimachiben@gmail.com",
						url = "https://github.com/BenjaminAdimachukwu/Shield_Bank_Application_Backend.git"
				),
				license = @License(
						name = "The shield Bank group",
						url = "https://github.com/BenjaminAdimachukwu/Shield_Bank_Application_Backend.git"
				)
		),
	externalDocs = @ExternalDocumentation(
			description = "The Shield Bank App documentation",
			url = "https://github.com/BenjaminAdimachukwu/Shield_Bank_Application_Backend.git"

	)
)
@SpringBootApplication
public class ShieldBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShieldBankApplication.class, args);
	}

}
