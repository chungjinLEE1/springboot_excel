package org.example.sample.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.sample.test.helper.ExcelHelper;
import org.example.sample.test.message.ResponseMessage;
import org.example.sample.test.model.SampleExcel;
import org.example.sample.test.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    private final String UPLOAD_DIR = "./uploads";

    @GetMapping("/excel")
    public String excel(){
        log.info("excel###");
        return "excel";
    }

    @GetMapping("/uploadform")
    public String uploadform(){
        log.info("uploadform###");
        return "multipartfile/uploadform";
    }


   // Excel download REST API
    @GetMapping(path = "/excel/download", produces = "application/vnd.ms-excel")
    public void downloadExcel(HttpServletResponse response) throws IOException {

        log.info("downloadExcel");
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("첫번째 시트");
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        // Header
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("번호");
        cell = row.createCell(1);
        cell.setCellValue("이름");
        cell = row.createCell(2);
        cell.setCellValue("제목");

        // Body
        for (int i=0; i<3; i++) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(i);
            cell = row.createCell(1);
            cell.setCellValue(i+"_name");
            cell = row.createCell(2);
            cell.setCellValue(i+"_title");
        }

        // 컨텐츠 타입과 파일명 지정.
        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=example.xlsx");

        // Excel File Output
        wb.write(response.getOutputStream());
        wb.close();
    }


    @PostMapping("/excel/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file){
        log.info("### uploadFile");
        String message = "";

        if(ExcelHelper.hasExcelFormat(file)){
            try{
                excelService.save(file);
                message = "Upload the file successfully " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            }catch(Exception e){
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }
        message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }


    @GetMapping("/excel/sampleList")
    public ResponseEntity<List<SampleExcel>> getAllSamples(Model model) {
        System.out.println("### getAllSamples");
        try {
            List<SampleExcel> tutorials = excelService.getAllSamples();


            System.out.println(tutorials);
            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
     * Download Files
     */
    @GetMapping("/excel/file")
    public ResponseEntity<InputStreamResource> downloadFile() {
        log.info("downloadFile###");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=samples.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(excelService.loadFile()));
    }

    /*
     * Upload Files
     */
    @RequestMapping(value = "/excel/upload", method = {RequestMethod.GET, RequestMethod.POST})
    public String uploadMultipartFile(@RequestParam("uploadfile") MultipartFile file, Model model, RedirectAttributes attributes) {
        log.info("uploadMultipartFile###");

        // Check if file is empty
        if(file.isEmpty()){
            attributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/";
        }


        // save the file on the local file system
        try {
            excelService.store(file);
            model.addAttribute("message", "File uploaded successfully!");
        } catch (Exception e) {
            model.addAttribute("message", "Fail! -> uploaded filename: " + file.getOriginalFilename());
        }
        return "multipartfile/uploadform";
    }





}
