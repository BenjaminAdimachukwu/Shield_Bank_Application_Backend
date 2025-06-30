package net.microguides.ShieldBankApplication.service.impl;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import net.microguides.ShieldBankApplication.dto.EmailDetails;
import net.microguides.ShieldBankApplication.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${sendgrid.sender.email}")
    private String senderEmail;

    public void sendEmailAlert(EmailDetails emailDetails) {
        Email from = new Email(senderEmail);
        Email to = new Email(emailDetails.getRecipient());
        Content content = new Content("text/plain", emailDetails.getMessageBody());
        Mail mail = new Mail(from, emailDetails.getSubject(), to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);


        } catch (IOException ex) {
            throw new RuntimeException("Failed to send email", ex);
        }
    }
}
