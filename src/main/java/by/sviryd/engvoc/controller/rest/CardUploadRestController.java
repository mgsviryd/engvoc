package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.service.CardUploadService;
import by.sviryd.engvoc.service.card.reader.ExcelCardShortReaderService;
import by.sviryd.engvoc.service.card.reader.XmlCardReaderService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/json/card")
public class CardUploadRestController {
    @Autowired
    private ExcelCardShortReaderService excelCardShortReaderService;
    @Autowired
    private XmlCardReaderService xmlCardReaderService;
    @Autowired
    private CardUploadService cardUploadService;

    @PostMapping("upload/excel/filename")
    public HashMap<Object, Object> excelFilename(
            @RequestBody String json
    ) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        String filename = obj.get("filename").getAsString();
        File file = new File(filename);
        List<Card> extract = excelCardShortReaderService.extract(file);
        return cardUploadService.saveNewDictionariesAndCardsBatch(extract);
    }

    @PostMapping("upload/excel/file")
    public HashMap<Object, Object> excelFile(
            @RequestParam("file") MultipartFile file
    ) {
        List<Card> extract = excelCardShortReaderService.extract(file);
        return cardUploadService.saveNewDictionariesAndCardsBatch(extract);
    }

    @PostMapping("upload/excel/files")
    public HashMap<Object, Object> excelFiles(
            @RequestParam("files") MultipartFile[] files
    ) {
        List<Card> extract = Arrays.stream(files)
                .map(excelCardShortReaderService::extract)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return cardUploadService.saveNewDictionariesAndCardsBatch(extract);
    }

    @PostMapping("upload/xml/filename")
    public HashMap<Object, Object> xml(
            @RequestBody String json
    ) throws Exception {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        String filename = obj.get("filename").getAsString();
        File file = new File(filename);
        List<Card> extract = xmlCardReaderService.extract(file);
        return cardUploadService.saveNewDictionariesAndCardsBatch(extract);
    }

    @PostMapping("upload/xml/file")
    public HashMap<Object, Object> xmlFile(
            @RequestParam("file") MultipartFile file
    ) {
        List<Card> extract = xmlCardReaderService.extract(file);
        return cardUploadService.saveNewDictionariesAndCardsBatch(extract);
    }

    @PostMapping("upload/xml/files")
    public HashMap<Object, Object> xmlFiles(
            @RequestParam("files") MultipartFile[] files
    ) {
        List<Card> extract = Arrays.stream(files)
                .map(xmlCardReaderService::extract)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return cardUploadService.saveNewDictionariesAndCardsBatch(extract);
    }
}
