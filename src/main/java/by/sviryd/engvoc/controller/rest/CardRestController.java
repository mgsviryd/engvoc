package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.*;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.repos.exception.UpdateAllOrNothingException;
import by.sviryd.engvoc.service.*;
import by.sviryd.engvoc.util.LocaleExceptionMessage;
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
    @Autowired
    private AudioMediaService audioMediaService;
    @Autowired
    private MessageI18nService messageI18nService;


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
    @JsonView({Views.CardAndLocaleExceptionMessage.class})
    public HashMap<Object, Object> saveWithoutPicture(
            @AuthenticationPrincipal User user,
            Locale locale,
            @RequestBody Card card
    ) {
        List<LocaleExceptionMessage> errors = new ArrayList<>();
        Card saved = null;
        if (card.isUnrepeated() && cardService.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(card, user) != null) {
            String code = "cardNotUniqueError";
            String message = messageI18nService.getMessage(code, null, locale);
            LocaleExceptionMessage error = new LocaleExceptionMessage(code, "translation", message);
            errors.add(error);
        } else {
            Optional<Dictionary> dictionaryOpt = dictionaryService.findById(card.getDictionary().getId());
            Dictionary d = dictionaryOpt.get();
            card.setDictionary(d);
            card.setVocabulary(d.getVocabulary());
            card.setAuthor(user);
            card.setClient(user);
            saved = cardService.save(card);
        }
        HashMap<Object, Object> data = new HashMap<>();
        data.put("card", saved);
        data.put("errors", errors);
        return data;
    }

    @PostMapping(value = "/saveWithAudioAndPicture", consumes = {"multipart/form-data"})
    @JsonView({Views.Card.class})
    public HashMap<Object, Object> saveWithAudioAndPicture(
            @AuthenticationPrincipal User user,
            Locale locale,
            @RequestPart(value = "audio", required = false) MultipartFile audioFile,
            @RequestPart(value = "picture", required = false) MultipartFile pictureFile,
            @RequestPart("card") String cardJson
    ) throws IOException {
        Card card = new ObjectMapper().readValue(cardJson, Card.class);
        List<LocaleExceptionMessage> errors = new ArrayList<>();
        Card saved = null;
        if (card.isUnrepeated() && cardService.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(card, user) != null) {
            String code = "cardNotUniqueError";
            String message = messageI18nService.getMessage(code, null, locale);
            LocaleExceptionMessage error = new LocaleExceptionMessage(code, "name", message);
            errors.add(error);
        } else {
            String audio = null;
            if (audioFile != null) {
                audio = audioMediaService.saveOrRollback(audioFile);
            }
            String picture = null;
            if (pictureFile != null) {
                picture = pictureMediaService.savePictureOrRollback(pictureFile);
            }
            Optional<Dictionary> dictionaryOpt = dictionaryService.findById(card.getDictionary().getId());
            Dictionary d = dictionaryOpt.get();
            Vocabulary vocabulary = d.getVocabulary();
            card.setDictionary(d);
            card.setVocabulary(vocabulary);
            card.setAuthor(user);
            card.setClient(user);
            card.setAudio(audio);
            card.setPicture(picture);
            card.setTarget(vocabulary.getTarget());
            card.setSource(vocabulary.getSource());
            saved = cardService.save(card);
        }
        HashMap<Object, Object> data = new HashMap<>();
        data.put("card", saved);
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
        List<UUID> ids = gson.fromJson(idsString, arrayOfLongType);
        if (ids == null || ids.isEmpty()) return;
        cardService.deleteByIdIn(ids);
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
        Vocabulary vocabulary = dictionary.getVocabulary();
        if (cards.isEmpty()) {
            return convertToMap(Collections.emptyList(), Collections.emptyList());
        }
        boolean unrepeated = dictionary.isUnrepeated();
        List<Card> notSaved = new ArrayList<>();
        if (!unrepeated) {
            cards.forEach(c -> {
                if (!user.isIdentical(c.getClientId())) {
                    c.setId(null);
                    c.setClient(user);
                }
                c.setDictionary(dictionary);
                c.setUnrepeated(unrepeated);
                c.setTarget(vocabulary.getTarget());
                c.setSource(vocabulary.getSource());
            });
        } else {
            notSaved = cardUnrepeatedService.getRepeatedByWordAndTranslation(cards);
            cards.removeAll(notSaved);
            List<Card> alreadyIn = new ArrayList<>();
            int cycles = cards.size() / 100;
            for (int i = 0; i < cycles; i++) {
                List<Card> between;
                if (i != cycles - 1) {
                    between = cardService.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(cards.subList(i * 100, (i + 1) * 100), user);
                } else {
                    between = cardService.findDistinctByClientAndWordAndTranslationWithUnrepeatedTrue(cards.subList(i * 100, cards.size()), user);
                }
                alreadyIn.addAll(between);
            }
            List<Card> repeated = cardUnrepeatedService.getRepeatedByWordAndTranslation(cards, alreadyIn);
            notSaved.addAll(repeated);
            cards.removeAll(repeated);
            cards.forEach(c -> {
                if (!user.isIdentical(c.getClientId())) {
                    c.setId(null);
                    c.setClient(user);
                }
                c.setDictionary(dictionary);
                c.setUnrepeated(unrepeated);
                c.setTarget(vocabulary.getTarget());
                c.setSource(vocabulary.getSource());
            });
        }
        try {
            cards = cardService.saveAll(cards);
            return convertToMap(cards, notSaved);
        } catch (Exception e) {
            cards.addAll(notSaved);
            return convertToMap(Collections.emptyList(), cards);
        }
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors()).map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null).toArray(String[]::new);
    }
}
