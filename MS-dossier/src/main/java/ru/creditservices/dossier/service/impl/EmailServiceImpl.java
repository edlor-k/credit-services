package ru.creditservices.dossier.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.creditservices.dossier.dto.EmailMessageDto;
import ru.creditservices.dossier.mapper.EmailMessageMapper;
import ru.creditservices.dossier.model.entity.EmailMessageEntity;
import ru.creditservices.dossier.service.EmailService;
import ru.creditservices.dossier.util.DuplicateEmailCache;
import ru.creditservices.dossier.util.EmailSubjectConstants;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailMessageMapper emailMessageMapper;

    @Override
    public void sendEmail(EmailMessageDto message) {

        log.debug("Отправка email: {}, тема: {}", message.getAddress(), message.getTheme());

        EmailMessageEntity emailMessageEntity = emailMessageMapper.toEntity(message);

        if (DuplicateEmailCache.isDuplicate(emailMessageEntity.getStatementId(), emailMessageEntity.getTheme())) {
            log.warn("Дубликат email - не отправляем повторно: {}, тема: {}",
                    emailMessageEntity.getAddress(), emailMessageEntity.getTheme());
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String subject = EmailSubjectConstants.SUBJECTS.getOrDefault(
                    emailMessageEntity.getTheme(),
                    "Информационное сообщение"
            );

            helper.setFrom("support@creditservices.local");
            helper.setTo(emailMessageEntity.getAddress());
            helper.setSubject(subject);
            helper.setText(emailMessageEntity.getText(), false);

            mailSender.send(mimeMessage);
            log.info("Email отправлен на адрес {} с темой {}", emailMessageEntity.getAddress(), subject);

        } catch (MessagingException e) {
            log.error("Ошибка при отправке письма на {}: {}", emailMessageEntity.getAddress(), e.getMessage(), e);
        }
    }
}
