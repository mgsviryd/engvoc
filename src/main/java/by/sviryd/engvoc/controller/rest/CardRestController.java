package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.domain.Views;
import by.sviryd.engvoc.service.CardService;
import by.sviryd.engvoc.service.card.reader.ExcelCardShortReaderService;
import by.sviryd.engvoc.service.card.reader.XmlCardReaderService;
import by.sviryd.engvoc.util.ObjectToViewTransformUtil;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/json/card")
public class CardRestController {
    @Autowired
    private CardService cardService;
    @Autowired
    private ExcelCardShortReaderService excelCardShortReaderService;
    @Autowired
    private XmlCardReaderService xmlCardReaderService;


    @PostMapping(value = "/db/ids")
    @JsonView(Views.CardPage.class)
    public List<Card> propertyChoices(
            @RequestBody String json
    ) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        JsonArray idsString = obj.get("ids").getAsJsonArray();
        Type arrayOfLongType = new TypeToken<ArrayList<Long>>() {
        }.getType();
        List<Long> indexes = gson.fromJson(idsString, arrayOfLongType);
        return cardService.findAllById(indexes);
    }

    @GetMapping("db/{id}")
    public String findById(
            @PathVariable("id") Card card
    ) {
        String description = card.getWord(); // remove lazy
        card = ObjectToViewTransformUtil.transformToView(card, Views.CardPage.class);
        Gson gson = new GsonBuilder().create();
        return gson.toJson(card);
    }
    @GetMapping("db/dictionary/{id}")
    @JsonView(Views.CardPage.class)
    public List<Card> findByDictionary(
            @PathVariable("id") Dictionary dictionary
    ) {
        String name = dictionary.getName(); // remove lazy
        return cardService.getCardsByDictionary(dictionary);
    }

    @PostMapping("upload/excel/filename")
    public List<Card> excelFilename(
            @RequestBody String json
    ) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        String filename = obj.get("filename").getAsString();
        File file = new File(filename);
        return excelCardShortReaderService.extract(file);
    }
    @PostMapping("upload/excel/file")
    public List<Card> excelFile(
            @RequestParam("file") MultipartFile file
    ) {
        return excelCardShortReaderService.extract(file);
    }

    @PostMapping("upload/excel/files")
    public List<Card> excelFiles(
            @RequestParam("files") MultipartFile [] files
    ) {
        return Arrays.stream(files)
                .map(excelCardShortReaderService::extract)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
    @PostMapping("upload/xml/filename")
    public List<Card> xml(
            @RequestBody String json
    ) throws Exception {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        String filename = obj.get("filename").getAsString();
        File file = new File(filename);
        return xmlCardReaderService.extract(file);
    }
    @PostMapping("upload/xml/file")
    public List<Card> xmlFile(
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        return xmlCardReaderService.extract(file);
    }
    @PostMapping("upload/xml/files")
    public List<Card> xmlFiles(
            @RequestParam("files") MultipartFile [] files
    ) throws Exception {
        return Arrays.stream(files)
                .map(xmlCardReaderService::extract)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
