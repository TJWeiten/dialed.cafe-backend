package cafe.dialed;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.clerk.backend_api.Clerk;
import com.clerk.backend_api.models.errors.ClerkErrors;
import com.clerk.backend_api.models.operations.GetEmailAddressResponse;
import com.clerk.backend_api.models.operations.GetPublicInterstitialResponse;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DialedApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DialedApplication.class, args);
	}

}
