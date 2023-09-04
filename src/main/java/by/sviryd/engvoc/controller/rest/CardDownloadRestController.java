package by.sviryd.engvoc.controller.rest;

import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.service.CardService;
import by.sviryd.engvoc.service.card.writer.ExcelCardFullWriterService;
import by.sviryd.engvoc.service.card.writer.XmlCardWriterService;
import by.sviryd.engvoc.type.LangLocale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/json/card")
public class CardDownloadRestController {
    @Autowired
    private ExcelCardFullWriterService excelCardFullWriterService;
    @Autowired
    private XmlCardWriterService xmlCardWriterService;
    @Autowired
    private CardService cardService;

    @GetMapping("/download/excel")
    public ResponseEntity<Resource> excel(
            @RequestParam("dictionaryId") Dictionary dictionary
    ) throws Exception {
        List<Card> cards = cardService.getCardsByDictionary(dictionary);
        ByteArrayInputStream inp = excelCardFullWriterService.dateToExcel(cards, dictionary.getName());
        InputStreamResource resource = new InputStreamResource(inp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + dictionary.getName() + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    @GetMapping("/download/xml")
    public ResponseEntity<Resource> xml(
            @RequestParam("dictionaryId") Dictionary dictionary
    ) throws Exception {
        List<Card> cards = cardService.getCardsByDictionary(dictionary);
        ByteArrayInputStream inp = xmlCardWriterService.dateToXml(cards, dictionary.getName(), LangLocale.getCapitalizeLangCouple(dictionary.getSource(), dictionary.getDestin()));
        InputStreamResource resource = new InputStreamResource(inp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + dictionary.getName() + ".xlsx")
                .contentType(MediaType.APPLICATION_XML)
                .body(resource);
    }
}
