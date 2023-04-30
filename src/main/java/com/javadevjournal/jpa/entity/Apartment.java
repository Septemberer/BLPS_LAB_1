package com.javadevjournal.jpa.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@ToString
public class Apartment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "apartment_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer owner;

	@Column(name = "price")
	private Long price;

	@Column(name = "floor")
	private Integer floor;

	@Column(name = "rooms")
	private Integer rooms;

	@Column(name = "address")
	private String address;

	@Column(name = "approved")
	private boolean approved = false;

	@OneToOne
	@JoinColumn(name = "vote_id")
	private Vote vote;

}
