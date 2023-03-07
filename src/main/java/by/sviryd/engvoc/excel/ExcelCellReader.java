package by.sviryd.engvoc.excel;


import com.google.common.collect.Streams;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class ExcelCellReader {
    public Stream<URL> getURLs(
            int iRowStart,
            int iRowEnd,
            int iColumn,
            String pathName
    ) throws IOException {
        evaluateArgs(iRowStart, iRowEnd);

        FileInputStream inputStream = new FileInputStream(new File(pathName));
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        HSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        for (int i = 0; i < iRowStart; i++) {
            rowIterator.next();
        }
        return Streams.stream(rowIterator)
                .skip(iRowStart)
                .map(getRowURLFunction(iColumn))
                .filter(Objects::nonNull);
    }

    private Function<Row, URL> getRowURLFunction(int iColumn) {
        return r-> {
            Cell c = r.getCell(iColumn);
            if (c == null) return null;
            try{
                return new URL(c.getStringCellValue().trim());
            }catch (Exception e){
                return null;
            }
        };
    }

    private void evaluateArgs(int iRowStart, int iRowEnd) {
        if (iRowStart >= iRowEnd)
            throw new IllegalArgumentException(
                    "iRowStart: " + iRowStart + " cannot be more iRowEnd " + iRowEnd + "."
            );
        if (iRowStart < 0 || iRowEnd < 0) {
            throw new IllegalArgumentException(
                    "iRow cannot be less than 0. iRowStart: " + iRowStart + " iRowEnd: " + iRowEnd + "."
            );
        }
    }
}

