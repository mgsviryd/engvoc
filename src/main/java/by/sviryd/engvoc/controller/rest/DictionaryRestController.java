package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.config.DictionaryConfig;
import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.Views;
import by.sviryd.engvoc.service.CardService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/json/dictionary")
public class DictionaryRestController {
    @Autowired
    private DictionaryConfig dictionaryConfig;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private PictureMediaService pictureMediaService;
    @Autowired
    private CardService cardService;

    @DeleteMapping("{id}")
    public void delete(
            @PathVariable("id") Dictionary dictionary
    ) {
        cardService.deleteByDictionary(dictionary);
        dictionaryService.delete(dictionary);
    }

    @GetMapping("{id}")
    public Dictionary get(@PathVariable("id") Dictionary dictionary) {
        return dictionary;
    }

    @PostMapping()
    public Dictionary save(
            @RequestBody Dictionary dictionary
    ) {
        return dictionaryService.save(dictionary);
    }


    @PutMapping("{id}")
    public Dictionary update(@RequestBody Dictionary dictionary, @PathVariable("id") Dictionary dictionaryDb) {
        BeanUtils.copyProperties(dictionary, dictionaryDb, "id");
        return dictionaryService.save(dictionaryDb);
    }

    @GetMapping("/findAll")
    @JsonView(Views.Dictionary.class)
    public List<Dictionary> findAll() {
        return dictionaryService.findAll();
    }

    @GetMapping("/findDictionariesAndCards")
    @JsonView({Views.DictionaryCard.class})
    public HashMap<Object, Object> findDictionariesAndCards() {
        List<Dictionary> dictionaries = dictionaryService.findAll();
        List<Card> cards = cardService.findAll();
        HashMap<Object, Object> data = new HashMap<>();
        data.put("dictionaries", dictionaries);
        data.put("cards", cards);
        return data;
    }

    @PostMapping("/saveUnique")
    public HashMap<Object, Object> saveUnique(
            @RequestBody Dictionary dictionary
    ) {
        Optional<Dictionary> dictionaryDbOpt = dictionaryService.findByNameAndUnique(dictionary.getName(), dictionary.isUnique());
        Dictionary saved = null;
        HashMap<Object, Object> errors = new HashMap<>();
        if (!dictionaryDbOpt.isPresent()) {
            saved = dictionaryService.save(dictionary);
        } else {
            errors.put("notUniqueDictionaryError", "error");
        }
        HashMap<Object, Object> data = new HashMap<>();
        data.put("saved", saved);
        data.put("errors", errors);
        return data;
    }

    @PostMapping(value = "/saveUniqueWithPicture", consumes = {"multipart/form-data"})
    public HashMap<Object, Object> addWithPicture(
            @RequestPart("file") MultipartFile file,
            @RequestPart("dictionary") String dictionaryJson
    ) throws IOException {
        Dictionary dictionary = new ObjectMapper().readValue(dictionaryJson, Dictionary.class);
        Optional<Dictionary> dictionaryDbOpt = dictionaryService.findByNameAndUnique(dictionary.getName(), dictionary.isUnique());
        Dictionary saved = null;
        HashMap<Object, Object> errors = new HashMap<>();
        if (!dictionaryDbOpt.isPresent()) {
            String picture = pictureMediaService.savePictureOrRollback(file);
            dictionary.setPicture(picture);
            saved = dictionaryService.save(dictionary);
        } else {
            errors.put("notUniqueDictionaryError", "error");
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
        Type arrayOfLongType = new TypeToken<ArrayList<Long>>() {
        }.getType();
        List<Long> indexes = gson.fromJson(idsString, arrayOfLongType);
        if (indexes == null || indexes.isEmpty()) return;
        List<Dictionary> dictionariesDb = dictionaryService.findAllById(indexes);
        if (dictionariesDb.isEmpty()) return;
        cardService.deleteByDictionaryIn(dictionariesDb);
        dictionaryService.deleteByIdIn(dictionariesDb.stream().map(Dictionary::getId).collect(Collectors.toList()));
    }

    @DeleteMapping(value = "/deleteByUnique", consumes = {"application/json"})
    public void deleteByUnique(
            @RequestBody String json
    ) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        boolean unique = obj.get("unique").getAsBoolean();
        dictionaryService.deleteByUnique(unique);
    }
}
