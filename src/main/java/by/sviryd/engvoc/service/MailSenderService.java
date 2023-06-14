package by.sviryd.engvoc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class MailSenderService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    public MailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAsText(String emailTo, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }

    public void sendAsHtml(String emailTo, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(username);
        helper.setTo(emailTo);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    public void sendAsTextWithAttachment(String emailTo, String subject, String text, String attachmentName, String attachmentPath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(username);
        helper.setTo(emailTo);
        helper.setSubject(subject);
        helper.setText(text);
        FileSystemResource file = new FileSystemResource(new File(attachmentPath));
        helper.addAttachment(attachmentName, file);
        mailSender.send(message);
    }

    public void sendAsHtmlWithAttachment(String emailTo, String subject, String htmlContent, String attachmentName, String attachmentPath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(username);
        helper.setTo(emailTo);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        FileSystemResource file = new FileSystemResource(new File(attachmentPath));
        helper.addAttachment(attachmentName, file);
        mailSender.send(message);
    }

}
