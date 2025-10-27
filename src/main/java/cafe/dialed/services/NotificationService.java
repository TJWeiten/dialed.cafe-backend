package cafe.dialed.services;

import com.resend.*;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Value("${resend.token}")
    private String resendToken;
    @Value("${resend.send.domain}")
    private String sendDomain;
    @Value("${resend.to.admin}")
    private String toAdmin;

    public void sendAdminNotification(String subject, String body) throws ResendException {
        Resend resend = new Resend(resendToken);

        CreateEmailOptions sendEmailParams = CreateEmailOptions.builder()
                .from(sendDomain)
                .to(toAdmin)
                .subject(subject)
                .html(String.format("<h1>DIALED ADMIN NOTIFICATION</h1><br/><span>%s</span>", body))
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(sendEmailParams);
            System.out.println("NotificationService:sendAdminNotification:" + data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }

    }

}
