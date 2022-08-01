package com.ffmoyano.jwtcrud.repository;

import com.ffmoyano.jwtcrud.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
}
