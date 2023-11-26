package com.javadevjournal.controller;

import com.javadevjournal.dto.CustomerDTO;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.service.repo.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor
public class TokenController {

	public static final String NO_TOKEN_FOUND = "no token found";
	public static final String HELLO_WORLD = "Hello world!!!";
	private final CustomerService customerService;

	@GetMapping("/")
	public String hello() {
		return HELLO_WORLD;
	}

	@PostMapping("/token")
	public String getToken(@RequestParam("username") final String username,
						   @RequestParam("password") final String password) {
		String token = customerService.login(username, password);
		if (Objects.equals(token, "")) {
			return NO_TOKEN_FOUND;
		}
		return token;
	}

	@PostMapping(value = "/registration")
	public Customer createNewUser(@RequestBody CustomerDTO customerDTO) {
		return customerService.registration(customerDTO);
	}

	@GetMapping(value = "/all", produces = "application/json")
	public List<Customer> getAllUsers() {
		return customerService.findAll();
	}
}
