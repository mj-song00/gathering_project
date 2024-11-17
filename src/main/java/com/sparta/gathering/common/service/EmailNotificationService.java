package com.sparta.gathering.common.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${mail.sender}")
    private String senderEmail;

    public void sendEmailWithTemplate(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            // 템플릿 내용 처리
            Context context = new Context();
            context.setVariables(variables);
            String content = templateEngine.process(templateName, context);

            // MIME 메시지 생성
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 발송자 설정
            helper.setFrom(new InternetAddress(senderEmail, "Sparta Gathering Team 21"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            // 메일 발송
            mailSender.send(message);
        } catch (Exception e) {
            throw new BaseException(ExceptionEnum.EMAIL_SEND_FAILURE);
        }
    }

}
