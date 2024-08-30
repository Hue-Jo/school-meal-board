package com.zerobase.schoolmealboard.repository;

import com.zerobase.schoolmealboard.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByPhoneNum(String phoneNum);
  Optional<User> findByEmail(String email);
  Optional<User> findByNickName(String nickname);
}
