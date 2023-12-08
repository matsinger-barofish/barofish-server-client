package com.matsinger.barofishserver.domain.store.application;

import com.matsinger.barofishserver.domain.store.dto.StoreExcelInquiryDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreExcelQueryService {

    public Workbook makeExcelForm(List<StoreExcelInquiryDto> excelDtos) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        int rowNum = 0;
        Row row = sheet.createRow(rowNum++);

        // 셀 스타일 지정
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.YELLOW1.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Header
        List<String> headers = List.of(
                "입점사명", "입점사ID", "판매상태",
                "위치", "키워드", "정산비율",
                "은행명", "예금주", "계좌번호",
                "대표자이름", "사업자번호", "업태/종목", "통신판매신고번호",
                "사업장주소", "우편번호", "지번", "도로명",
                "상세주소", "전화번호", "이메일", "팩스번호");
        Cell cell;
        for (int i = 0; i < 21; i++) {
            cell = row.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(cellStyle);
        }

        // body
        for (StoreExcelInquiryDto excelDto : excelDtos) {
            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(excelDto.getStoreName() != null ? excelDto.getStoreName() : null);
            row.createCell(1).setCellValue(excelDto.getLoginId() != null ? excelDto.getLoginId() : null);
            row.createCell(2).setCellValue(excelDto.getState() != null ? excelDto.getState().toString() : null);
            row.createCell(3).setCellValue(excelDto.getLocation() != null ? excelDto.getLocation() : null);
            row.createCell(4).setCellValue(excelDto.getKeyword() != null ? excelDto.getKeyword() : null);
            row.createCell(5).setCellValue(excelDto.getSettlementRate() != null ? excelDto.getSettlementRate() : null);
            row.createCell(6).setCellValue(excelDto.getBankName() != null ? excelDto.getBankName() : null);
            row.createCell(7).setCellValue(excelDto.getBankHolder() != null ? excelDto.getBankHolder() : null);
            row.createCell(8).setCellValue(excelDto.getBankAccount() != null ? excelDto.getBankAccount() : null);
            row.createCell(9).setCellValue(excelDto.getRepresentativeName() != null ? excelDto.getRepresentativeName() : null);
            row.createCell(10).setCellValue(excelDto.getCompanyId() != null ? excelDto.getCompanyId() : null);
            row.createCell(11).setCellValue(excelDto.getBusinessType() != null ? excelDto.getBusinessType() : null);
            row.createCell(12).setCellValue(excelDto.getMosRegistrationNumber() != null ? excelDto.getMosRegistrationNumber() : null);
            row.createCell(13).setCellValue(excelDto.getBusinessAddress() != null ? excelDto.getBusinessAddress() : null);
            row.createCell(14).setCellValue(excelDto.getPostalCode() != null ? excelDto.getPostalCode() : null);
            row.createCell(15).setCellValue(excelDto.getLotNumberAddress() != null ? excelDto.getLotNumberAddress() : null);
            row.createCell(16).setCellValue(excelDto.getStreetNameAddress() != null ? excelDto.getStreetNameAddress() : null);
            row.createCell(17).setCellValue(excelDto.getAddressDetail() != null ? excelDto.getAddressDetail() : null);
            row.createCell(18).setCellValue(excelDto.getPhoneNumber() != null ? excelDto.getPhoneNumber() : null);
            row.createCell(19).setCellValue(excelDto.getEmail() != null ? excelDto.getEmail() : null);
            row.createCell(20).setCellValue(excelDto.getFaxNumber() != null ? excelDto.getFaxNumber() : null);
        }

        return workbook;
    }
}
