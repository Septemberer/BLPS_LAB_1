package com.javadevjournal.controller;

import com.javadevjournal.dto.ApartmentDTO;
import com.javadevjournal.jpa.entity.Apartment;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.Offer;
import com.javadevjournal.jpa.entity.Vote;
import com.javadevjournal.security.MyResourceNotFoundException;
import com.javadevjournal.service.repo.ApartmentService;
import com.javadevjournal.service.repo.CustomerService;
import com.javadevjournal.service.repo.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserProfileController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserProfileController.class);
    public static final String DELETE_SUCCESS = "Вы и ваши квартиры успешно удалены из системы";
    public static final String PERMISSION_DENIED = "Хз как так вышло, вы не авторизованы";
    public static final String BANNED = "Вы забанены, у вас не может быть объявлений";
    private final CustomerService customerService;
    private final ApartmentService apartmentService;
    private final VoteService voteService;

    @GetMapping(value = "/users/user/{id}", produces = "application/json")
    public Customer getUserDetail(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @DeleteMapping(value = "/users/delete/me")
    public String deleteMe(HttpServletRequest httpServletRequest) {
        customerService.deleteMe(httpServletRequest);
        return DELETE_SUCCESS;
    }

    @GetMapping(value = "/users/user/all", produces = "application/json")
    public List<Customer> getAllUsers() {
        return customerService.findAll();
    }

    @GetMapping(value = "/users/complaint/{id}")
    public String complaint(HttpServletRequest httpServletRequest,
                            @PathVariable Long id) {
        return customerService.complaint(httpServletRequest, id);
    }

    @GetMapping(value = "/apartment/filter", produces = "application/json")
    public List<Apartment> findApartments(@RequestParam("minPrice") final Long minPrice,
                                          @RequestParam("maxPrice") final Long maxPrice,
                                          @RequestParam("floor") final Integer floor,
                                          @RequestParam("rooms") final Integer rooms) {
        return apartmentService.findApartmentsByFilter(minPrice, maxPrice, floor, rooms);
    }

    @GetMapping(value = "/apartments/my", produces = "application/json")
    public List<Apartment> findMyApartments(HttpServletRequest httpServletRequest) {
        return apartmentService.findMyApartments(customerCheck(httpServletRequest));
    }

    @GetMapping(value = "/apartments/all", produces = "application/json")
    public List<Apartment> findAllApprovedApartments() {
        return apartmentService.findAllApprovedApartments();
    }

    @GetMapping(value = "/apartments/allVoting", produces = "application/json")
    public List<Apartment> findAllUnapprovedApartments() {
        return apartmentService.findAllUnapprovedApartments();
    }


    @PostMapping(value = "/apartment/create", produces = "application/json")
    public Apartment createApartment(HttpServletRequest httpServletRequest,
                                     @RequestBody ApartmentDTO apartmentDTO) {
        return apartmentService.createApartment(apartmentDTO, customerCheck(httpServletRequest));
    }

    private Customer customerCheck(HttpServletRequest httpServletRequest) {
        Optional<Customer> customerOpt = customerService.whoIs(httpServletRequest);
        if (!customerOpt.isPresent()) {
            throw new MyResourceNotFoundException(PERMISSION_DENIED);
        }
        Customer customer = customerOpt.get();
        if (customer.isBanned()) {
            throw new MyResourceNotFoundException(BANNED);
        }
        return customer;
    }

    @GetMapping(value = "/apartment/vote/list")
    public List<Vote> getAllOpenedVotes() {
        return voteService.getAllOpenedVotes();
    }

    @Scheduled(fixedDelay = 10000)
    private void getOpenedVotes() {
        List<Vote> voteList = getAllOpenedVotes();
        log.info("-----Opened Votes-----");
        for (Vote vote : voteList) {
            log.info("ID: {}\n", vote.getId());
            log.info("Players: {}\n", vote.getOfferList().size());
            int i = 1;
            for (Offer offer : vote.getOfferList()) {
                log.info("{}. {} : {}\n", i, offer.getCustomer().getUserName(), offer.getPrice());
                i++;
            }
            log.info("+_+_+_+_+_+_+_+_+_+_+");
        }
    }

}
