package com.javadevjournal.controller;

import com.javadevjournal.service.CustomerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/apartments")
public class ApartmentController {

	@Autowired
	private CustomerService customerService;

	@GetMapping(value = "/whoIs")
	public String whoIs(HttpServletRequest httpServletRequest) {
		String token = StringUtils.isNotEmpty(httpServletRequest.getHeader(AUTHORIZATION)) ?
				httpServletRequest.getHeader(AUTHORIZATION) : "";
		token = StringUtils.removeStart(token, "Bearer").trim();
		String name = customerService.findByToken(token).get().getUsername();
		return String.format("Здесь был : %s", name);
	}
}
