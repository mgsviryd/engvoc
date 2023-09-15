package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.*;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.provider.AuthProvider;
import by.sviryd.engvoc.service.*;
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

    @PostMapping(value = "/findDictionariesAndCards")
    @JsonView({Views.DictionaryCard.class})
    public HashMap<Object, Object> findDictionariesAndCards(
            @AuthenticationPrincipal User user,
            @RequestBody Vocabulary vocabulary
    ){
        List<Dictionary> dictionaries = dictionaryService.findAllByAuthorAndVocabulary(user, vocabulary);
        List<Card> cards = cardService.findAllByClientAndVocabulary(user, vocabulary);
        HashMap<Object, Object> data = new HashMap<>();
        data.put("dictionaries", dictionaries);
        data.put("cards", cards);
        return data;
    }

    @PostMapping("/saveUnrepeated")
    @JsonView({Views.Dictionary.class})
    public HashMap<Object, Object> saveUnrepeated(
            @RequestBody Dictionary dictionary
    ) {
        Optional<Dictionary> dictionaryDbOpt = dictionaryService.findByNameAndUnrepeated(dictionary.getName(), dictionary.isUnrepeated());
        Dictionary saved = null;
        HashMap<Object, Object> errors = new HashMap<>();
        if (!dictionaryDbOpt.isPresent()) {
            saved = dictionaryService.save(dictionary);
        } else {
            errors.put("notUnrepeatedDictionaryError", "error");
        }
        HashMap<Object, Object> data = new HashMap<>();
        data.put("saved", saved);
        data.put("errors", errors);
        return data;
    }

    @PostMapping(value = "/saveUnrepeatedWithPicture", consumes = {"multipart/form-data"})
    @JsonView({Views.Dictionary.class})
    public HashMap<Object, Object> addWithPicture(
            @RequestPart("file") MultipartFile file,
            @RequestPart("dictionary") String dictionaryJson
    ) throws IOException {
        Dictionary dictionary = new ObjectMapper().readValue(dictionaryJson, Dictionary.class);
        Optional<Dictionary> dictionaryDbOpt = dictionaryService.findByNameAndUnrepeated(dictionary.getName(), dictionary.isUnrepeated());
        Dictionary saved = null;
        HashMap<Object, Object> errors = new HashMap<>();
        if (!dictionaryDbOpt.isPresent()) {
            String picture = pictureMediaService.savePictureOrRollback(file);
            dictionary.setPicture(picture);
            saved = dictionaryService.save(dictionary);
        } else {
            errors.put("notUnrepeatedDictionaryError", "error");
        }
        HashMap<Object, Object> data = new HashMap<>();
        data.put("saved", saved);
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

    @DeleteMapping(value = "/deleteByUnrepeated", consumes = {"application/json"})
    public void deleteByUnrepeated(
            @RequestBody String json
    ) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        boolean unrepeated = obj.get("unrepeated").getAsBoolean();
        dictionaryService.deleteByUnrepeated(unrepeated);
    }

    @PostMapping(value = "/saveNewUnrepeated")
    @JsonView({Views.Dictionary.class})
    public Dictionary saveNewUnrepeated(
            @AuthenticationPrincipal User user,
            @RequestBody Vocabulary vocabulary
    ){
        if(user.getVocabularies().contains(vocabulary)){
            return null;
        }else{
            vocabulary.setAuthor(user);
            vocabulary = vocabularyService.save(vocabulary);
            user.addVocabulary(vocabulary);
            authProvider.refreshContext(user);
            return dictionaryService.findIfAbsentSaveNewUnrepeated(user, vocabulary);
        }
    }
}
