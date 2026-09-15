package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.entity.Property;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ExcelReportGenerator {

    public byte[] generateExcel(Property property) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Sheet 1: Summary
            Sheet sheet1 = workbook.createSheet("Summary");
            Row r0 = sheet1.createRow(0);
            r0.createCell(0).setCellValue("Property Address");
            r0.createCell(1).setCellValue(property.getAddress());

            // Sheet 2: Risk Assessment
            Sheet sheet2 = workbook.createSheet("Risk Assessment");
            Row rRisk = sheet2.createRow(0);
            rRisk.createCell(0).setCellValue("Category");
            rRisk.createCell(1).setCellValue("Score");

            // Sheet 3: Comparable Listings
            Sheet sheet3 = workbook.createSheet("Comparable Listings");
            sheet3.createRow(0).createCell(0).setCellValue("Comparable Address");

            // Sheet 4: Timeline
            Sheet sheet4 = workbook.createSheet("Timeline");
            sheet4.createRow(0).createCell(0).setCellValue("Event Date");

            workbook.write(out);
            return out.toByteArray();
        }
    }
}