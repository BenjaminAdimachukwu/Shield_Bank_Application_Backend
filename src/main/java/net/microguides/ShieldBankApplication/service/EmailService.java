package net.microguides.ShieldBankApplication.service;

import net.microguides.ShieldBankApplication.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);
}
