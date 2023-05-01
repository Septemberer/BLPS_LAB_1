package com.javadevjournal.service;

import com.javadevjournal.dto.ApartmentDTO;
import com.javadevjournal.jpa.entity.Apartment;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.Offer;
import com.javadevjournal.jpa.entity.Vote;

import java.util.List;

public interface ApartmentService {

	List<Apartment> findApartmentsByFilter(Long minPrice, Long maxPrice, Integer floor, Integer rooms);

	List<Apartment> findMyApartments(Customer customer);

	List<Apartment> findAllApprovedApartments();

	List<Apartment> findAllUnapprovedApartments();

	void deleteAllByOwner(Customer customer);

	Apartment createApartment(ApartmentDTO apartmentDTO, Customer customer);

	void unApprove(Customer customer);

	void closeVote(Apartment apartment, Long price);

	void save(Apartment apartment);

	void addOfferInVote(Vote vote, Offer offer);

	void makeOffer(Long id, Customer customer, Long price);
}
