package com.matsinger.barofishserver.excel.application;

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
    public void checkUploadExcelValid(Row firstRow, List<String> columnName) {
        Iterator<Cell> cellIterator = firstRow.cellIterator();
        int i = 0;
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (!cell.getStringCellValue().equals(columnName.get(i))) throw new IllegalArgumentException("엑셀 파일 형식을 확인해주세요.");
            i++;
        }
    }

    public XSSFSheet readExcel(MultipartFile file) {
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            throw new IllegalArgumentException("엑셀 파일을 읽는데 실패했습니다.");
        }
        XSSFSheet sheet = workbook.getSheetAt(0);
        return sheet;
    }
}
