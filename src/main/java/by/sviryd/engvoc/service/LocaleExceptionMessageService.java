package by.sviryd.engvoc.service;

import by.sviryd.engvoc.util.LocaleException;
import by.sviryd.engvoc.util.LocaleExceptionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class LocaleExceptionMessageService {
    @Autowired
    private MessageI18nService messageI18nService;

    public LocaleExceptionMessage getLEM(LocaleException localeException, Locale locale) {
        String message = messageI18nService.getMessage(localeException.getCode(), localeException.getArgs(), locale);
        return new LocaleExceptionMessage(localeException.getCode(), localeException.getAttribute(), message);
    }
}
