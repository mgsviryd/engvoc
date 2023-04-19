package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.Views;
import by.sviryd.engvoc.service.CardService;
import by.sviryd.engvoc.service.CardUniqueService;
import by.sviryd.engvoc.service.DictionaryService;
import by.sviryd.engvoc.service.card.reader.ExcelCardShortReaderService;
import by.sviryd.engvoc.service.card.reader.XmlCardReaderService;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.beans.FeatureDescriptor;
import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/json/card")
public class CardRestController {
    @Autowired
    private CardService cardService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private CardUniqueService cardUniqueService;

    @DeleteMapping("{id}")
    public void delete(
            @PathVariable("id") Card card) {
        cardService.delete(card);
    }

    @GetMapping("{id}")
    public Card get(@PathVariable("id") Card card) {
        return card;
    }

    @PostMapping()
    public Card save(@RequestBody Card card) {
        return cardService.save(card);
    }

    @PutMapping("{id}")
    public Card update(@RequestBody Card card, @PathVariable("id") Card cardDb) {
        if (cardDb == null) {
            return card;
        } else {
            BeanUtils.copyProperties(card, cardDb, "id");
            return cardService.save(cardDb);
        }
    }

    @PostMapping("saveUnique")
    public Map<Object, Object> saveUnique(@RequestBody Card card) {
        Card unique = cardService.findDistinctByWordAndTranslationWithUniqueTrue(card.getWord(), card.getTranslation());
        Card saved = null;
        Card notSaved = null;
        if (unique == null) {
            saved = cardService.save(card);
        } else {
            notSaved = card;
        }
        HashMap<Object, Object> data = new HashMap<>();
        data.put("saved", saved);
        data.put("notSaved", notSaved);
        return data;
    }

    @PostMapping("updateUnique")
    public Map<Object, Object> updateUnique(@RequestBody Card card) {
        Card cardDb = cardService.findDistinctByWordAndTranslationWithUniqueTrue(card.getWord(), card.getTranslation());
        Card saved = null;
        Card notSaved = null;
        if (cardDb != null) {
            BeanUtils.copyProperties(card, cardDb, "id");
            saved = cardService.save(cardDb);
        } else {
            notSaved = card;
        }
        HashMap<Object, Object> data = new HashMap<>();
        data.put("saved", saved);
        data.put("notSaved", notSaved);
        return data;
    }

    @PostMapping(value = "ids")
    @JsonView(Views.CardPage.class)
    public List<Card> findAllById(
            @RequestBody String json
    ) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        JsonArray idsString = obj.get("ids").getAsJsonArray();
        Type arrayOfLongType = new TypeToken<ArrayList<Long>>() {
        }.getType();
        List<Long> indexes = gson.fromJson(idsString, arrayOfLongType);
        if (indexes.isEmpty()) return Collections.emptyList();
        return cardService.findAllById(indexes);
    }

    @GetMapping("dictionary/{id}")
    @JsonView(Views.CardPage.class)
    public List<Card> findByDictionary(
            @PathVariable("id") Dictionary dictionary
    ) {
        String name = dictionary.getName(); // remove lazy
        return cardService.getCardsByDictionary(dictionary);
    }

    @GetMapping("findAll")
    @JsonView(Views.CardPage.class)
    public List<Card> findAll() {
        return cardService.findAll();
    }


    @DeleteMapping("deleteByIdIn")
    public void deleteInBatch(
            @RequestBody String json
    ) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        JsonArray idsString = obj.get("ids").getAsJsonArray();
        Type arrayOfLongType = new TypeToken<ArrayList<Long>>() {
        }.getType();
        List<Long> indexes = gson.fromJson(idsString, arrayOfLongType);
        if (indexes == null || indexes.isEmpty()) return;
        cardService.deleteByIdIn(indexes);
    }

    @PostMapping("saveAllUnique")
    public Map<Object, Object> saveAllUnique(
            @RequestBody List<Card> cards
    ) {
        List<Card> repeatedInList = cardUniqueService.getRepeatedByWordAndTranslation(cards);
        cards.removeAll(repeatedInList);
        List<Card> unique = cardService.findDistinctByWordAndTranslationWithUniqueTrue(cards);
        List<Card> repeatedInDb = cardUniqueService.getRepeatedByWordAndTranslation(cards, unique);
        cards.removeAll(repeatedInDb);
        repeatedInDb.addAll(repeatedInList);
        List<Card> saved = new ArrayList<>();
        if (!cards.isEmpty()) {
            saved = cardService.saveAll(cards);
        }
        HashMap<Object, Object> data = new HashMap<>();
        data.put("saved", saved);
        data.put("notSaved", repeatedInDb);
        return data;
    }

    @PostMapping("updateAllUnique")
    public Map<Object, Object> updateAllUnique(
            @RequestBody List<Card> cards
    ) {
        List<Card> repeatedInList = cardUniqueService.getRepeatedByWordAndTranslation(cards);
        cards.removeAll(repeatedInList);
        List<Card> alreadyIn = cardService.findDistinctByWordAndTranslationWithUniqueTrue(cards);
        List<Card> repeated = cardUniqueService.getRepeatedByWordAndTranslation(cards, alreadyIn);
        IntStream.range(0, repeated.size()).forEach((x) -> BeanUtils.copyProperties(repeated.get(x), alreadyIn.get(x), getNullPropertyNames(repeated.get(x))));
        cards.removeAll(repeated);
        cards.addAll(repeatedInList);
        List<Card> saved = new ArrayList<>();
        if (!alreadyIn.isEmpty()) {
            saved = cardService.saveAll(alreadyIn);
        }
        HashMap<Object, Object> data = new HashMap<>();
        data.put("saved", saved);
        data.put("notSaved", cards);
        return data;
    }

    @PostMapping("changeDictionary")
    public Map<Object, Object> changeDictionary(
            @RequestBody String json
    ) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        Long cardId = obj.get("cardId").getAsLong();
        Long destDictionaryId = obj.get("destId").getAsLong();
        Card card = cardService.findById(cardId).get();
        Optional<Dictionary> destOpt = dictionaryService.findById(destDictionaryId);
        boolean isNeedCheckUnique = obj.get("isNeedCheckUnique").getAsBoolean();
        if (isNeedCheckUnique)
        {
            card.setDictionary(destOpt.get());
            return saveUnique(card);
        }
        Card saved = null;
        Card notSaved = card;
        if (destOpt.isPresent()) {
            Dictionary dictionary = destOpt.get();
            notSaved.setDictionary(dictionary);
            saved = cardService.save(notSaved);
            notSaved = null;
        }
        HashMap<Object, Object> data = new HashMap<>();
        data.put("saved", saved);
        data.put("notSaved", notSaved);
        return data;
    }

    @PostMapping("changeDictionaries")
    public Map<Object, Object> changeDictionaries(
            @RequestBody String json
    ) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        JsonArray cardIdsJson = obj.get("cardIds").getAsJsonArray();
        Type arrayOfLongType = new TypeToken<ArrayList<Long>>() {
        }.getType();
        List<Long> cardIds = gson.fromJson(cardIdsJson, arrayOfLongType);
        Long destDictionaryId = obj.get("destId").getAsLong();
        List<Card> cards = cardService.findAllById(cardIds);
        Optional<Dictionary> destOpt = dictionaryService.findById(destDictionaryId);
        boolean isNeedCheckUnique = obj.get("isNeedCheckUnique").getAsBoolean();
        if (isNeedCheckUnique)
        {
            cards.forEach(c -> c.setDictionary(destOpt.get()));
            return saveAllUnique(cards);
        }
        List<Card> saved = new ArrayList<>();
        List<Card> notSaved = cards;
        if (destOpt.isPresent()) {
            Dictionary dictionary = destOpt.get();
            notSaved.forEach(card -> card.setDictionary(dictionary));
            saved = cardService.saveAll(notSaved);
            notSaved = null;
        }
        HashMap<Object, Object> data = new HashMap<>();
        data.put("saved", saved);
        data.put("notSaved", notSaved);
        return data;
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors()).map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null).toArray(String[]::new);
    }
}
