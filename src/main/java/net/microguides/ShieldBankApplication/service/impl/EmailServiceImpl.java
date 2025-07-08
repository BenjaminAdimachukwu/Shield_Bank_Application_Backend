package net.microguides.ShieldBankApplication.service.impl;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import net.microguides.ShieldBankApplication.dto.EmailDetails;
import net.microguides.ShieldBankApplication.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@Service
public class EmailServiceImpl implements EmailService {
    private final Dotenv dotenv;

    public EmailServiceImpl(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    public void sendEmailAlert(EmailDetails emailDetails) {
        String apiKey = dotenv.get("SENDGRID_API_KEY");
        String senderEmail = dotenv.get("SENDGRID_SENDER_EMAIL");

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

    @Override
    public void sendEmailWithAttachment(EmailDetails emailDetails) {
        String apiKey = dotenv.get("SENDGRID_API_KEY");
        String senderEmail = dotenv.get("SENDGRID_SENDER_EMAIL");

        Email from = new Email(senderEmail);
        Email to = new Email(emailDetails.getRecipient());
        Content content = new Content("text/plain", emailDetails.getMessageBody());
        Mail mail = new Mail(from, emailDetails.getSubject(), to, content);

        // Check if attachment file exists
        if (emailDetails.getAttachment() != null) {
            try {
                File file = new File(emailDetails.getAttachment());
                if (!file.exists()) {
                    throw new RuntimeException("Attachment file not found: " + file.getAbsolutePath());
                }

                byte[] fileBytes = Files.readAllBytes(file.toPath());
                String encodedFile = Base64.getEncoder().encodeToString(fileBytes);

                Attachments attachments = new Attachments();
                attachments.setContent(encodedFile);
                attachments.setType("application/pdf");
                attachments.setFilename(file.getName());
                attachments.setDisposition("attachment");

                mail.addAttachments(attachments);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read or attach the file", e);
            }
        }

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to send email with attachment", ex);
        }
    }
}
