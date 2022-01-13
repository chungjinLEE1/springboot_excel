package org.example.sample;

import org.example.sample.test.utils.SHA256;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.security.NoSuchAlgorithmException;

// DB 접속을 하지 않는 Application 설정
//https://www.baeldung.com/spring-data-disable-auto-config
//(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class SampleTestApplication {

    public static void main(String[] args) throws NoSuchAlgorithmException {

        SpringApplication.run(SampleTestApplication.class, args);


        SHA256 sha256 = new SHA256();

        String password = "hi12345678";

        String cryptogram = sha256.encrypt(password);

        System.out.println(cryptogram);

        System.out.println(cryptogram.equals(sha256.encrypt(password)));


    }

}
