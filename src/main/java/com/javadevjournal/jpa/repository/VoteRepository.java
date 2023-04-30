package com.javadevjournal.jpa.repository;

import com.javadevjournal.jpa.entity.Offer;
import com.javadevjournal.jpa.entity.Vote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends CrudRepository<Vote, Long> {

	List<Vote> findAllByOpened(boolean isOpened);

	List<Vote> findAllByOfferListContains(Offer offer);
}