package com.matsinger.barofishserver.settlement.api;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.test.context.ActiveProfiles;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest
class SettlementControllerTest {

    @DisplayName("엑셀 테스트용")
    @Test
    void excelTest() {

        XSSFWorkbook workbook = new XSSFWorkbook();

        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Sheet sheet = workbook.createSheet(today + "_정산목록");

        String bodyDatass[][] = new String[][]{
                {"첫번째 행 첫번째 데이터", "첫번째 행 두번째 데이터", "첫번째 행 세번째 데이터"},
                {"두번째 행 첫번째 데이터", "두번째 행 두번째 데이터", "두번째 행 세번째 데이터"},
                {"세번째 행 첫번째 데이터", "세번째 행 두번째 데이터", "세번째 행 세번째 데이터"},
                {"네번째 행 첫번째 데이터", "네번째 행 두번째 데이터", "네번째 행 세번째 데이터"}
        };

        int rowCount = 0;

        Row bodyRow = null;
        Cell bodyCell = null;

        for (String[] bodyDatas : bodyDatass) {
            bodyRow = sheet.createRow(rowCount++);
            System.out.println("bodyDatas = " + bodyDatas.toString());
            System.out.println("rowCount = " + rowCount);
        }
        System.out.println("endedRowCount = " + rowCount);
    }
}