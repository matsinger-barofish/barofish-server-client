package com.matsinger.barofishserver.excel;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
@Component
@RequiredArgsConstructor
public class ExcelService {
    public void checkUploadExcelValid(Row firstRow, List<String> columnName) throws Exception {
        Iterator<Cell> cellIterator = firstRow.cellIterator();
        int i = 0;
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (!cell.getStringCellValue().equals(columnName.get(i))) throw new Exception("엑셀 파일 형식을 확인해주세요.");
            i++;
        }
    }

    public XSSFSheet readExcel(MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);
        return sheet;
    }
}
