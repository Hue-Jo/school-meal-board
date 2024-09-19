package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.Review;
import com.zerobase.schoolmealboard.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  Page<Review> findAllByUser(User user, Pageable pageable);

}
