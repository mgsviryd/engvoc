package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.*;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.provider.AuthProvider;
import by.sviryd.engvoc.service.*;
import by.sviryd.engvoc.util.LocaleExceptionMessage;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/json/vocabulary")
public class VocabularyRestController {
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthProvider authProvider;
    @Autowired
    private VocabularyService vocabularyService;
    @Autowired
    private MessageI18nService messageI18nService;
    @Autowired
    private CardService cardService;

    @PostMapping(value = "/save")
    @JsonView({Views.User.class})
    public HashMap<Object, Object> saveVocabulary(
            @AuthenticationPrincipal User user,
            Locale locale,
            @RequestBody Vocabulary vocabulary
    ) {
        HashMap<Object, Object> data = new HashMap<>();
        List<LocaleExceptionMessage> errors = new ArrayList<>();
        if (isVocabularyNonUnique(user, vocabulary)) {
            String code = "vocabularyNotUniqueError";
            String message = messageI18nService.getMessage(code, null, locale);
            LocaleExceptionMessage error = new LocaleExceptionMessage(code, "name", message);
            errors.add(error);
            data.put("errors", errors);
        } else {
            vocabulary.setAuthor(user);
            vocabulary = vocabularyService.save(vocabulary);
            user.addVocabulary(vocabulary);
            authProvider.refreshContext(user);
            Dictionary dictionary = dictionaryService.findIfAbsentSaveNewUnrepeated(user, vocabulary);
            data.put("user", user);
            data.put("vocabulary", vocabulary);
            data.put("errors", Collections.emptyList());
        }
        return data;
    }

    @PostMapping("/delete/danger")
    @JsonView({Views.UserAndLocaleExceptionMessage.class})
    public HashMap<Object, Object> deleteDanger(
            @AuthenticationPrincipal User user,
            Locale locale,
            @RequestBody HashMap<String, String> json
    ) throws IOException {
        HashMap<Object, Object> data = new HashMap<>();
        List<LocaleExceptionMessage> errors = new ArrayList<>();
        String vocabularyJson = json.get("vocabulary");
        Vocabulary vocabulary = new ObjectMapper().readValue(vocabularyJson, Vocabulary.class);
        String actual = json.get("actual");
        String expected = json.get("expected");
        if (actual.equals(expected)) {
            cardService.deleteByClientAndVocabulary(user, vocabulary);
            dictionaryService.deleteByAuthorAndVocabulary(user, vocabulary);
            vocabularyService.delete(vocabulary);
            List<Vocabulary> vocabularies = user.getVocabularies();
            int inx = IntStream.range(0, vocabularies.size()).filter(i -> vocabularies.get(i).getId().equals(vocabulary.getId())).findFirst().orElse(-1);
            if (inx >= 0) {
                vocabularies.remove(inx);
                user.setVocabularies(vocabularies);
                authProvider.refreshContext(user);
            }
        } else {
            String code = "incorrectInputError";
            String message = messageI18nService.getMessage(code, null, locale);
            LocaleExceptionMessage error = new LocaleExceptionMessage(code, "actual", message);
            errors.add(error);
        }
        data.put("user", user);
        data.put("errors", errors);
        return data;
    }

    @PostMapping(value = "/findData")
    @JsonView({Views.VocabularyDictionaryCardAndLocaleMessageException.class})
    public HashMap<Object, Object> findData(
            @AuthenticationPrincipal User user,
            @RequestBody Vocabulary vocabulary
    ) {
        List<Dictionary> dictionaries = dictionaryService.findAllByAuthorAndVocabulary(user, vocabulary);
        List<Card> cards = cardService.findAllByClientAndVocabulary(user, vocabulary);
        HashMap<Object, Object> data = new HashMap<>();
        data.put("dictionaries", dictionaries);
        data.put("cards", cards);
        return data;
    }

    public boolean isVocabularyNonUnique(User user, Vocabulary voc) {
        return user.getVocabularies().stream().anyMatch(x -> x.equals(voc));
    }
}
