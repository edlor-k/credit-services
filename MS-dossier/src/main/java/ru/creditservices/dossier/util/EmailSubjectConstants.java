package ru.creditservices.dossier.util;

import ru.creditservices.dossier.model.enums.EmailTheme;

import java.util.EnumMap;
import java.util.Map;

public class EmailSubjectConstants {

    public static final Map<EmailTheme, String> SUBJECTS = new EnumMap<>(EmailTheme.class);

    static {
        SUBJECTS.put(EmailTheme.FINISH_REGISTRATION, "Завершите регистрацию");
        SUBJECTS.put(EmailTheme.CREATE_DOCUMENTS, "Документы сформированы");
        SUBJECTS.put(EmailTheme.SEND_DOCUMENTS, "Документы отправлены");
        SUBJECTS.put(EmailTheme.SEND_SES, "Подпишите документы через ЭП");
        SUBJECTS.put(EmailTheme.CREDIT_ISSUED, "Кредит выдан");
        SUBJECTS.put(EmailTheme.STATEMENT_DENIED, "Заявка отклонена");
    }

    private EmailSubjectConstants() {
    }
}
