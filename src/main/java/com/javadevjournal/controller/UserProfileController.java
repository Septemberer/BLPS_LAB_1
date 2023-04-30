package com.javadevjournal.controller;

import com.javadevjournal.dto.CustomerDTO;
import com.javadevjournal.jpa.Customer;
import com.javadevjournal.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api/users")
public class UserProfileController {

    @Autowired
    private CustomerService customerService;

    @GetMapping(value = "/api/users/user/{id}",produces = "application/json")
    public Customer getUserDetail(@PathVariable Long id){
        return customerService.findById(id);
    }

    @PostMapping(value = "/registration")
    public Customer createNewUser(@RequestBody CustomerDTO customerDTO) {
        return customerService.registration(customerDTO);
    }
}
