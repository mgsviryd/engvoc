package by.sviryd.engvoc.service;

import by.sviryd.engvoc.config.InfoConfig;
import by.sviryd.engvoc.config.RegistrationTokenExpirationDTConfig;
import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.domain.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class VerificationTokenSenderService {
    @Autowired
    private MailSenderService mailSenderService;
    @Autowired
    private VerificationTokenService verificationTokenService;
    @Autowired
    private RegistrationTokenExpirationDTConfig registrationTokenExpirationDTConfig;
    @Autowired
    private MessageI18nService messageI18nService;
    @Autowired
    private InfoConfig infoConfig;

    public void sendVerificationToken(User user, Locale locale) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plus(registrationTokenExpirationDTConfig.getExpirationDT()));
        verificationTokenService.save(verificationToken);
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = messageI18nService.getMessage(
                    "verificationTokenMailMessage",
                    new Object[]{
                            user.getUsername(),
                            infoConfig.getUrl(),
                            verificationToken.getToken()},
                    locale
            );
            String subject = messageI18nService.getMessage("verificationTokenMailSubject", new Object[]{infoConfig.getLogo()}, locale);
            mailSenderService.send(user.getEmail(), subject, message);
        }
    }
}
