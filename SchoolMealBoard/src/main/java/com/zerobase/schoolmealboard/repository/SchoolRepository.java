package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.School;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, String> {

  Optional<School> findBySchoolName(String schoolName);

}
