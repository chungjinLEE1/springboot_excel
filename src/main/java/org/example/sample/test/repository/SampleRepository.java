package org.example.sample.test.repository;

import org.example.sample.test.model.SampleExcel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleRepository extends JpaRepository<SampleExcel, Long> {

}
