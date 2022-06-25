package com.ffmoyano.idunn.repository;

import com.ffmoyano.idunn.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByEmail(String email);

}
