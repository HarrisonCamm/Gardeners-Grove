package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    private MailService mailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mailService = new MailService(javaMailSender);
    }

    /**
     * Test for the sendSimpleMessage method in the MailService class.
     * Verifies that the send method of the JavaMailSender object is called with the correct SimpleMailMessage object.
     */
    @Test
    public void testSendSimpleMessage() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Message";

        mailService.sendSimpleMessage(to, subject, text);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gardenersgrovenoreply@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        verify(javaMailSender, times(1)).send(message);
    }
}