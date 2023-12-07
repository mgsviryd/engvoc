package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.User;
import by.sviryd.engvoc.domain.Views;
import by.sviryd.engvoc.domain.Vocabulary;
import by.sviryd.engvoc.provider.AuthProvider;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/json/dictionary")
public class DictionaryRestController {
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private PictureMediaService pictureMediaService;
    @Autowired
    private CardService cardService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthProvider authProvider;
    @Autowired
    private VocabularyService vocabularyService;
    @Autowired
    private MessageI18nService messageI18nService;

    @DeleteMapping("{id}")
    public void delete(
            @PathVariable("id") Dictionary dictionary
    ) {
        cardService.deleteByDictionary(dictionary);
        dictionaryService.delete(dictionary);
    }

    @GetMapping("{id}")
    @JsonView({Views.Dictionary.class})
    public Dictionary get(@PathVariable("id") Dictionary dictionary) {
        return dictionary;
    }

    @PostMapping()
    @JsonView({Views.Dictionary.class})
    public Dictionary save(
            @RequestBody Dictionary dictionary
    ) {
        return dictionaryService.save(dictionary);
    }


    @PutMapping("{id}")
    @JsonView({Views.Dictionary.class})
    public Dictionary update(@RequestBody Dictionary dictionary, @PathVariable("id") Dictionary dictionaryDb) {
        BeanUtils.copyProperties(dictionary, dictionaryDb, "id");
        return dictionaryService.save(dictionaryDb);
    }

    @GetMapping("/findAll")
    @JsonView(Views.Dictionary.class)
    public List<Dictionary> findAll() {
        return dictionaryService.findAll();
    }

    @PostMapping("/saveWithoutPicture")
    @JsonView({Views.DictionaryAndLocaleExceptionMessage.class})
    public HashMap<Object, Object> saveWithoutPicture(
            @AuthenticationPrincipal User user,
            Locale locale,
            @RequestBody Map<String, String> json
    ) throws IOException {
        String dictionaryJson = json.get("dictionary");
        String vocabularyId = json.get("vocabularyId");
        Dictionary dictionary = new ObjectMapper().readValue(dictionaryJson, Dictionary.class);
        Optional<Vocabulary> vocabularyOpt = vocabularyService.findById(UUID.fromString(vocabularyId));
        Optional<Dictionary> dictionaryDbOpt = dictionaryService.findByNameAndUnrepeated(dictionary.getName(), dictionary.isUnrepeated());
        Dictionary saved = null;
        HashMap<Object, Object> data = new HashMap<>();
        List<LocaleExceptionMessage> errors = new ArrayList<>();
        if (!dictionaryDbOpt.isPresent() && vocabularyOpt.isPresent()) {
            dictionary.setVocabulary(vocabularyOpt.get());
            dictionary.setAuthor(user);
            saved = dictionaryService.save(dictionary);
        } else {
            String code = "dictionaryNotUniqueError";
            String message = messageI18nService.getMessage(code, null, locale);
            LocaleExceptionMessage error = new LocaleExceptionMessage(code, "name", message);
            errors.add(error);
        }
        data.put("dictionary", saved);
        data.put("errors", errors);
        return data;
    }

    @PostMapping(value = "/saveWithPicture", consumes = {"multipart/form-data"})
    @JsonView({Views.DictionaryAndLocaleExceptionMessage.class})
    public HashMap<Object, Object> saveWithPicture(
            @AuthenticationPrincipal User user,
            Locale locale,
            @RequestPart("file") MultipartFile file,
            @RequestPart("dictionary") String dictionaryJson,
            @RequestPart("vocabularyId") String vocabularyId
    ) throws IOException {
        Dictionary dictionary = new ObjectMapper().readValue(dictionaryJson, Dictionary.class);
        Optional<Vocabulary> vocabularyOpt = vocabularyService.findById(UUID.fromString(vocabularyId));
        Optional<Dictionary> dictionaryDbOpt = dictionaryService.findByNameAndUnrepeated(dictionary.getName(), dictionary.isUnrepeated());
        Dictionary saved = null;
        HashMap<Object, Object> data = new HashMap<>();
        List<LocaleExceptionMessage> errors = new ArrayList<>();
        if (!dictionaryDbOpt.isPresent() && vocabularyOpt.isPresent()) {
            String picture = pictureMediaService.savePictureOrRollback(file);
            dictionary.setPicture(picture);
            dictionary.setVocabulary(vocabularyOpt.get());
            dictionary.setAuthor(user);
            saved = dictionaryService.save(dictionary);
        } else {
            String code = "dictionaryNotUniqueError";
            String message = messageI18nService.getMessage(code, null, locale);
            LocaleExceptionMessage error = new LocaleExceptionMessage(code, "name", message);
            errors.add(error);
        }
        data.put("dictionary", saved);
        data.put("errors", errors);
        return data;
    }

    @DeleteMapping(value = "/deleteByIdIn", consumes = {"application/json"})
    public void deleteInBatch(
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
        List<Dictionary> dictionariesDb = dictionaryService.findAllById(indexes);
        if (dictionariesDb.isEmpty()) return;
        cardService.deleteByDictionaryIn(dictionariesDb);
        dictionaryService.deleteByIdIn(dictionariesDb.stream().map(Dictionary::getId).collect(Collectors.toList()));
    }

    @PostMapping("/deleteByUnrepeated/danger")
    @JsonView({Views.DictionaryAndLocaleExceptionMessage.class})
    public HashMap<Object, Object> deleteByUnrepeatedDanger(
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
        Dictionary dictionary = null;
        if (actual.equals(expected)) {
            boolean unrepeated = Boolean.parseBoolean(json.get("unrepeated"));
            cardService.deleteByClientAndVocabularyAndUnrepeated(user, vocabulary, unrepeated);
            dictionaryService.deleteByAuthorAndUnrepeated(user, unrepeated);
            if (unrepeated) {
                dictionary = dictionaryService.saveNewUnrepeated(user, vocabulary);
            }
        } else {
            String code = "incorrectInputError";
            String message = messageI18nService.getMessage(code, null, locale);
            LocaleExceptionMessage error = new LocaleExceptionMessage(code, "actual", message);
            errors.add(error);
        }
        data.put("dictionary", dictionary);
        data.put("errors", errors);
        return data;
    }
}
