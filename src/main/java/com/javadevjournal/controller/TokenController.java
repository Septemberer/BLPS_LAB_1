package com.javadevjournal.controller;

import com.javadevjournal.dto.CustomerDTO;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.service.CustomerService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TokenController {

    private final CustomerService customerService;

    @PostMapping("/token")
    public String getToken(@RequestParam("username") final String username, @RequestParam("password") final String password) {
        String token = customerService.login(username, password);
        if (StringUtils.isEmpty(token)) {
            return "no token found";
        }
        return token;
    }

    @PostMapping(value = "/registration")
    public Customer createNewUser(@RequestBody CustomerDTO customerDTO) {
        return customerService.registration(customerDTO);
    }
}
