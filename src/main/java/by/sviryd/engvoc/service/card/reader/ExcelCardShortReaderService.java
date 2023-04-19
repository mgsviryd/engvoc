package by.sviryd.engvoc.service.card.reader;

import by.sviryd.engvoc.config.card.io.ExcelCardColumnConfig;
import by.sviryd.engvoc.config.card.io.ExcelCardRowConfig;
import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.util.StringUtil;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.EmptyFileException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ExcelCardShortReaderService {
    @Autowired
    private ExcelCardRowConfig rowConfig;
    @Autowired
    private ExcelCardColumnConfig columnConfig;

    public List<Card> extract(File file, String sheetName) {
        try (FileInputStream fip = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fip)) {
            return getCards(sheetName, workbook);
        } catch (Exception e) {
            throw new IllegalArgumentException("Something wrong with " + file.getAbsolutePath());
        }
    }

    public List<Card> extract(MultipartFile file, String sheetName) {
        try (InputStream is = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            return getCards(sheetName, workbook);
        } catch (Exception e) {
            throw new IllegalArgumentException("Something wrong with " + file.getOriginalFilename());
        }
    }

    @NotNull
    private List<Card> getCards(String sheetName, XSSFWorkbook workbook) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet sheet = workbook.getSheetAt(i);
            if (sheet.getSheetName().equals(sheetName)) {
                List<Card> sheetCards = extract(sheet);
                cards.addAll(sheetCards);
                return cards;
            }

        }
        return cards;
    }

    public List<Card> extract(File file) {
        try (FileInputStream is = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            return extract(workbook);
        } catch (Exception e) {
            throw new IllegalArgumentException("Something wrong with " + file.getAbsolutePath());
        }
    }

    public List<Card> extract(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            return extract(workbook);
        } catch (EmptyFileException e) {
            return Collections.emptyList();
        } catch (Exception e) {
            throw new IllegalArgumentException("Something wrong with " + file.getOriginalFilename());
        }
    }

    public List<Card> extract(XSSFWorkbook workbook) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet sheet = workbook.getSheetAt(i);
            List<Card> sheetCards = extract(sheet);
            cards.addAll(sheetCards);
        }
        return cards;
    }

    public List<Card> extract(XSSFSheet sheet) {
        Dictionary dictionary = new Dictionary(sheet.getSheetName());
        List<Card> cards = new ArrayList<>();
        for (int i = sheet.getFirstRowNum() + rowConfig.getCountSkipFirstRow(); i < sheet.getLastRowNum() + 1; i++) {
            XSSFRow row = sheet.getRow(i);
            if (row == null) continue;
            XSSFCell cell = row.getCell(0);
            if (cell == null) continue;
            Card card;
            try {
                card = getCard(row, dictionary);
            } catch (Exception e) {
                continue;
            }
            if (card.getWord() == null
                    || card.getWord().isEmpty()
                    || card.getTranslation() == null
                    || card.getTranslation().isEmpty()) {
                continue;
            }
            if (isCardNecessaryFieldsAbsent(card)) continue;
            cards.add(card);
        }
        return cards;
    }
    private boolean isCardNecessaryFieldsAbsent(Card card) {
        return StringUtils.isEmpty(card.getWord()) || StringUtils.isEmpty(card.getTranslation());
    }


    private Card getCard(XSSFRow row, Dictionary dictionary) {
        return new Card().builder()
                .word(getCellOrException(row, columnConfig.getWord()).trim())
                .translation(getCellOrException(row, columnConfig.getTranslation()).trim())
                .example(getCellOrEmptyValue(row, columnConfig.getExample()).trim())
                .exampleTranslation(getCellOrEmptyValue(row, columnConfig.getExampleTranslation()).trim())
                .dictionary(dictionary)
                .build();
    }

    private String getCellOrEmptyValue(XSSFRow row, int index) {
        try {
            return row.getCell(index).getStringCellValue();
        } catch (Exception e) {
            return Strings.EMPTY;
        }
    }

    private String getCellOrException(XSSFRow row, int index) {
        return row.getCell(index).getStringCellValue();
    }
}
