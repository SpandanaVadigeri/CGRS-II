package Backend.CGRSBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * CGRS Backend Application entry point.
 * Phase 3: @EnableScheduling activates EscalationService cron job.
 */
@SpringBootApplication
@EnableScheduling
public class CgrsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CgrsBackendApplication.class, args);
	}

}
