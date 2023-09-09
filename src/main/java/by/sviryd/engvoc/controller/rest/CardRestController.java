package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.domain.Views;
import by.sviryd.engvoc.repos.exception.UpdateAllOrNothingException;
import by.sviryd.engvoc.service.CardService;
import by.sviryd.engvoc.service.CardUnrepeatedService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.beans.FeatureDescriptor;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/json/card")
public class CardRestController {
    @Autowired
    private CardService cardService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private CardUnrepeatedService cardUnrepeatedService;
    @Autowired
    private PictureMediaService pictureMediaService;


    @DeleteMapping("{id}")
    public void delete(
            @PathVariable("id") Card card) {
        cardService.delete(card);
    }

    @GetMapping("{id}")
    @JsonView({Views.Card.class})
    public Card get(@PathVariable("id") Card card) {
        return card;
    }

    @PostMapping()
    @JsonView({Views.Card.class})
    public Card save(
            @RequestBody Card card
    ) {
        return cardService.save(card);
    }

    @PutMapping("{id}")
    @JsonView({Views.Card.class})
    public Card update(
            @RequestBody Card card,
            @PathVariable("id") Card cardDb) {
        if (cardDb == null) {
            return card;
        } else {
            BeanUtils.copyProperties(card, cardDb, "id");
            return cardService.save(cardDb);
        }
    }

    @PostMapping("/saveWithoutPicture")
    @JsonView({Views.Card.class})
    public HashMap<Object, Object> saveWithoutPicture(
            @AuthenticationPrincipal User user,
            @RequestBody Card card
    ) {
        HashMap<Object, Object> errors = new HashMap<>();
        Card saved = null;
        if (card.isUnrepeated() && cardService.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(card, user) != null) {
            errors.put("notUnrepeatedCardError", "error");
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
    @JsonView({Views.Card.class})
    public HashMap<Object, Object> saveWithPicture(
            @AuthenticationPrincipal User user,
            @RequestPart("file") MultipartFile file,
            @RequestPart("card") String cardJson
    ) throws IOException {
        Card card = new ObjectMapper().readValue(cardJson, Card.class);
        HashMap<Object, Object> errors = new HashMap<>();
        Card saved = null;
        if (card.isUnrepeated() && cardService.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(card, user) != null) {
            errors.put("notUnrepeatedCardError", "error");
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

    @PostMapping("/ids")
    @JsonView(Views.Card.class)
    public List<Card> findAllById(
            @RequestBody String json
    ) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        JsonArray idsString = obj.get("ids").getAsJsonArray();
        Type arrayOfLongType = new TypeToken<ArrayList<UUID>>() {
        }.getType();
        List<UUID> indexes = gson.fromJson(idsString, arrayOfLongType);
        if (indexes.isEmpty()) return Collections.emptyList();
        return cardService.findAllById(indexes);
    }

    @GetMapping("/dictionary/{id}")
    @JsonView(Views.Card.class)
    public List<Card> findByDictionary(
            @PathVariable("id") Dictionary dictionary
    ) {
        String name = dictionary.getName(); // remove lazy
        return cardService.findAllByDictionary(dictionary);
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
        Type arrayOfLongType = new TypeToken<ArrayList<UUID>>() {
        }.getType();
        List<UUID> indexes = gson.fromJson(idsString, arrayOfLongType);
        if (indexes == null || indexes.isEmpty()) return;
        cardService.deleteByIdIn(indexes);
    }

    @DeleteMapping("/deleteByDictionary")
    public void deleteByDictionary(
            @RequestBody String json
    ) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        UUID id = UUID.fromString(obj.get("id").getAsString());
        Optional<Dictionary> dictionaryOpt = dictionaryService.findById(id);
        dictionaryOpt.ifPresent(dictionary -> cardService.deleteByDictionary(dictionary));
    }

    private HashMap<Object, Object> convertToMap(Object saved, Object notSaved) {
        HashMap<Object, Object> data = new HashMap<>();
        data.put("saved", saved);
        data.put("notSaved", notSaved);
        return data;
    }

    private boolean isNeedCheckUnrepeated(Card card, Dictionary dictionary) {
        return !card.isUnrepeated() && dictionary.isUnrepeated();
    }

    @PostMapping("/changeDictionary")
    @JsonView({Views.Card.class})
    public Map<Object, Object> changeDictionary(
            @AuthenticationPrincipal User user,
            @RequestBody Card card,
            @RequestParam("id") Dictionary dictionary
    ) {
        if (isNeedCheckUnrepeated(card, dictionary)) {
            Card alreadyIn = cardService.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(card, user);
            if (alreadyIn != null) return convertToMap(null, card);
        }
        try {
            cardService.updateDictionaryAndUnrepeatedById(card.getId(), dictionary, dictionary.isUnrepeated());
            card.setDictionary(dictionary);
            card.setUnrepeated(dictionary.isUnrepeated());
            return convertToMap(card, null);
        } catch (UpdateAllOrNothingException e) {
            return convertToMap(null, card);
        }

    }

    @PostMapping("/changeDictionaries")
    @JsonView({Views.Card.class})
    public Map<Object, Object> changeDictionaries(
            @AuthenticationPrincipal User user,
            @RequestBody List<Card> cards,
            @RequestParam("id") Dictionary dictionary
    ) {
        cards = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards);
        List<Card> checkCards = cards.stream().filter(c -> isNeedCheckUnrepeated(c, dictionary)).collect(Collectors.toList());
        List<Card> alreadyIn = cardService.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(checkCards, user);
        List<Card> repeated = cardUnrepeatedService.getRepeatedByWordAndTranslation(cards, alreadyIn);
        List<Card> forUpdate = cardUnrepeatedService.getUnrepeatedByWordAndTranslation(cards, repeated);
        List<UUID> forUpdateIds = forUpdate.stream().map(Card::getId).collect(Collectors.toList());
        try {
            cardService.updateDictionaryAndUnrepeatedByIdIn(forUpdateIds, dictionary, dictionary.isUnrepeated());
            forUpdate.forEach(c -> {
                c.setDictionary(dictionary);
                c.setUnrepeated(dictionary.isUnrepeated());
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
