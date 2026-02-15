package com.example.AgriProject.service;

import com.example.AgriProject.dto.AddressDto;
import com.example.AgriProject.entity.Address;
import com.example.AgriProject.entity.User;
import com.example.AgriProject.repository.AddressRepository;
import com.example.AgriProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.eclipse.jdt.internal.compiler.lookup.MostSpecificExceptionMethodBinding;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public AddressDto saveAddress(Long userId, AddressDto addressDto){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));

        Address address= modelMapper.map(addressDto,Address.class);
        address.setUser(user);

        Address saved=addressRepository.save(address);
        return modelMapper.map(saved,AddressDto.class);
    }

    public List<AddressDto> getUserAddress(Long userId){
        return addressRepository.findByUserId(userId)
                .stream()
                .map(address -> modelMapper.map(address,AddressDto.class))
                .toList();
    }
}
