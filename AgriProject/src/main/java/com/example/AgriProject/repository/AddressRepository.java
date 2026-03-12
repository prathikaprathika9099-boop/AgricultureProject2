package com.example.AgriProject.repository;

import com.example.AgriProject.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address,Long> {
    List<Address> findByUserId(Long userId);

    List<Address> findByUser_Id(Long userId);
}
