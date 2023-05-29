package com.javadevjournal.service;

import com.javadevjournal.dto.VoteDTO;
import com.javadevjournal.security.MyResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.var;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DemoStringConsumer {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DemoStringConsumer.class);
	private final ApartmentService apartmentService;
	private final CustomerService customerService;

	@KafkaListener(id = "Consumer", topics = "voting.topic")
	public void makeVote(String message) {
		log.info("Received: " + message);
		VoteDTO voteDTO = mapMessage(message);
		var customerOpt = customerService.whoIs(voteDTO.getToken());
		if (!customerOpt.isPresent()) {
			log.info("Вы не авторизованы");
			throw new MyResourceNotFoundException("Вы не авторизованы");
		}
		var customer = customerOpt.get();
		if (customer.isBanned()) {
			log.info("Вы забанены, вам нельзя участвовать в голосовании");
			throw new MyResourceNotFoundException("Вы забанены, вам нельзя участвовать в голосовании");
		}
		if (!customer.isProfessional()) {
			log.info("Вы не имеете права голосовать, вашего опыта еще не достаточно");
		}
		log.info("Пользователь {} участвует в голосовании {} и предлагает цену {}",
				customer.getUserName(), voteDTO.getVoteId(), voteDTO.getPrice());
		apartmentService.makeOffer(voteDTO.getVoteId(), customer, voteDTO.getPrice());
		log.info("Спасибо! Ваш голос учтен!");
	}

	private VoteDTO mapMessage(String message) {
		VoteDTO voteDTO = new VoteDTO();
		try {
			String[] parts = message.split("&&");
			voteDTO.setToken(parts[0]);
			voteDTO.setVoteId(Long.parseLong(parts[1]));
			voteDTO.setPrice(Long.parseLong((parts[2])));
		} catch (Exception e) {
			throw new MyResourceNotFoundException("Не верный формат сообщения принят из топика", e);
		}
		return voteDTO;
	}
}