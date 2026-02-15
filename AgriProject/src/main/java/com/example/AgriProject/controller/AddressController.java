package com.example.AgriProject.controller;

import com.example.AgriProject.dto.AddressDto;
import com.example.AgriProject.entity.Address;
import com.example.AgriProject.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:63342")
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/address/add/{userId}")
    public AddressDto addAddress(@PathVariable Long userId, @RequestBody AddressDto address){
        return addressService.saveAddress(userId,address);
    }

    @GetMapping("/address/get/{userId}")
    public List<AddressDto> getAddress(@PathVariable Long userId){
        return addressService.getUserAddress(userId);
    }
}
