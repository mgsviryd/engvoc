package by.sviryd.engvoc.service.card.writer;

import by.sviryd.engvoc.config.card.io.ExcelCardColumnConfig;
import by.sviryd.engvoc.config.card.io.ExcelCardRowConfig;
import by.sviryd.engvoc.config.card.io.ExcelCardTemplateConfig;
import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.util.StringConverterUtil;
import com.google.common.io.Files;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class ExcelCardFullWriterService {
    @Autowired
    private ExcelCardColumnConfig columnConfig;
    @Autowired
    private ExcelCardRowConfig rowConfig;
    @Autowired
    private ExcelCardTemplateConfig templateConfig;

    public void write(List<Card> cards, File file, String sheetName) throws Exception {
        Files.copy(new File(templateConfig.getFullTemplateFilename()), file);
        try (
                XSSFWorkbook workbook = getWorkBook(file);
                FileOutputStream outputStream = new FileOutputStream(file)
        ) {
            workbook.setSheetName(0, sheetName);
            Sheet sheet = workbook.getSheet(sheetName);
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            style.setAlignment(HorizontalAlignment.LEFT);
            style.setVerticalAlignment(VerticalAlignment.TOP);
            style.setIndention((short) 1);

            int skip = rowConfig.getCountSkipFirstRow();
            for (int i = 0; i < cards.size(); i++) {
                Row row = sheet.createRow(i + skip);
                extractRow(cards, style, i, row);

            }
            workbook.write(outputStream);
        }
    }

    private void extractRow(List<Card> cards, CellStyle style, int i, Row row) {
        Cell cell;

        cell = row.createCell(columnConfig.getWord());
        cell.setCellValue(cards.get(i).getWord());
        cell.setCellStyle(style);

        cell = row.createCell(columnConfig.getTranslation());
        cell.setCellValue(cards.get(i).getTranslation());
        cell.setCellStyle(style);

        cell = row.createCell(columnConfig.getWorkspace());
        cell.setCellStyle(style);

        cell = row.createCell(columnConfig.getExample());
        cell.setCellValue(cards.get(i).getExample());
        cell.setCellStyle(style);

        cell = row.createCell(columnConfig.getExampleTranslation());
        cell.setCellValue(cards.get(i).getExampleTranslation());
        cell.setCellStyle(style);

        cell = row.createCell(columnConfig.getDictionary());
        Dictionary dictionary = cards.get(i).getDictionary();
        if (dictionary == null || dictionary.getId() == null) {
            cell.setCellValue(StringConverterUtil.EMPTY);
        } else {
            cell.setCellValue(dictionary.getId().toString());
        }
        cell.setCellStyle(style);

        cell = row.createCell(columnConfig.getTranscription());
        cell.setCellValue(cards.get(i).getTranscription());
        cell.setCellStyle(style);

        cell = row.createCell(columnConfig.getLearned());
        cell.setCellValue(cards.get(i).isLearned());
        cell.setCellStyle(style);

        cell = row.createCell(columnConfig.getSound());
        cell.setCellValue(cards.get(i).getSound());
        cell.setCellStyle(style);

        cell = row.createCell(columnConfig.getCreationLDT());
        cell.setCellValue(StringConverterUtil.getStringOrEmpty(cards.get(i).getCreationLDT()));
        cell.setCellStyle(style);


        cell = row.createCell(columnConfig.getLearnedLDT());
        cell.setCellValue(StringConverterUtil.getStringOrEmpty(cards.get(i).getLearnedLDT()));
        cell.setCellStyle(style);

        cell = row.createCell(columnConfig.getForgotLDT());
        cell.setCellValue(StringConverterUtil.getStringOrEmpty(cards.get(i).getForgotLDT()));
        cell.setCellStyle(style);

        cell = row.createCell(columnConfig.getCountForgot());
        cell.setCellValue(StringConverterUtil.getStringOrEmpty(cards.get(i).getLearnedLDT()));
        cell.setCellStyle(style);

        cell = row.createCell(columnConfig.getPicture());
        cell.setCellValue(cards.get(i).getPicture());
        cell.setCellStyle(style);

        cell = row.createCell(columnConfig.getInvisible());
        cell.setCellValue(cards.get(i).isInvisible());
        cell.setCellStyle(style);
    }

    public ByteArrayInputStream dateToExcel(List<Card> cards, String sheetName) throws Exception {
        byte[] buf = date(cards, sheetName);
        return new ByteArrayInputStream(buf);
    }

    public byte[] date(List<Card> cards, String sheetName) throws Exception {
        try (
                HSSFWorkbook workbook = new HSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.createSheet(sheetName);
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            style.setAlignment(HorizontalAlignment.LEFT);
            style.setVerticalAlignment(VerticalAlignment.TOP);
            style.setIndention((short) 1);

//            int skip = rowConfig.getCountSkipFirstRow();
            for (int i = 0; i < cards.size(); i++) {
                Row row = sheet.createRow(i);
                extractRow(cards, style, i, row);

            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    @NotNull
    private XSSFWorkbook getWorkBook(File file) throws IOException {
        FileInputStream fip = new FileInputStream(file);
        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(fip);
        } catch (Exception e) {
            fip.close();
            workbook = new XSSFWorkbook();
        }
        return workbook;
    }

}
