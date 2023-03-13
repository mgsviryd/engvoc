package by.sviryd.engvoc.service.card.writer;

import by.sviryd.engvoc.config.card.io.ExcelCardColumnConfig;
import by.sviryd.engvoc.config.card.io.ExcelCardRowConfig;
import by.sviryd.engvoc.config.card.io.ExcelCardTemplateConfig;
import by.sviryd.engvoc.domain.Card;
import by.sviryd.engvoc.domain.Dictionary;
import by.sviryd.engvoc.util.StringConverterUtil;
import com.google.common.io.Files;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        FileInputStream fip = new FileInputStream(file);
        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(fip);
        } catch (Exception e) {
            fip.close();
            workbook = new XSSFWorkbook();
        }
        workbook.setSheetName(0, sheetName);
        Sheet sheet = workbook.getSheet(sheetName);
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setIndention((short)1);

        for (int i = rowConfig.getCountSkipFirstRow(); i < cards.size(); i++) {
            Row row = sheet.createRow(i);
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
            if (dictionary == null) {
                cell.setCellValue(StringConverterUtil.EMPTY);
            } else {
                cell.setCellValue(dictionary.getName());
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
        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        workbook.close();
    }

}
