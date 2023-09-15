package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Vocabulary;
import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.domain.Views;
import by.sviryd.engvoc.service.CardService;
import by.sviryd.engvoc.service.CardUnrepeatedService;
import by.sviryd.engvoc.service.CardUploadService;
import by.sviryd.engvoc.service.card.reader.ExcelCardShortReaderService;
import by.sviryd.engvoc.service.card.reader.XmlCardReaderService;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    private CardUnrepeatedService cardUnrepeatedService;

    @PostMapping(value = "/upload/excel/file", consumes = {"multipart/form-data"})
    @JsonView({Views.DictionaryCard.class})
    public HashMap<Object, Object> excelFile(
            @AuthenticationPrincipal User user,
            @RequestPart("file") MultipartFile file,
            @RequestPart("vocabulary") String vocabularyJson,
            @RequestPart("options") String optionsJson
    ) throws IOException {
        List<Card> cards = excelCardShortReaderService.extract(file);
        cards = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards);
        return getData(user, vocabularyJson, optionsJson, cards);
    }

    @PostMapping(value = "upload/xml/file", consumes = {"multipart/form-data"})
    @JsonView({Views.DictionaryCard.class})
    public HashMap<Object, Object> xmlFile(
            @AuthenticationPrincipal User user,
            @RequestPart("file") MultipartFile file,
            @RequestPart("vocabulary") String vocabularyJson,
            @RequestPart("options") String optionsJson
    ) throws IOException {
        List<Card> cards = xmlCardReaderService.extract(file);
        cards = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards);
        return getData(user, vocabularyJson, optionsJson, cards);
    }

    @PostMapping(value = "/upload/excel/files", consumes = {"multipart/form-data"})
    @JsonView({Views.DictionaryCard.class})
    public HashMap<Object, Object> excelFiles(
            @AuthenticationPrincipal User user,
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("vocabulary") String vocabularyJson,
            @RequestPart("options") String optionsJson
    ) throws IOException {
        List<Card> cards = Arrays.stream(files)
                .map(excelCardShortReaderService::extract)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        cards = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards);
        return getData(user, vocabularyJson, optionsJson, cards);
    }

    @PostMapping(value = "/upload/xml/files", consumes = {"multipart/form-data"})
    @JsonView({Views.DictionaryCard.class})
    public HashMap<Object, Object> xmlFiles(
            @AuthenticationPrincipal User user,
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("vocabulary") String vocabularyJson,
            @RequestPart("options") String optionsJson
    ) throws IOException {
        List<Card> cards = Arrays.stream(files)
                .map(xmlCardReaderService::extract)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        cards = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards);
        return getData(user, vocabularyJson, optionsJson, cards);
    }

    private HashMap<Object, Object> getData(@AuthenticationPrincipal User user, @RequestPart("vocabulary")String vocabularyJson, @RequestPart("options") String optionsJson, List<Card> cards) throws IOException {
        Vocabulary vocabulary = new ObjectMapper().readValue(vocabularyJson, Vocabulary.class);
        cards.forEach(c -> {
            c.setClient(user);
            c.setVocabulary(vocabulary);
        });
        TypeReference<HashMap<String, Boolean>> typeRef = new TypeReference<HashMap<String, Boolean>>() {};
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
        if (saveAllUploadCards){
            data.putAll(cardUploadService.saveNewDictionariesAndCards(user, cards, vocabulary));
        }
        if(updateCardsWithAbsentSound){
            data.putAll(cardUploadService.updateCardsWithAbsentSound(user, cards, vocabulary));
        }
        return data;
    }
}
