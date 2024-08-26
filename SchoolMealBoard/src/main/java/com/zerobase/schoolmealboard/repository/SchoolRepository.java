package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, String> {

}
