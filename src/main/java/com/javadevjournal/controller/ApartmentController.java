package com.javadevjournal.controller;

import com.javadevjournal.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@AllArgsConstructor
@RequestMapping("/api/apartments")
public class ApartmentController {

	private final CustomerService customerService;

	@GetMapping(value = "/whoIs")
	public String whoIs(HttpServletRequest httpServletRequest) {
		String token = !Objects.equals(httpServletRequest.getHeader(AUTHORIZATION), "") ?
				httpServletRequest.getHeader(AUTHORIZATION) : "";
		token = token.replaceAll("Basic", "").trim();
		String name = customerService.findByToken(token).get().getUsername();
		return String.format("Здесь был : %s", name);
	}
}
