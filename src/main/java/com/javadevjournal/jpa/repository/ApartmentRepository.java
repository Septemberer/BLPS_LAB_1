package com.javadevjournal.jpa.repository;

import com.javadevjournal.jpa.entity.Apartment;
import com.javadevjournal.jpa.entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepository extends CrudRepository<Apartment, Long> {

	List<Apartment> findAllByPriceBetweenAndFloorAndRooms(Long minPrice,
																 Long maxPrice,
																 Integer floor,
																 Integer rooms);

	List<Apartment> findAllByOwner(Customer owner);

	List<Apartment> findAllByApprovedIsTrue();

	void deleteAllByOwner(Customer customer);
}