package by.sviryd.engvoc.controller.mail;

import by.sviryd.engvoc.config.InfoConfig;
import by.sviryd.engvoc.service.MessageI18nService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

@Service
public class MailHtmlController {
    private static final String CHARSET_NAME = "UTF-8";
    @Autowired
    private InfoConfig infoConfig;
    @Autowired
    private MessageI18nService messageI18nService;

    public String getSignUpConfirmationHtml(Locale locale, String link) {
        Document doc;
        try {
            File input = ResourceUtils.getFile("classpath:" + "templates/mail/signUpMailConfirmation.html");
            doc = Jsoup.parse(input, CHARSET_NAME, infoConfig.getUrl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        doc.getElementById("url").attr("href", infoConfig.getUrl());
        doc.getElementById("url").attr("data-saferedirecturl", infoConfig.getUrl());
        doc.getElementById("link").attr("href", link);
        doc.getElementById("link").text(messageI18nService.getMessage("signUpMailConfirmationLinkButton", new Object[]{}, locale));
        doc.getElementById("contactUrl").attr("href", infoConfig.getContactUrl());
        doc.getElementById("contactUrl").text(messageI18nService.getMessage("signUpMailConfirmationContactButton", new Object[]{}, locale));
        doc.getElementById("logoUrl").attr("src", infoConfig.getLogoUrl());
        doc.getElementById("p1").text(messageI18nService.getMessage("signUpMailConfirmationP1", new Object[]{}, locale));
        doc.getElementById("p2").text(messageI18nService.getMessage("signUpMailConfirmationP2", new Object[]{}, locale));
        doc.getElementById("p3").text(messageI18nService.getMessage("signUpMailConfirmationP3", new Object[]{}, locale));
        doc.getElementById("p4").text(messageI18nService.getMessage("signUpMailConfirmationP4", new Object[]{}, locale));
        doc.getElementById("p5").text(messageI18nService.getMessage("signUpMailConfirmationP5", new Object[]{}, locale));
        doc.getElementById("p6").text(messageI18nService.getMessage("signUpMailConfirmationP6", new Object[]{}, locale));
        return doc.html();
    }
}
