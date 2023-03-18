package com.rn.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SimpleAuthEmailService implements AuthEmailService {
    private final JavaMailSender emailSender;
    private final String sender;
    private final String verificationLink;
    private final String subject;




    @Autowired
    public SimpleAuthEmailService(
        JavaMailSender emailSender,
        @Value("${spring.mail.username}") String sender,
        @Value("${RedNet.app.verificationLink}") String verificationLink,
        @Value("${RedNet.app.verificationEmailSubject}") String subject
    ) {
        this.emailSender = emailSender;
        this.sender = sender;
        this.verificationLink = verificationLink;
        this.subject = subject;
    }




    @Override
    public void sendEmail(String receiver, String verificationToken) {
        try{
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(receiver);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(generateText(verificationToken));

            emailSender.send(mimeMessage);
        } catch(MessagingException ex) {
            throw new IllegalStateException("failed to send message");
        }
    }




    private String generateText(String verificationToken){
        return verificationLink + verificationToken;
    }
}
