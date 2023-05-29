package com.javadevjournal.controller;

import com.javadevjournal.dto.ApartmentDTO;
import com.javadevjournal.jpa.entity.Apartment;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.Offer;
import com.javadevjournal.jpa.entity.Vote;
import com.javadevjournal.security.MyResourceNotFoundException;
import com.javadevjournal.service.ApartmentService;
import com.javadevjournal.service.CustomerService;
import com.javadevjournal.service.VoteService;
import lombok.AllArgsConstructor;
import lombok.var;
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

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserProfileController {

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
        return "Вы и ваши квартиры успешно удалены из системы";
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
        var customerOpt = customerService.whoIs(httpServletRequest);
        if (!customerOpt.isPresent()) {
            throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
        }
        var customer = customerOpt.get();
        if (customer.isBanned()) {
            throw new MyResourceNotFoundException("Вы забанены, у вас не может быть объявлений");
        }
        return apartmentService.findMyApartments(customer);
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
        var customerOpt = customerService.whoIs(httpServletRequest);
        if (!customerOpt.isPresent()) {
            throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
        }
        var customer = customerOpt.get();
        if (customer.isBanned()) {
            throw new MyResourceNotFoundException("Вы забанены, вам нельзя выставлять квартиры на продажу");
        }
        return apartmentService.createApartment(apartmentDTO, customer);
    }

    @GetMapping(value = "/apartment/vote/list")
    public List<Vote> getAllOpenedVotes() {
        return voteService.getAllOpenedVotes();
    }

    @Scheduled(fixedDelay = 10000)
    private void getOpenedVotes() {
        List<Vote> voteList = getAllOpenedVotes();
        System.out.println("-----Opened Votes-----");
        for (Vote vote : voteList) {
            System.out.printf("---ID: %s\n", vote.getId());
            System.out.printf("---Players: %s\n", vote.getOfferList().size());
            System.out.println("---Names---");
            int i = 1;
            for (Offer offer : vote.getOfferList()) {
                System.out.printf("%s. %s\n", i, offer.getCustomer().getUserName());
            }
            System.out.println("+_+_+_+_+_+_+_+_+_+_+");
        }
    }

}
