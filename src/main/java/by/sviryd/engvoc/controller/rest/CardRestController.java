package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.Views;
import by.sviryd.engvoc.repos.exception.UpdateAllOrNothingException;
import by.sviryd.engvoc.service.CardService;
import by.sviryd.engvoc.service.CardUniqueService;
import by.sviryd.engvoc.service.DictionaryService;
import by.sviryd.engvoc.service.PictureMediaService;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.IOException;
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
    @Autowired
    private PictureMediaService pictureMediaService;


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

    @PostMapping("/saveWithoutPicture")
    public HashMap<Object, Object> saveWithoutPicture(
            @RequestBody Card card
    ) {
        HashMap<Object, Object> errors = new HashMap<>();
        Card saved = null;
        if (card.isUnique() && cardService.findDistinctByWordAndTranslationWithUniqueTrue(card) != null) {
            errors.put("notUniqueCardError", "error");
        } else {
            Optional<Dictionary> dictionaryOpt = dictionaryService.findById(card.getDictionary().getId());
            card.setDictionary(dictionaryOpt.get());
            saved = cardService.save(card);
        }
        HashMap<Object, Object> data = new HashMap<>();
        data.put("saved", saved);
        data.put("errors", errors);
        return data;
    }

    @PostMapping(value = "/saveWithPicture", consumes = {"multipart/form-data"})
    public HashMap<Object, Object> saveWithPicture(
            @RequestPart("file") MultipartFile file,
            @RequestPart("card") String cardJson
    ) throws IOException {
        Card card = new ObjectMapper().readValue(cardJson, Card.class);
        HashMap<Object, Object> errors = new HashMap<>();
        Card saved = null;
        if (card.isUnique() && cardService.findDistinctByWordAndTranslationWithUniqueTrue(card) != null) {
            errors.put("notUniqueCardError", "error");
        } else {
            String picture = pictureMediaService.savePictureOrRollback(file);
            Optional<Dictionary> dictionaryOpt = dictionaryService.findById(card.getDictionary().getId());
            card.setDictionary(dictionaryOpt.get());
            card.setPicture(picture);
            saved = cardService.save(card);
        }
        HashMap<Object, Object> data = new HashMap<>();
        data.put("saved", saved);
        data.put("errors", errors);
        return data;
    }

    @PostMapping("/saveUnique")
    public Map<Object, Object> saveUnique(@RequestBody Card card) {
        Card unique = cardService.findDistinctByWordAndTranslationWithUniqueTrue(card);
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

    @PostMapping("/updateUnique")
    public Map<Object, Object> updateUnique(@RequestBody Card card) {
        Card cardDb = cardService.findDistinctByWordAndTranslationWithUniqueTrue(card);
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

    @PostMapping("/ids")
    @JsonView(Views.Card.class)
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

    @GetMapping("/dictionary/{id}")
    @JsonView(Views.Card.class)
    public List<Card> findByDictionary(
            @PathVariable("id") Dictionary dictionary
    ) {
        String name = dictionary.getName(); // remove lazy
        return cardService.getCardsByDictionary(dictionary);
    }

    @GetMapping("/findAll")
    @JsonView(Views.Card.class)
    public List<Card> findAll() {
        return cardService.findAll();
    }


    @DeleteMapping("/deleteByIdIn")
    public void deleteByIdIn(
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

    @DeleteMapping("/deleteByDictionary")
    public void deleteByDictionary(
            @RequestBody String json
    ) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        Long id = obj.get("id").getAsLong();
        Optional<Dictionary> dictionaryOpt = dictionaryService.findById(id);
        if (dictionaryOpt.isPresent()) {
            cardService.deleteByDictionary(dictionaryOpt.get());
        }
    }

    @PostMapping("/saveAllUnique")
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

    @PostMapping("/updateAllUnique")
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

    private HashMap<Object, Object> convertToMap(Object saved, Object notSaved) {
        HashMap<Object, Object> data = new HashMap<>();
        data.put("saved", saved);
        data.put("notSaved", notSaved);
        return data;
    }

    private boolean isNeedCheckUnique(Card card, Dictionary dictionary) {
        return !card.isUnique() && dictionary.isUnique();
    }

    @PostMapping("/changeDictionary")
    public Map<Object, Object> changeDictionary(
            @RequestBody Card card,
            @RequestParam("id") Dictionary dictionary
    ) {
        if (isNeedCheckUnique(card, dictionary)) {
            Card alreadyIn = cardService.findDistinctByWordAndTranslationWithUniqueTrue(card);
            if (alreadyIn != null) return convertToMap(null, card);
        }
        try {
            cardService.updateDictionaryAndUniqueById(card.getId(), dictionary, dictionary.isUnique());
            card.setDictionary(dictionary);
            card.setUnique(dictionary.isUnique());
            return convertToMap(card, null);
        } catch (UpdateAllOrNothingException e) {
            return convertToMap(null, card);
        }

    }

    @PostMapping("/changeDictionaries")
    public Map<Object, Object> changeDictionaries(
            @RequestBody List<Card> cards,
            @RequestParam("id") Dictionary dictionary
    ) {
        List<Card> checkCards = cards.stream().filter(c -> isNeedCheckUnique(c, dictionary)).collect(Collectors.toList());
        List<Card> alreadyIn = cardService.findDistinctByWordAndTranslationWithUniqueTrue(checkCards);
        List<Card> repeated = cardUniqueService.getRepeatedByWordAndTranslation(cards, alreadyIn);
        List<Card> forUpdate = cardUniqueService.getNotRepeatedByWordAndTranslation(cards, repeated);
        List<Long> forUpdateIds = forUpdate.stream().map(by.sviryd.engvoc.domain.Card::getId).collect(Collectors.toList());
        try {
            cardService.updateDictionaryAndUniqueByIdIn(forUpdateIds, dictionary, dictionary.isUnique());
            forUpdate.forEach(c -> {
                c.setDictionary(dictionary);
                c.setUnique(dictionary.isUnique());
            });
            return convertToMap(forUpdate, repeated);
        } catch (UpdateAllOrNothingException e) {
            return convertToMap(Collections.emptyList(), cards);
        }
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors()).map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null).toArray(String[]::new);
    }
}
