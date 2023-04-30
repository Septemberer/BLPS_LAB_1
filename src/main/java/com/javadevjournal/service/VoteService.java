package com.javadevjournal.service;

import com.javadevjournal.jpa.entity.Offer;
import com.javadevjournal.jpa.entity.Vote;

import java.util.List;
import java.util.Optional;

public interface VoteService {

	Vote createVote();

	List<Vote> getAllOpenedVotes();

	void save(Vote vote);

	List<Vote> findAllByOfferListContains(Offer offer);

	Optional<Vote> findById(Long id);
}
