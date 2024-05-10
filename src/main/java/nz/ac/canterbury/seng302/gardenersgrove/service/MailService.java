package nz.ac.canterbury.seng302.gardenersgrove.service;

import jakarta.validation.Valid;
import org.hibernate.annotations.Array;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender emailSender;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Autowired
    public MailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        logger.info("Mail Host: " + host);
        logger.info("Mail Port: " + port);
        logger.info("Mail Username: " + username);
        logger.info("Mail Password: " + password);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gardenersgrovenoreply@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}