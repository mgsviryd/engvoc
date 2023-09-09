package by.sviryd.engvoc.service.card.reader;

import by.sviryd.engvoc.config.card.io.ExcelCardColumnConfig;
import by.sviryd.engvoc.config.card.io.ExcelCardRowConfig;
import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.util.StringConverterUtil;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ExcelCardFullReaderService {
    @Autowired
    private ExcelCardRowConfig rowConfig;
    @Autowired
    private ExcelCardColumnConfig columnConfig;

    public List<Card> extract(File file, String sheetName) {
        try (FileInputStream fip = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fip)) {
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
        } catch (Exception e) {
            throw new IllegalArgumentException("Something wrong with " + file.getAbsolutePath());
        }
    }

    public List<Card> extract(File file) {
        try (FileInputStream fip = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fip)) {
            return extract(workbook);
        } catch (Exception e) {
            throw new IllegalArgumentException("Something wrong with " + file.getAbsolutePath());
        }
    }

    public List<Card> extract(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            return extract(workbook);
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
        List<Card> cards = new ArrayList<>();
        for (int i = sheet.getFirstRowNum() + rowConfig.getCountSkipFirstRow(); i < sheet.getLastRowNum() + 1; i++) {
            XSSFRow row = sheet.getRow(i);
            if (row == null) continue;
            XSSFCell cell = row.getCell(0);
            if (cell == null) continue;
            Card card;
            try {
                card = getCard(row);
            } catch (Exception e) {
                continue;
            }
            if (isCardNecessaryFieldsAbsent(card)) continue;
            cards.add(card);
        }
        return cards;
    }

    private boolean isCardNecessaryFieldsAbsent(Card card) {
        return card.getWord() == null
                || card.getWord().isEmpty()
                || card.getTranslation() == null
                || card.getTranslation().isEmpty();
    }

    public Card getCard(XSSFRow row) {
        return new Card().builder()
                .word(getCellOrException(row, columnConfig.getWord()).trim())
                .translation(getCellOrException(row, columnConfig.getTranslation()).trim())
                .example(getCellOrEmptyValue(row, columnConfig.getExample()).trim())
                .exampleTranslation(getCellOrEmptyValue(row, columnConfig.getExampleTranslation()).trim())
                .dictionary(new Dictionary(UUID.fromString(getCellOrEmptyValue(row, columnConfig.getDictionary()))))
                .transcription(getCellOrEmptyValue(row, columnConfig.getTranscription()).trim())
                .learned(StringConverterUtil.getBoolean(getCellOrEmptyValue(row, columnConfig.getLearned())))
                .sound(getCellOrEmptyValue(row, columnConfig.getSound()).trim())
                .creationLDT(StringConverterUtil.getLDTOrNull(getCellOrEmptyValue(row, columnConfig.getCreationLDT())))
                .learnedLDT(StringConverterUtil.getLDTOrNull(getCellOrEmptyValue(row, columnConfig.getLearnedLDT())))
                .forgotLDT(StringConverterUtil.getLDTOrNull(getCellOrEmptyValue(row, columnConfig.getForgotLDT())))
                .countForgot(StringConverterUtil.getIntegerOrNull(getCellOrEmptyValue(row, columnConfig.getCountForgot())))
                .picture(getCellOrEmptyValue(row, columnConfig.getPicture()).trim())
                .invisible(StringConverterUtil.getBoolean(getCellOrEmptyValue(row, columnConfig.getInvisible())))
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
