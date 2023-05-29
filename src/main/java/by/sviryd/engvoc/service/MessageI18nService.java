package by.sviryd.engvoc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class MessageI18nService implements MessageSource {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private MessageSourceResourceBundleService messageSourceResourceBundleService;

    @Override
    public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

    @Override
    public String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
        return messageSource.getMessage(code, args, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return messageSource.getMessage(resolvable, locale);
    }

    public Map<String, String> getMessages(Locale locale) {
        Set<String> keys = getKeys(locale);
        TreeMap<String, String> messages = new TreeMap<>();
        keys.forEach(key -> messages.put(key, getMessage(key, null, locale)));
        return messages;
    }

    public Set<String> getKeys(Locale locale) {
        return messageSourceResourceBundleService.getKeys(locale);
    }
}
