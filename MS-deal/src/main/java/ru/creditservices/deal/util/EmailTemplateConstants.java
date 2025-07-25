package ru.creditservices.deal.util;

import java.util.Map;
import java.util.EnumMap;
import ru.creditservices.deal.model.enums.EmailTheme;

public class EmailTemplateConstants {

    public static final Map<EmailTheme, String> EMAIL_TEMPLATES = new EnumMap<>(EmailTheme.class);

    static {
        EMAIL_TEMPLATES.put(EmailTheme.FINISH_REGISTRATION,
                "Пожалуйста, завершите регистрацию для получения кредита.");
        EMAIL_TEMPLATES.put(EmailTheme.CREATE_DOCUMENTS,
                "Ваши документы успешно сформированы.");
        EMAIL_TEMPLATES.put(EmailTheme.SEND_DOCUMENTS,
                "Документы были отправлены на вашу почту.");
        EMAIL_TEMPLATES.put(EmailTheme.SEND_SES,
                "Пожалуйста, подпишите документы через электронную подпись.");
        EMAIL_TEMPLATES.put(EmailTheme.CREDIT_ISSUED,
                "Поздравляем! Кредит был успешно выдан.");
        EMAIL_TEMPLATES.put(EmailTheme.STATEMENT_DENIED,
                "К сожалению, ваша заявка на кредит была отклонена.");
    }

    private EmailTemplateConstants() {
    }
}
