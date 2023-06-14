package by.sviryd.engvoc.service;

import by.sviryd.engvoc.config.InfoConfig;
import by.sviryd.engvoc.config.SignConfig;
import by.sviryd.engvoc.controller.mail.MailHtmlController;
import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.domain.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class VerificationTokenSenderService {
    @Autowired
    private MailSenderService mailSenderService;
    @Autowired
    private VerificationTokenService verificationTokenService;
    @Autowired
    private SignConfig signConfig;
    @Autowired
    private MessageI18nService messageI18nService;
    @Autowired
    private InfoConfig infoConfig;
    @Autowired
    private MailHtmlController mailHtmlController;

    public void sendSignUpMailConfirmation(String url, User user, Locale locale) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plus(signConfig.getTokenExpirationDT()));
        verificationTokenService.save(verificationToken);
        if (!StringUtils.isEmpty(user.getEmail())) {
            String subject = messageI18nService.getMessage("signUpMailConfirmationSubject", new Object[]{}, locale);
            String message = mailHtmlController.getSignUpConfirmationHtml(locale, infoConfig.getUrl() + url + "/" + verificationToken.getToken());
            try {
                mailSenderService.sendAsHtml(user.getEmail(), subject, message);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
