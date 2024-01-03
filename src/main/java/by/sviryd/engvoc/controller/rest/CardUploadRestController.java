package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.*;
import by.sviryd.engvoc.service.*;
import by.sviryd.engvoc.service.card.reader.ExcelCardShortReaderService;
import by.sviryd.engvoc.service.card.reader.MultipartCardReader;
import by.sviryd.engvoc.service.card.reader.XmlCardReaderService;
import by.sviryd.engvoc.service.entityAdjuster.UploadCardEntityAdjusterService;
import by.sviryd.engvoc.util.LocaleExceptionMessage;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/json/card")
public class CardUploadRestController {
    @Autowired
    private ExcelCardShortReaderService excelCardShortReaderService;
    @Autowired
    private XmlCardReaderService xmlCardReaderService;
    @Autowired
    private CardUploadService cardUploadService;
    @Autowired
    private CardService cardService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private CardUnrepeatedService cardUnrepeatedService;
    @Autowired
    private VocabularyService vocabularyService;
    @Autowired
    private MessageI18nService messageI18nService;
    @Autowired
    private UploadCardEntityAdjusterService uploadCardEntityAdjusterService;


    @PostMapping(value = "/upload/dictionary/file", consumes = {"multipart/form-data"})
    @JsonView({Views.VocabularyDictionaryCardAndLocaleMessageException.class})
    public HashMap<Object, Object> fileToDictionary(
            @AuthenticationPrincipal User user,
            @RequestPart("file") MultipartFile file,
            @RequestPart("dictionaryId") String dictionaryId,
            @RequestPart("fileTypeLabel") String fileTypeLabel,
            Locale locale
    ) {
        HashMap<Object, Object> data = new HashMap<>();
        List<Card> saved = new ArrayList<>();
        List<Card> notSaved = new ArrayList<>();
        List<LocaleExceptionMessage> errors = new ArrayList<>();
        List<Card> cards = null;
        MultipartCardReader reader = null;
        switch (fileTypeLabel) {
            case "excel": {
                reader = excelCardShortReaderService;
                break;
            }
            case "xml": {
                reader = xmlCardReaderService;
                break;
            }
            default: {
                String code = "IllegalFileTypeUploadException";
                String message = messageI18nService.getMessage(code, null, locale);
                LocaleExceptionMessage error = new LocaleExceptionMessage(code, "fileTypeLabel", message);
                errors.add(error);
                return compileData(data, saved, notSaved, errors);
            }
        }
        try {
            cards = reader.extract(file);
        } catch (Exception e) {
            String code = "extractCardError";
            String message = messageI18nService.getMessage(code, null, locale);
            LocaleExceptionMessage error = new LocaleExceptionMessage(code, file.getOriginalFilename(), message);
            errors.add(error);
            return compileData(data, saved, notSaved, errors);
        }
        cards = uploadCardEntityAdjusterService.adjust(cards);
        Optional<Dictionary> dictionaryOpt = dictionaryService.findById(UUID.fromString(dictionaryId));
        Dictionary dictionary = dictionaryOpt.get();
        Vocabulary vocabulary = dictionary.getVocabulary();
        cards.forEach(c -> {
            c.setClient(user);
            c.setVocabulary(vocabulary);
            c.setDictionary(dictionary);
            c.setUnrepeated(dictionary.isUnrepeated());
        });
        if (dictionary.isUnrepeated()) {
            cards = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards);
            int cycles = cards.size() / 100;
            for (int i = 0; i < cycles; i++) {
                List<Card> between;
                if (i != cycles - 1) {
                    between = cardService.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(cards.subList(i * 100, (i + 1) * 100), user);
                } else {
                    between = cardService.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(cards.subList(i * 100, cards.size()), user);
                }
                notSaved.addAll(between);
            }
            saved = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards, notSaved);
        } else {
            saved = cards;
        }
        if (saved != null) {
            saved = cardService.saveAll(saved);
        }
        return compileData(data, saved, notSaved, errors);
    }

    @PostMapping(value = "/upload/dictionary/files", consumes = {"multipart/form-data"})
    @JsonView({Views.VocabularyDictionaryCardAndLocaleMessageException.class})
    public HashMap<Object, Object> filesToDictionary(
            @AuthenticationPrincipal User user,
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("dictionaryId") String dictionaryId,
            @RequestPart("fileTypeLabel") String fileTypeLabel,
            Locale locale
    ) {
        HashMap<Object, Object> data = new HashMap<>();
        List<Card> saved = new ArrayList<>();
        List<Card> notSaved = new ArrayList<>();
        List<LocaleExceptionMessage> errors = new ArrayList<>();
        List<Card> cards = null;
        MultipartCardReader reader = null;
        switch (fileTypeLabel) {
            case "excel": {
                reader = excelCardShortReaderService;
                break;
            }
            case "xml": {
                reader = xmlCardReaderService;
                break;
            }
            default: {
                String code = "IllegalFileTypeUploadException";
                String message = messageI18nService.getMessage(code, null, locale);
                LocaleExceptionMessage error = new LocaleExceptionMessage(code, "fileTypeLabel", message);
                errors.add(error);
                return compileData(data, saved, notSaved, errors);
            }
        }
        MultipartCardReader finalReader = reader;
        Function<MultipartFile, List<Card>> extract = (file) -> {
            try {
                return finalReader.extract(file);

            } catch (IOException e) {
                String code = "extractCardError";
                String message = messageI18nService.getMessage(code, null, locale);
                LocaleExceptionMessage error = new LocaleExceptionMessage(code, file.getOriginalFilename(), message);
                errors.add(error);
                return new ArrayList<>();
            }
        };
        cards = Arrays.stream(files)
                .map(extract)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        cards = uploadCardEntityAdjusterService.adjust(cards);
        Optional<Dictionary> dictionaryOpt = dictionaryService.findById(UUID.fromString(dictionaryId));
        Dictionary dictionary = dictionaryOpt.get();
        Vocabulary vocabulary = dictionary.getVocabulary();
        cards.forEach(c -> {
            c.setClient(user);
            c.setVocabulary(vocabulary);
            c.setDictionary(dictionary);
            c.setUnrepeated(dictionary.isUnrepeated());
        });
        if (dictionary.isUnrepeated()) {
            cards = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards);
            notSaved = cardService.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(cards, user);
            saved = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards, notSaved);
        } else {
            saved = cards;
        }
        if (saved != null) {
            saved = cardService.saveAll(saved);
        }
        return compileData(data, saved, notSaved, errors);
    }

    @NotNull
    private HashMap<Object, Object> compileData(HashMap<Object, Object> data, List<Card> saved, List<Card> notSaved, List<LocaleExceptionMessage> errors) {
        data.put("saved", saved);
        data.put("notSaved", notSaved);
        data.put("errors", errors);
        return data;
    }

    @NotNull
    private HashMap<Object, Object> getDataWithException(@RequestPart("file") MultipartFile file, Locale locale, HashMap<Object, Object> data, List<Card> saved, List<Card> notSaved, List<LocaleExceptionMessage> errors, String code) {
        String message = messageI18nService.getMessage(code, null, locale);
        LocaleExceptionMessage error = new LocaleExceptionMessage(code, file.getOriginalFilename(), message);
        errors.add(error);
        return compileData(data, saved, notSaved, errors);
    }


    @PostMapping(value = "/upload/vocabulary/excel/file", consumes = {"multipart/form-data"})
    @JsonView({Views.VocabularyDictionaryCardAndLocaleMessageException.class})
    public HashMap<Object, Object> excelFile(
            @AuthenticationPrincipal User user,
            @RequestPart("file") MultipartFile file,
            @RequestPart("vocabularyId") String vocabularyId,
            @RequestPart("options") String optionsJson
    ) throws IOException {
        List<Card> cards = excelCardShortReaderService.extract(file);
        cards = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards);
        Optional<Vocabulary> vocabularyOpt = vocabularyService.findById(UUID.fromString(vocabularyId));
        Vocabulary vocabulary = vocabularyOpt.get();
        return getData(user, vocabulary, optionsJson, cards);
    }

    @PostMapping(value = "/upload/vocabulary/xml/file", consumes = {"multipart/form-data"})
    @JsonView({Views.VocabularyDictionaryCardAndLocaleMessageException.class})
    public HashMap<Object, Object> xmlFile(
            @AuthenticationPrincipal User user,
            @RequestPart("file") MultipartFile file,
            @RequestPart("vocabularyId") String vocabularyId,
            @RequestPart("options") String optionsJson
    ) throws IOException {
        List<Card> cards = xmlCardReaderService.extract(file);
        cards = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards);
        Optional<Vocabulary> vocabularyOpt = vocabularyService.findById(UUID.fromString(vocabularyId));
        Vocabulary vocabulary = vocabularyOpt.get();
        return getData(user, vocabulary, optionsJson, cards);
    }

    @PostMapping(value = "/upload/vocabulary/excel/files", consumes = {"multipart/form-data"})
    @JsonView({Views.VocabularyDictionaryCardAndLocaleMessageException.class})
    public HashMap<Object, Object> excelFiles(
            @AuthenticationPrincipal User user,
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("vocabularyId") String vocabularyId,
            @RequestPart("options") String optionsJson
    ) throws IOException {
        Function<MultipartFile, List<Card>> extract = (file) -> {
            try {
                return excelCardShortReaderService.extract(file);
            } catch (IOException e) {
                return new ArrayList<>();
            }
        };
        List<Card> cards = Arrays.stream(files)
                .map(extract)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        cards = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards);
        Optional<Vocabulary> vocabularyOpt = vocabularyService.findById(UUID.fromString(vocabularyId));
        Vocabulary vocabulary = vocabularyOpt.get();
        return getData(user, vocabulary, optionsJson, cards);
    }

    @PostMapping(value = "/upload/vocabulary/xml/files", consumes = {"multipart/form-data"})
    @JsonView({Views.VocabularyDictionaryCardAndLocaleMessageException.class})
    public HashMap<Object, Object> xmlFiles(
            @AuthenticationPrincipal User user,
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("vocabularyId") String vocabularyId,
            @RequestPart("options") String optionsJson
    ) throws IOException {
        List<Card> cards = Arrays.stream(files)
                .map(xmlCardReaderService::extract)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        cards = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards);
        Optional<Vocabulary> vocabularyOpt = vocabularyService.findById(UUID.fromString(vocabularyId));
        Vocabulary vocabulary = vocabularyOpt.get();
        return getData(user, vocabulary, optionsJson, cards);
    }

    private HashMap<Object, Object> getData(@AuthenticationPrincipal User user, Vocabulary vocabulary, @RequestPart("options") String optionsJson, List<Card> cards) throws IOException {
        cards.forEach(c -> {
            c.setClient(user);
            c.setVocabulary(vocabulary);
        });
        TypeReference<HashMap<String, Boolean>> typeRef = new TypeReference<HashMap<String, Boolean>>() {
        };
        HashMap<String, Boolean> options = new ObjectMapper().readValue(optionsJson, typeRef);
        Boolean saveNewUnrepeatedCards = options.get("saveNewUnrepeatedCards");
        Boolean updateLearnedStatusUnrepeatedCards = options.get("updateLearnedStatusUnrepeatedCards");
        Boolean saveAllUploadCards = options.get("saveAllUploadCards");
        Boolean updateCardsWithAbsentSound = options.get("updateCardsWithAbsentSound");
        HashMap<Object, Object> data = new HashMap<>();
        if (updateLearnedStatusUnrepeatedCards) {
            data.putAll(cardUploadService.updateLearnedStatusUnrepeatedCards(user, cards, vocabulary));
        }
        if (saveNewUnrepeatedCards) {
            data.putAll(cardUploadService.saveNewUnrepeatedCards(user, cards, vocabulary));
        }
        if (saveAllUploadCards) {
            data.putAll(cardUploadService.saveNewDictionariesAndCards(user, cards, vocabulary));
        }
        if (updateCardsWithAbsentSound) {
            data.putAll(cardUploadService.updateCardsWithAbsentSound(user, cards, vocabulary));
        }
        return data;
    }
}
