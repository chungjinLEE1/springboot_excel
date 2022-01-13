package org.example.sample.test.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.sample.test.model.SampleExcel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtils {

    public static ByteArrayInputStream samplesToExcel(List<SampleExcel> samples) throws IOException {
        String[] COLUMNs = {"Id", "Title", "Description", "Published"};
        try(
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
        ){
            CreationHelper createHelper = workbook.getCreationHelper();

            Sheet sheet = workbook.createSheet("Samples");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLUE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Row for Header
            Row headerRow = sheet.createRow(0);

            // Header
            for (int col = 0; col < COLUMNs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(COLUMNs[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // CellStyle for Age
            CellStyle ageCellStyle = workbook.createCellStyle();
            ageCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#"));

            int rowIdx = 1;
            for (SampleExcel sample : samples) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(sample.getId());
                row.createCell(1).setCellValue(sample.getTitle());
                row.createCell(2).setCellValue(sample.getDescription());
                row.createCell(3).setCellValue(sample.isPublished());

                Cell ageCell = row.createCell(3);
                ageCell.setCellValue(sample.getDescription());
                ageCell.setCellStyle(ageCellStyle);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public static List<SampleExcel> parseExcelFile(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet("Samples");
            Iterator<Row> rows = sheet.iterator();

            List<SampleExcel> lstSampleExcel = new ArrayList<SampleExcel>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if(rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                SampleExcel samp = new SampleExcel();

                int cellIndex = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    if(cellIndex==0) { // ID
                        samp.setId((long) currentCell.getNumericCellValue());
                    } else if(cellIndex==1) { // Desc
                        samp.setDescription(currentCell.getStringCellValue());
                    } else if(cellIndex==2) { // Title
                        samp.setTitle(currentCell.getStringCellValue());
                    } else if(cellIndex==3) { // published
                        samp.setPublished(currentCell.getBooleanCellValue());
                    }

                    cellIndex++;
                }

                lstSampleExcel.add(samp);
            }

            // Close WorkBook
            workbook.close();

            return lstSampleExcel;
        } catch (IOException e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }

    }
}


