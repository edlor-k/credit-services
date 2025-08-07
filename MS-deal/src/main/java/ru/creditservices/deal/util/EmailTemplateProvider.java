package ru.creditservices.deal.util;

import org.springframework.stereotype.Component;
import ru.creditservices.deal.config.EmailTemplateProperties;
import ru.creditservices.deal.model.enums.EmailTheme;

import java.util.EnumMap;
import java.util.Map;

@Component
public class EmailTemplateProvider {

    private final Map<EmailTheme, String> emailTemplates = new EnumMap<>(EmailTheme.class);

    public EmailTemplateProvider(EmailTemplateProperties properties) {
        for (EmailTheme theme : EmailTheme.values()) {
            String value = properties.getTemplates().get(theme.name().toLowerCase().replace("_", "-"));
            if (value != null) {
                emailTemplates.put(theme, value);
            }
        }
    }

    public String getTemplate(EmailTheme theme) {
        return emailTemplates.get(theme);
    }
}
