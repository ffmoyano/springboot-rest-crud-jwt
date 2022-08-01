package com.ffmoyano.idunn.repository;

import com.ffmoyano.idunn.entity.Adventurer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdventurerRepository extends JpaRepository<Adventurer, Long> {

}
