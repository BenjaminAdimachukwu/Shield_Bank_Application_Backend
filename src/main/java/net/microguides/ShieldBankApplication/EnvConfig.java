package net.microguides.ShieldBankApplication;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {
    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .directory("./")  // Looks in project root directory
                .ignoreIfMissing() // Prevents crash if .env is missing
                .load();
    }
}