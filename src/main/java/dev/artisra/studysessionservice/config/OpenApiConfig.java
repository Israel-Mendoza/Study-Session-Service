package dev.artisra.studysessionservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "Study Session Service",
                version = "1.0",
                description = "This is a sample Spring Boot REST API for demonstration purposes.",
                contact = @Contact(
                        name = "Israel Mendoza",
                        email = "israel.mendoza9@icloud.com",
                        url = "https://artisra.dev"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        )
)
public class OpenApiConfig {
    // You can add more configuration here if needed
}
