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

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("excel/file")
    @JsonView(Views.CardPage.class)
    public List<Card> excel(
            @RequestBody String json
    ) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        String filename = obj.get("filename").getAsString();
        File file = new File(filename);
        return excelCardShortReaderService.extract(file);
    }
    @PostMapping("xml/file")
    public List<Card> xml(
            @RequestBody String json
    ) throws Exception {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        String filename = obj.get("filename").getAsString();
        File file = new File(filename);
        return xmlCardReaderService.extract(file);
    }
}
