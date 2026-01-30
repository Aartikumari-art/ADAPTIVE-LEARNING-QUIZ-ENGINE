package com.quiz.AdaptiveQuiz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @org.springframework.beans.factory.annotation.Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Email Verification - Adaptive Quiz App";
        String verificationUrl = frontendUrl + "/verify-email?email=" + toEmail + "&token=" + token;

        // Note: The /verify-email route on frontend (port 5173) will handle the call to
        // backend

        String messageComp = "Thank you for registering. Please click the link below to verify your email:\n"
                + verificationUrl
                + "\n\nIf you did not register, please ignore this email.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@adaptivequiz.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(messageComp);

        try {
            mailSender.send(message);
            System.out.println("Verification email sent to " + toEmail);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email. Please check your email address.");
        }
    }
}
