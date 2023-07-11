package com.matsinger.barofishserver.excel;

import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.utils.Common;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductExcelService {
    @Value("${cloud.aws.s3.imageUrl}")
    private String s3Url;
    private final Common utils;
    private final ExcelService excelService;

    public void convertRow2Product(Row row) {
        // 0 입점사 ID
        String partnerId = row.getCell(0).getStringCellValue();
        // 1 1차 카테고리
        String parentCategory = row.getCell(1).getStringCellValue();
        // 2 2차 카테고리
        String category = row.getCell(2).getStringCellValue();
        // 3 상품명
        String productName = row.getCell(3).getStringCellValue();
        // 4 도착 예정일
        // 5 발송안내
        // 6 배송비
        // 7 택배 책정  수량
        // 8 노출 상태 (노출 | 미노출)
        // 9 과세여부 (과세 | 비과세)
        // 10 옵션 여부
        // 11 매입가
        // 12 대표 옵션 번호
        // 13 옵션명
        // 14 옵션 정가
        // 15 옵션 할인가
        // 16 최대 주문 수량
        // 17 재고
        // 18 적립금 지급
    }

    public void processProductExcel(MultipartFile file) throws Exception {
        XSSFSheet sheet = excelService.readExcel(file);
        Iterator<Row> rowIterator = sheet.iterator();
        Row firstRow = rowIterator.next();
        List<String>
                columnName =
                List.of("입점사ID",
                        "1차 카테고리",
                        "2차 카테고리",
                        "상품명",
                        "도착 예정일",
                        "발송안내",
                        "배송비",
                        "택배 책정 수량",
                        "노출상태",
                        "과세여부",
                        "옵션여부",
                        "매입가",
                        "대표옵션 번호",
                        "옵션명",
                        "옵션 정가",
                        "옵션 할인가",
                        "최대 주문 수량",
                        "재고",
                        "적립금지급");
        excelService.checkUploadExcelValid(firstRow, columnName);

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String value = row.getCell(11).getStringCellValue();
            System.out.println(value.split("\n").length);
            System.out.println("----------------------------");
        }
    }
}
