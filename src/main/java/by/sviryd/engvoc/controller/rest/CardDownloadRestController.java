package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.service.CardService;
import by.sviryd.engvoc.service.card.writer.ExcelCardFullWriterService;
import by.sviryd.engvoc.service.card.writer.XmlCardWriterService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/json/card")
public class CardDownloadRestController {
    @Autowired
    private ExcelCardFullWriterService excelCardFullWriterService;
    @Autowired
    private XmlCardWriterService xmlCardWriterService;
    @Autowired
    private CardService cardService;

    // TODO: excel file is not opened correctly
    @GetMapping("/download/excel")
    public ResponseEntity<Resource> excel(
            @RequestParam("dictionaryId") Dictionary dictionary
    ) throws Exception {
        List<Card> cards = cardService.findAllByDictionary(dictionary);
        String pair = dictionary.getVocabulary().getCapitalizeLangPair();
        ByteArrayInputStream inp = excelCardFullWriterService.dateToExcel(cards, dictionary.getName() + pair);
        InputStreamResource resource = new InputStreamResource(inp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + dictionary.getName() + pair + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @GetMapping("/download/xml")
    public ResponseEntity<Resource> xml(
            @RequestParam("dictionaryId") Dictionary dictionary
    ) throws Exception {
        List<Card> cards = cardService.findAllByDictionary(dictionary);
        ByteArrayInputStream inp = xmlCardWriterService.dataToXml(cards, dictionary);
        InputStreamResource resource = new InputStreamResource(inp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + dictionary.getName() + dictionary.getVocabulary().getCapitalizeLangPair() + ".xml")
                .contentType(MediaType.APPLICATION_XML)
                .body(resource);
    }

//    TODO: zip file is not opened correctly
    @PostMapping(value = "/download/xmls")
    public ResponseEntity<Resource> xmls(
            @RequestBody String json
    ) throws Exception {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray idsString = parser.parse(json).getAsJsonArray();
        Type arrayOfLongType = new TypeToken<ArrayList<UUID>>() {
        }.getType();
        List<UUID> ids = gson.fromJson(idsString, arrayOfLongType);
        List<Card> cards = cardService.getCardsByDictionaryIdIn(ids);
        Map<Dictionary, List<Card>> collect = cards.stream().collect(Collectors.groupingBy(Card::getDictionary));
        Set<Dictionary> dictionaries = collect.keySet();
        byte [] zipData;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)
        ) {
            for (Dictionary dictionary : dictionaries) {
                ByteArrayInputStream is = xmlCardWriterService.dataToXml(collect.get(dictionary), dictionary);
                String name = dictionary.getName() + dictionary.getVocabulary().getCapitalizeLangPair();
                String ext = ".xml";
                String filename = name + ext;
                ZipEntry entry = new ZipEntry(filename);
                zos.putNextEntry(entry);
                IOUtils.copy(is, zos);
                is.close();
                zos.closeEntry();
            }
            zipData = baos.toByteArray();
        }
        ByteArrayInputStream inp = new ByteArrayInputStream(zipData);
        InputStreamResource resource = new InputStreamResource(inp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "dictionaries" + ".zip")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);
    }
}
