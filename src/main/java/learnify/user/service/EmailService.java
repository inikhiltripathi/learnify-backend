package learnify.user.service;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOtpToEmail(String to, String otp){

        String subject = "Learnify OTP Verification Code";
        String body = "Hello,\n"
        + "Thank you for using Learnify.\n"
        + "Your One-Time Password (OTP) for email verification is:\n"
        + otp + "\n\n"
        + "This OTP is valid for the next 5 minutes. Please do not share this code with anyone.\n"
        + "If you did not request this, please ignore this email.\n"
        + "Best regards,\n"
        + "Team Learnify";

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
