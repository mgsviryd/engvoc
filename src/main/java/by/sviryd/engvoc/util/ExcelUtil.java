package by.sviryd.engvoc.util;


import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ExcelUtil {
    public String getSheetName(XSSFWorkbook workbook, int index) {
        XSSFSheet sheet = workbook.getSheetAt(index);
        return sheet.getSheetName();
    }
    public List<String> getSheetNames(XSSFWorkbook workbook){
        return IntStream.range(0, workbook.getNumberOfSheets()).mapToObj(workbook::getSheetName).collect(Collectors.toList());
    }
}
