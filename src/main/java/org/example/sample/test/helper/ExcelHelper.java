package org.example.sample.test.helper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.sample.test.model.SampleExcel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {
    public static String TYPE =  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {"Id", "Title", "Description", "Published"};
    static String SHEET = "sample";

    public static boolean hasExcelFormat(MultipartFile file){
        if(!TYPE.equals(file.getContentType())){
            return false;
        }
        return true;
    }

    public static List<SampleExcel> excelSample(InputStream is){
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<SampleExcel> samples = new ArrayList<SampleExcel>();

            int rowNumber  = 0;
            while(rows.hasNext()){
                Row currentRow = rows.next();

                //Skip header
                if(rowNumber == 0){
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();

                SampleExcel sample = new SampleExcel();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            sample.setId((long) currentCell.getNumericCellValue());
                            break;

                        case 1:
                            sample.setTitle(currentCell.getStringCellValue());
                            break;

                        case 2:
                            sample.setDescription(currentCell.getStringCellValue());
                            break;

                        case 3:
                            sample.setPublished(currentCell.getBooleanCellValue());
                            break;

                        default:
                            break;
                    }

                    cellIdx++;
                }
                samples.add(sample);
            }
            workbook.close();

            return samples;
        }catch(IOException e){
            throw new RuntimeException("fail to parse Excel file : " + e.getMessage());
        }

    }


}
