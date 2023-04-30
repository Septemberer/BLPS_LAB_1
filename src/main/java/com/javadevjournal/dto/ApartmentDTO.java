package com.javadevjournal.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApartmentDTO {

	private Long price;
	private Integer floor;
	private String address;

}
