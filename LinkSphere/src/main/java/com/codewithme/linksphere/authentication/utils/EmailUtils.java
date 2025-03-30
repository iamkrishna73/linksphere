package com.codewithme.linksphere.authentication.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class EmailUtils {
    private final JavaMailSender mailSender;

    public EmailUtils(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String email, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

       /// helper.setFrom("krishna137731@gmail.com", "LinkSphere");
        helper.setTo(email);

        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }
}
