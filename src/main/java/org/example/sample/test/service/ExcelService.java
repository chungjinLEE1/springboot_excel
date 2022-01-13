package org.example.sample.test.service;

import javassist.tools.rmi.Sample;
import lombok.extern.slf4j.Slf4j;
import org.example.sample.test.helper.ExcelHelper;
import org.example.sample.test.model.SampleExcel;
import org.example.sample.test.repository.SampleRepository;
import org.example.sample.test.utils.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class ExcelService {

    @Autowired
    SampleRepository sampleRepository;

    public void save(MultipartFile file){
        log.info("serivce > save ###");
        try {
            List<SampleExcel> samples = ExcelHelper.excelSample(file.getInputStream());
            sampleRepository.saveAll(samples);

        }catch(IOException e){
            throw new RuntimeException("fail to store excel data : " + e.getMessage());
        }
    }

    public List<SampleExcel> getAllSamples(){
        return sampleRepository.findAll();
    }


    // Store File Data to Database
    public void store(MultipartFile file){
        log.info("service > store");
        try {
            List<SampleExcel> lstCustomers = ExcelUtils.parseExcelFile(file.getInputStream());
            // Save Customers to DataBase
            sampleRepository.saveAll(lstCustomers);
        } catch (IOException e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }

    // Load Data to Excel File
    public ByteArrayInputStream loadFile() {
        log.info("service > loadFile");

        List<SampleExcel> samples = (List<SampleExcel>) sampleRepository.findAll();

        try {
            ByteArrayInputStream in = ExcelUtils.samplesToExcel(samples);
            return in;
        } catch (IOException e) {}

        return null;
    }


}
