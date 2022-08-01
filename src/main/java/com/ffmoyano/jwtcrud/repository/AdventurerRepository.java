package com.ffmoyano.jwtcrud.repository;

import com.ffmoyano.jwtcrud.entity.Adventurer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdventurerRepository extends JpaRepository<Adventurer, Long> {

}
