package com.javadevjournal.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VoteDTO {

	private String token;
	private Long voteId;
	private Long price;
}
