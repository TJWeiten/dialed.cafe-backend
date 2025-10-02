package cafe.dialed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.clerk.backend_api.Clerk;
import com.clerk.backend_api.models.errors.ClerkErrors;
import com.clerk.backend_api.models.operations.GetEmailAddressResponse;
import com.clerk.backend_api.models.operations.GetPublicInterstitialResponse;

@SpringBootApplication
public class DialedApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DialedApplication.class, args);
//        Clerk sdk = Clerk.builder()
//                .bearerAuth(System.getenv().getOrDefault("BEARER_AUTH", ""))
//                .build();
//        GetPublicInterstitialResponse res = sdk.miscellaneous().getPublicInterstitial()
//                .call();
	}

}
